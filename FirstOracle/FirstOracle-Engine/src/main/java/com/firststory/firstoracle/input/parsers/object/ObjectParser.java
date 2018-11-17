/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.parsers.classes.ObjectClassParser;
import com.firststory.firstoracle.input.parsers.classes.TerrainClassParser;
import com.firststory.firstoracle.input.parsers.parameters.VerticesParser;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;
import com.firststory.firstoracle.object.Vertices;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    VerticesType extends Vertices< PositionType, ? >,
    PositionType extends Position,
    IndexType extends Index,
    ObjectClassParserType extends ObjectClassParser< PositionableObjectType >,
    TerrainClassParserType extends TerrainClassParser< TerrainType >
> {
    
    
    abstract List< IndexType > parseIndex( String text );
    
    abstract ObjectClassParserType getObjectClassParser( SharedData sharedData );
    
    abstract TerrainClassParserType getTerrainClassParser( SharedData sharedData );
    
    abstract Class< ? extends PositionableObjectType > getDefaultObjectClass();
    
    abstract Class< ? extends TerrainType > getDefaultTerrainClass();
    
    abstract void setPositionCalculator(
        TerrainType terrain,
        SharedData sharedData,
        Leaf leaf
    );
    
    abstract void setTransformation(
        PositionableObjectType object,
        SharedData sharedData,
        Leaf position,
        Leaf rotation,
        Leaf scale
    );
    
    abstract VerticesParser< VerticesType, PositionType > getVerticesParser( SharedData sharedData );
    
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
        var object = toGraphicObject(
            sharedData,
            node,
            this::createObjectInstance,
            getDefaultObjectClass(),
            getVerticesParser( sharedData )
        );
    
        setTransformation(
            object,
            sharedData,
            node.findLeaf( SCENE_PARAM_POSITION, null ),
            node.findLeaf( SCENE_PARAM_ROTATION, null ),
            node.findLeaf( SCENE_PARAM_SCALE, null )
        );
        return object;
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
        var terrain = toGraphicObject(
            sharedData,
            node,
            this::createTerrainInstance,
            getDefaultTerrainClass(),
            getVerticesParser( sharedData )
        );
        setPositionCalculator(
            terrain,
            sharedData,
            node.findLeaf( SCENE_PARAM_POSITION_CALC, null )
        );
        return terrain;
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
        VerticesParser< VerticesType, PositionType > verticesParser
    ) {
        var object = objectCreator.apply(
            sharedData,
            node.findValue( SCENE_PARAM_CLASS, defaultClass.getName() )
        );
        
        sharedData.getColouringParser().apply(
            object,
            node.findLeaf( SCENE_PARAM_COLOURING, null )
        );
        sharedData.getTextureParser().apply(
            object,
            node.findLeaf( SCENE_PARAM_TEXTURE, null )
        );
        sharedData.getUvMapParser().apply(
            object,
            node.findLeaf( SCENE_PARAM_UV_MAP, null )
        );
        verticesParser.apply(
            object,
            node.findLeaf( SCENE_PARAM_VERTICES, null )
        );
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
