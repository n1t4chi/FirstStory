/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.input.parsers.classes.*;
import com.firststory.firstoracle.input.structure.*;
import com.firststory.firstoracle.object.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.input.ParseUtils.*;

/**
 * @author n1t4chi
 */
public abstract class ObjectParser<
    PositionType extends Position,
    IndexType extends Index,
    PositionableObjectType extends PositionableObject< PositionType, ?, ?, ?, ?, ? >,
    TerrainType extends Terrain< PositionType, ?, ?, ?, ?, ?, IndexType, ? >,
    ObjectClassParserType extends ObjectClassParser< PositionableObjectType >,
    TerrainClassParserType extends TerrainClassParser< TerrainType >
> {
    
    abstract List< IndexType > parseIndices( String text );
    
    abstract PositionType parsePosition( String text );
    
    abstract String positionToString( PositionType position );
    
    abstract ObjectClassParserType getObjectClassParser( SharedData sharedData );
    
    abstract TerrainClassParserType getTerrainClassParser( SharedData sharedData );
    
    abstract Class< ? extends PositionableObjectType > getDefaultObjectClass();
    
    abstract Class< ? extends TerrainType > getDefaultTerrainClass();
    
    abstract List< ParameterParser< ?, ? > > getSpecificObjectParsers( SharedData sharedData );
    
    abstract List< ParameterParser< ?, ? > > getSpecificTerrainParsers( SharedData sharedData );
    
    abstract List< ParameterParser< ?, ? > > getSpecificCommonParsers( SharedData sharedData );
    
    abstract SharedObjectsParser getSharedTerrainsParser( SharedObjects sharedObjects );
    
    abstract SharedObjectsParser getSharedObjectsParser( SharedObjects sharedObjects );
    
    public PositionableObjectType toObject( SharedData sharedData, SharedObjects sharedObjects, Composite node ) {
        return toGraphicObject(
            sharedData,
            getSharedObjectsParser( sharedObjects ),
            node,
            this::createObjectInstance,
            getDefaultObjectClass(),
            getObjectParsers( sharedData )
        );
    }
    
    private PriorityQueue< ParameterParser< ?, ? > > getObjectParsers( SharedData sharedData ) {
        var list = getCommonParsers( sharedData );
        list.addAll( getSpecificObjectParsers( sharedData ) );
        return new PriorityQueue<>( list );
    }
    
    private PriorityQueue< ParameterParser< ?, ? > > getTerrainParsers( SharedData sharedData ) {
        var list = getCommonParsers( sharedData );
        list.addAll( getSpecificTerrainParsers( sharedData ) );
        return new PriorityQueue<>( list );
    }
    
    private List< ParameterParser< ?, ? > > getCommonParsers( SharedData sharedData ) {
        var list = new ArrayList< ParameterParser< ?, ? > >();
        list.add( sharedData.getUvMapParser() );
        list.add( sharedData.getColouringParser() );
        list.add( sharedData.getTextureParser() );
        list.add( sharedData.getDirectionControllerParser() );
        list.add( sharedData.getFrameControllerParser() );
        list.addAll( getSpecificCommonParsers( sharedData ) );
        return list;
    }
    
    private Class< ? extends PositionableObjectType > getObjectClass(
        SharedData sharedData,
        String className
    ) {
        return getObjectClassParser( sharedData ).parse( className );
    }
    
    private Class< ? extends TerrainType > getTerrainClass(
        SharedData sharedData,
        String className
    ) {
        return getTerrainClassParser( sharedData ).parse( className );
    }
    
    private TerrainType toTerrain( SharedData sharedData, SharedObjects sharedObjects, Composite node ) {
        return toGraphicObject(
            sharedData,
            getSharedTerrainsParser( sharedObjects ),
            node,
            this::createTerrainInstance,
            getDefaultTerrainClass(),
            getTerrainParsers( sharedData )
        );
    }
    
    
    private TerrainType createTerrainInstance(
        SharedData sharedData,
        String className
    ) {
        return createGraphicObjectInstance( getTerrainClass(
            sharedData,
            className
        ) );
    }
    
    private PositionableObjectType createObjectInstance(
        SharedData sharedData,
        String className
    ) {
        return createGraphicObjectInstance( getObjectClass(
            sharedData,
            className
        ) );
    }
    
    public Map< String, TerrainPair< TerrainType, IndexType > > getTerrains(
        SharedData sharedData,
        SharedObjects sharedObjects,
        Composite sceneNode
    ) {
        return sceneNode.findComposite( SCENE_TERRAINS ).getComposites().stream().map( terrain -> Map.entry(
            terrain.getName(),
            new TerrainPair<>(
                toTerrain( sharedData, sharedObjects, terrain ),
                toIndices( terrain.findValueOrThrow( SCENE_PARAM_INDICES ) )
            )
        ) ).collect( Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue
        ) );
    }
    
    public Map< String, List< PositionableObjectType > > getObjects(
        SharedData sharedData,
        SharedObjects sharedObjects,
        Composite sceneNode
    ) {
        return sceneNode.findComposite( SCENE_OBJECTS ).getComposites().stream()
            .map( composite ->
                new ObjectPair<>(
                    composite,
                    toPositions( composite.findValue( SCENE_PARAM_POSITIONS, "[]" ) )
                )
            )
            .map( pair -> {
                var positions = pair.getPositions();
                var node = pair.getObjectNode();
                List< Composite > parsedNodes;
                
                if( positions.isEmpty() ) {
                    parsedNodes = List.of( node );
                } else {
                    parsedNodes = positions.stream()
                        .map( position -> new MutableComposite(
                            node.getName(),
                            node.getContent(),
                            List.of( new Leaf( ParseUtils.SCENE_PARAM_POSITION, positionToString( position )  ) )
                        ) )
                        .collect( Collectors.toList());
                }
                
                return Map.entry( node.getName(), parsedNodes.stream()
                    .map( parsedNode -> toObject(
                        sharedData,
                        sharedObjects,
                        parsedNode
                    ) )
                    .collect( Collectors.toList() )
                );
            } ).collect( Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ) );
        
    }
    
    private <ObjectType extends GraphicObject< ?, ?, ?, ?, ?, ?> > ObjectType toGraphicObject(
        SharedData sharedData,
        SharedObjectsParser sharedObjectsParser,
        Composite node,
        BiFunction< SharedData, String, ObjectType > objectCreator,
        Class< ? extends ObjectType > defaultClass,
        PriorityQueue< ParameterParser< ?, ? > > parsers
    ) {
        var parsedNode = sharedObjectsParser.parseNode( node );
        var object = objectCreator.apply(
            sharedData,
            parsedNode.findValue( SCENE_PARAM_CLASS, defaultClass.getName() )
        );
        
        ParameterParser< ?, ? > parser;
        while( ( parser = parsers.poll() ) != null ) {
            parser.tryToApply( object, parsedNode );
        }
        return object;
    }
    
    private < ObjectT extends GraphicObject< ?, ?, ?, ?, ?, ?> > ObjectT createGraphicObjectInstance(
        Class< ObjectT > aClass
    ) {
        try {
            return aClass.getDeclaredConstructor().newInstance();
        } catch ( Exception e ) {
            throw new ParseFailedException( e );
        }
    }
    
    private List< IndexType > toIndices( String text ) {
        return ParseUtils.toList( text ).stream()
            .map( this::parseIndices )
            .flatMap( Collection::stream )
            .collect( Collectors.toList() )
        ;
    }
    
    private List< PositionType > toPositions( String text ) {
        return ParseUtils.toList( text ).stream()
            .map( this::parsePosition )
            .collect( Collectors.toList() )
        ;
    }
}
