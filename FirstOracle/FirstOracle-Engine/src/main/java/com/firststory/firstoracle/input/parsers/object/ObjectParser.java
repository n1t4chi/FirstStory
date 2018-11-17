/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.input.parsers.classes.ObjectClassParser;
import com.firststory.firstoracle.input.parsers.classes.TerrainClassParser;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.input.ParseUtils.*;

/**
 * Both PositionableObjectType and TerrainType must inherit ObjectType!
 * @author n1t4chi
 */
public abstract class ObjectParser<
    PositionableObjectType extends PositionableObject< ?, ?, ?, ?, ?, ? >,
    TerrainType extends Terrain< ?, ?, ?, ?, ?, ?, IndexType >,
    IndexType extends Index,
    ObjectClassParserType extends ObjectClassParser< PositionableObjectType >,
    TerrainClassParserType extends TerrainClassParser< TerrainType >
> {
    
    abstract List< IndexType > parseIndex( String text );
    
    abstract ObjectClassParserType getObjectClassParser( SharedData sharedData );
    
    abstract TerrainClassParserType getTerrainClassParser( SharedData sharedData );
    
    abstract Class< ? extends PositionableObjectType > getDefaultObjectClass();
    
    abstract Class< ? extends TerrainType > getDefaultTerrainClass();
    
    abstract List< ParameterParser< ? > > getSpecificObjectParsers( SharedData sharedData );
    
    abstract List< ParameterParser< ? > > getSpecificTerrainParsers( SharedData sharedData );
    
    abstract List< ParameterParser< ? > > getSpecificCommonParsers( SharedData sharedData );
    
    public Map< String, TerrainPair< TerrainType, IndexType > > getTerrains(
        Composite sceneNode,
        SharedData sharedData
    ) {
        return getTerrains(
            sceneNode,
            sharedData,
            this::toTerrain,
            this::toIndices
        );
    }
    
    public Map< String, PositionableObjectType > getObjects(
        Composite sceneNode,
        SharedData sharedData
    ) {
        return getObjects(
            sceneNode,
            sharedData,
            this::toObject
        );
    }
    
    private PositionableObjectType toObject( SharedData sharedData, Composite node ) {
        return toGraphicObject(
            sharedData,
            node,
            this::createObjectInstance,
            getDefaultObjectClass(),
            getObjectParsers( sharedData )
        );
    }
    
    private PriorityQueue< ParameterParser< ? > > getObjectParsers( SharedData sharedData ) {
        var list = getCommonParsers( sharedData );
        list.addAll( getSpecificObjectParsers( sharedData ) );
        return new PriorityQueue<>( list );
    }
    
    private PriorityQueue< ParameterParser< ? > > getTerrainParsers( SharedData sharedData ) {
        var list = getCommonParsers( sharedData );
        list.addAll( getSpecificTerrainParsers( sharedData ) );
        return new PriorityQueue<>( list );
    }
    
    private List< ParameterParser< ? > > getCommonParsers( SharedData sharedData ) {
        var list = new ArrayList< ParameterParser< ? >>();
        list.add( sharedData.getUvMapParser() );
        list.add( sharedData.getColouringParser() );
        list.add( sharedData.getTextureParser() );
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
    
    private TerrainType toTerrain( SharedData sharedData, Composite node ) {
        return toGraphicObject(
            sharedData,
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
    
    private Map< String, TerrainPair< TerrainType, IndexType > > getTerrains(
        Composite sceneNode,
        SharedData sharedData,
        BiFunction< SharedData, Composite, TerrainType > toTerrain,
        Function< String, List< IndexType > > toIndices
    ) {
        return sceneNode.findComposite( SCENE_TERRAINS ).getComposites().stream().map( terrain -> Map.entry(
            terrain.getName(),
            new TerrainPair<>(
                toTerrain.apply( sharedData, terrain ),
                toIndices.apply( terrain.findValueOrThrow( SCENE_PARAM_INDICES ) )
            )
        ) ).collect( Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue
        ) );
    }
    
    private Map< String, PositionableObjectType > getObjects(
        Composite sceneNode,
        SharedData sharedData,
        BiFunction< SharedData, Composite, PositionableObjectType > toObject
    ) {
        return sceneNode.findComposite( SCENE_OBJECTS ).getComposites().stream().map( composite -> Map.entry(
            composite.getName(),
            toObject.apply(
                sharedData,
               composite
            )
        ) ).collect( Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue
        ) );
    }
    
    private <ObjectType extends GraphicObject< ?, ?, ?, ?, ?, ?> > ObjectType toGraphicObject(
        SharedData sharedData,
        Composite node,
        BiFunction< SharedData, String, ObjectType > objectCreator,
        Class< ? extends ObjectType > defaultClass,
        PriorityQueue< ParameterParser< ? > > parsers
    ) {
        var object = objectCreator.apply(
            sharedData,
            node.findValue( SCENE_PARAM_CLASS, defaultClass.getName() )
        );
        
        ParameterParser< ? > parser;
        while( ( parser = parsers.poll() ) != null ) {
            parser.tryToApply( object, node );
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
        return ParseUtils.toList( text ).stream().map( this::parseIndex ).flatMap( Collection::stream ).collect( Collectors.toList() );
    }
}
