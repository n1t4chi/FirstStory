/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.objectParsers;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.input.Exceptions.ParseFailedException;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.parameters.ColouringParser;
import com.firststory.firstoracle.input.parameters.TextureParser;
import com.firststory.firstoracle.input.parameters.UvMapParser;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;

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
    ObjectSuperType extends GraphicObject< ?, ?, ? >,
    PositionableObjectType extends PositionableObject< ?, ?, ? >,
    TerrainType extends Terrain< ?, ?, ?, IndexType, ? >,
    IndexType extends Index
> {
    
    abstract List< IndexType > parseIndex( String text );
    
    abstract Class< ? extends PositionableObjectType > extractObjectClass(
        SharedData sharedData,
        String key
    );
    
    abstract Class< ? extends PositionableObjectType > getObjectClass(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends PositionableObjectType > > sharedDataExtractor,
        String className
    );
    
    abstract Class< ? extends TerrainType > getTerrainClass(
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends TerrainType > > sharedDataExtractor,
        String className
    );
    
    abstract Class< ? extends TerrainType > extractTerrainClass(
        SharedData sharedData,
        String key
    );
    
    abstract Class< ? extends PositionableObjectType > getDefaultObjectClass();
    
    abstract Class< ? extends TerrainType > getDefaultTerrainClass();
    
    abstract void setPositionCalculator(
        TerrainType terrain,
        SharedData sharedData2,
        String calculatorName
    );
    
    abstract void setTransformation(
        PositionableObjectType object1,
        SharedData sharedData2,
        String position,
        String rotation,
        String scale
    );
    
    abstract void setVertices(
        ObjectSuperType object,
        SharedData sharedData1,
        String verticesText
    );
    
    @SuppressWarnings( "unchecked" )
    private void setVerticesAfterCast(
        Object object,
        SharedData sharedData1,
        String verticesText
    ) {
        setVertices(
            ( ObjectSuperType ) object,
            sharedData1,
            verticesText
        );
    }
    
    
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
        var object1 = toGraphicObject(
            sharedData,
            node,
            this::createObjectInstance,
            getDefaultObjectClass(),
            this::setVerticesAfterCast
        );
    
        setTransformation(
            object1,
            sharedData,
            node.findValue( SCENE_PARAM_POSITION, null ),
            node.findValue( SCENE_PARAM_ROTATION, null ),
            node.findValue( SCENE_PARAM_SCALE, null )
        );
        return object1;
    }
    
    private TerrainType toTerrain( SharedData sharedData, Composite node ) {
        var terrain = toGraphicObject(
            sharedData,
            node,
            this::createTerrainInstance,
            getDefaultTerrainClass(),
            this::setVerticesAfterCast
        );
    
        setPositionCalculator(
            terrain,
            sharedData,
            node.findValue( SCENE_PARAM_POSITION_CALC, null )
        );
    
        return terrain;
    }
    
    private TerrainType createTerrainInstance(
        SharedData sharedData,
        String className
    ) {
        return createGraphicObjectInstance( getTerrainClass(
            sharedData,
            this::extractTerrainClass,
            className
        ) );
    }
    
    private PositionableObjectType createObjectInstance(
        SharedData sharedData,
        String className
    ) {
        return createGraphicObjectInstance( getObjectClass(
            sharedData,
            this::extractObjectClass,
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
    
    private < ObjectType extends GraphicObject< ?, ?, ? > > ObjectType toGraphicObject(
        SharedData sharedData,
        Composite node,
        BiFunction< SharedData, String, ObjectType > objectCreator,
        Class< ? extends ObjectType > defaultClass,
        TriConsumer< ObjectType, SharedData, String > verticesApplier
    ) {
        var object = objectCreator.apply(
            sharedData,
            node.findValue( SCENE_PARAM_CLASS, defaultClass.getName() )
        );
        TextureParser.setTexture( object,
            sharedData,
            node.findValue( SCENE_PARAM_TEXTURE, null )
        );
        ColouringParser.setColouring( object,
            sharedData,
            node.findValue( SCENE_PARAM_COLOURING, null )
        );
        UvMapParser.setUvMap( object,
            sharedData,
            node.findValue( SCENE_PARAM_UV_MAP, null )
        );
        verticesApplier.accept( object,
            sharedData,
            node.findValue( SCENE_PARAM_VERTICES, null )
        );
        return object;
    }
    
    private < ObjectT extends GraphicObject< ?, ?, ? > > ObjectT createGraphicObjectInstance(
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
