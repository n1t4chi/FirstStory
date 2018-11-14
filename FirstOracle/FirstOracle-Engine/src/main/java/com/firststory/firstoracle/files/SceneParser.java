/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.files.Exceptions.ParseFailedException;
import com.firststory.firstoracle.files.dataParsers.*;
import com.firststory.firstoracle.files.structure.Composite;
import com.firststory.firstoracle.files.structure.Roots;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.PositionableObject2DImpl;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object2D.Terrain2DImpl;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.PositionableObject3DImpl;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.object3D.Terrain3DImpl;
import com.firststory.firstoracle.scene.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.files.ParseUtils.*;

/**
 * @author n1t4chi
 */
public class SceneParser {
    
    public static ScenePair<
        OptimisedRegistrableScene2DImpl,
        OptimisedRegistrableScene3DImpl
    > parseToOptimised( String text ) {
        return new SceneParser().parse(
            text,
            OptimisedRegistrableScene2DImpl::new,
            OptimisedRegistrableScene3DImpl::new
        );
    }
    
    public static ScenePair<
        RegistrableScene2DImpl,
        RegistrableScene3DImpl
    > parseToNonOptimised( String text ) {
        return new SceneParser().parse(
            text,
            RegistrableScene2DImpl::new,
            RegistrableScene3DImpl::new
        );
    }
    
    public <
        S2D extends RegistrableScene2D,
        S3D extends RegistrableScene3D
    > ScenePair< S2D, S3D > parse(
        String text,
        SceneSupplier< Index2D, S2D > scene2dSupplier,
        SceneSupplier< Index3D, S3D > scene3dSupplier
    ) {
        var roots = Roots.parse( text );
    
        var sharedData = new SharedData( roots.find( SHARED_PARAM ) );
        var configuration = new SceneConfiguration( roots.find( CONFIGURATION ) );
        
        return new ScenePair<>(
            parseScene2D( roots, sharedData, configuration, scene2dSupplier ),
            parseScene3D( roots, sharedData, configuration, scene3dSupplier )
        );
    }
    
    private < S2D extends RegistrableScene2D > S2D parseScene2D(
        Roots roots,
        SharedData sharedData,
        SceneConfiguration configuration,
        SceneSupplier< Index2D, S2D > scene2dSupplier
    ) {
        return parseScene(
            roots.find( SCENE_2D ),
            sharedData,
            scene2dSupplier,
            this::toObject2D,
            this::toTerrain2D,
            this::toIndices2D,
            RegistrableScene2D::registerMultipleObjects2D,
            RegistrableScene2D::registerMultipleTerrains2D,
            configuration.getTerrainSize2D(),
            Index2D.id2( -1, -1 ),
            Index2D::max,
            Index2D::leftFits,
            Index2D::increment,
            configuration.getTerrainShift2D()
        );
    }
    
    private List<Index2D> toIndices2D( String text ) {
        return toIndices( text, ParseUtils::toIndex2D );
    }
    
    private List<Index3D> toIndices3D( String text ) {
        return toIndices( text, ParseUtils::toIndex3D );
    }
    
    private < IndexType extends Index > List< IndexType > toIndices(
        String text,
        Function< String, IndexType > parser
    ) {
        return ParseUtils.toList( text ).stream()
            .map( parser )
            .collect( Collectors.toList() )
        ;
    }
    
    private < S3D extends RegistrableScene3D > S3D parseScene3D(
        Roots roots,
        SharedData sharedData,
        SceneConfiguration configuration,
        SceneSupplier< Index3D, S3D > scene3dSupplier
    ) {
        return parseScene(
            roots.find( SCENE_3D ),
            sharedData,
            scene3dSupplier,
            this::toObject3D,
            this::toTerrain3D,
            this::toIndices3D,
            RegistrableScene3D::registerMultipleObjects3D,
            RegistrableScene3D::registerMultipleTerrains3D,
            configuration.getTerrainSize3D(),
            Index3D.id3( -1, -1, -1 ),
            Index3D::max,
            Index3D::leftFits,
            Index3D::increment,
            configuration.getTerrainShift3D()
        );
    }
    
    private <
        Scene,
        IndexType extends Index,
        PositionableObjectType extends PositionableObject< ? , ?, ? >,
        TerrainType extends Terrain< ?, ?, ?, ?, ? >
    > Scene parseScene(
        Composite sceneNode,
        SharedData sharedData,
        SceneSupplier< IndexType, Scene > sceneSupplier,
        BiFunction< SharedData, Composite, PositionableObjectType > toObject,
        BiFunction< SharedData, Composite, TerrainType > toTerrain,
        Function< String, List< IndexType > > toIndices,
        BiConsumer< Scene, Collection< PositionableObjectType > > registerObject,
        TriConsumer< Scene, TerrainType, Collection< IndexType >  > registerTerrain,
        @Nullable IndexType size,
        IndexType negativeOneSize,
        BinaryOperator< IndexType > sumSize,
        BiPredicate< IndexType, IndexType > leftFitsInRight,
        Function< IndexType, IndexType > incrementSize,
        IndexType shift
    ) {
        Collection< PositionableObjectType > objects = sceneNode.findComposite( SCENE_OBJECTS )
            .getComposites().stream()
            .map( composite -> toObject.apply( sharedData, composite ) )
            .collect( Collectors.toList() )
        ;
    
        var terrains = sceneNode.findComposite( SCENE_TERRAINS )
            .getComposites().stream()
            .map( terrain -> Map.entry(
                toTerrain.apply( sharedData, terrain ),
                toIndices.apply( terrain.findValueOrThrow( SCENE_PARAM_INDICES ) )
            ) )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) )
        ;
        
    
        var terrainSize = getTerrainSize(
            size,
            negativeOneSize,
            sumSize,
            leftFitsInRight,
            incrementSize,
            terrains
        );
    
        var scene = sceneSupplier.create( terrainSize, shift );
        terrains.forEach( ( terrainType, indexTypes ) ->
            registerTerrain.accept( scene, terrainType, indexTypes )
        );
        registerObject.accept( scene, objects );
        return scene;
    }
    
    private < IndexType extends Index, TerrainType extends Terrain< ?, ?, ?, ?, ? > > IndexType getTerrainSize(
        @Nullable IndexType size,
        IndexType negativeOneSize,
        BinaryOperator< IndexType > sumSize,
        BiPredicate< IndexType, IndexType > leftFitsInRight,
        Function< IndexType, IndexType > incrementSize,
        Map< TerrainType, List< IndexType > > terrains
    ) {
        var terrainSize = size;
        var minArraySize = incrementSize.apply( terrains.values()
            .stream()
            .flatMap( Collection::stream )
            .reduce( negativeOneSize, sumSize )
        ) ;
        if( terrainSize == null ) {
            terrainSize = minArraySize;
        } else {
            if( !leftFitsInRight.test( minArraySize, terrainSize ) ) {
                throw new ParseFailedException(
                    "Declared terrains do not fit to configured size " + terrainSize + "." +
                    " Minimum size needed: " + minArraySize
                );
            }
        }
        return terrainSize;
    }
    
    private PositionableObject2D<?, ?> toObject2D( SharedData sharedData, Composite node ) {
        return toObject(
            sharedData,
            node,
            this::createObject2dInstance,
            PositionableObject2DImpl.class,
            VerticesParser::setVertices2D,
            TransformationParser::setTransformations2D
        );
    }
    
    private Terrain2D< ? > toTerrain2D( SharedData sharedData, Composite node ) {
        return toTerrain(
            sharedData,
            node,
            this::createTerrain2dInstance,
            Terrain2DImpl.class,
            VerticesParser::setVertices2D,
            PositionCalculatorParser::setCalculator2D
        );
    }
    
    private PositionableObject3D<?, ?> toObject3D( SharedData sharedData, Composite node ) {
        return toObject(
            sharedData,
            node,
            this::createObject3dInstance,
            PositionableObject3DImpl.class,
            VerticesParser::setVertices3D,
            TransformationParser::setTransformations3D
        );
    }
    
    private Terrain3D< ? > toTerrain3D( SharedData sharedData, Composite node ) {
        return toTerrain(
            sharedData,
            node,
            this::createTerrain3dInstance,
            Terrain3DImpl.class,
            VerticesParser::setVertices3D,
            PositionCalculatorParser::setCalculator3D
        );
    }
    
    private < TerrainType extends Terrain< ?, ?, ?, ?, ? > > TerrainType toTerrain(
        SharedData sharedData,
        Composite node,
        BiFunction< SharedData, String, TerrainType > objectCreator,
        Class< ? extends TerrainType > defaultClass,
        TriConsumer< TerrainType, SharedData, String > verticesApplier,
        TriConsumer< TerrainType, SharedData, String > positionCalculatorApplier
    ) {
        var terrain = toGraphicObject(
            sharedData,
            node,
            objectCreator,
            defaultClass,
            verticesApplier
        );
    
        positionCalculatorApplier.accept(
            terrain,
            sharedData,
            node.findValue( SCENE_PARAM_POSITION_CALC,  null )
        );
        
        return terrain;
    }
    
    private < ObjectType extends PositionableObject< ?, ?, ? > > ObjectType toObject(
        SharedData sharedData,
        Composite node,
        BiFunction< SharedData, String, ObjectType > objectCreator,
        Class< ? extends ObjectType > defaultClass,
        TriConsumer< ObjectType, SharedData, String > verticesApplier,
        PentaConsumer< ObjectType, SharedData, String, String, String > transformationApplier
        ) {
        var object = toGraphicObject(
            sharedData,
            node,
            objectCreator,
            defaultClass,
            verticesApplier
        );
        
        transformationApplier.accept(
            object,
            sharedData,
            node.findValue( SCENE_PARAM_POSITION, null ),
            node.findValue( SCENE_PARAM_ROTATION, null ),
            node.findValue( SCENE_PARAM_SCALE, null )
        );
        return object;
    }
    
    private < ObjectType extends GraphicObject< ?, ?, ? > > ObjectType toGraphicObject(
        SharedData sharedData,
        Composite node,
        BiFunction< SharedData, String, ObjectType > objectCreator,
        Class< ? extends ObjectType > defaultClass,
        TriConsumer< ObjectType, SharedData, String > verticesApplier
    ) {
        var object = objectCreator.apply( sharedData, node.findValue( SCENE_PARAM_CLASS, defaultClass.getName() ) );
        TextureParser.setTexture( object, sharedData, node.findValue( SCENE_PARAM_TEXTURE, null ) );
        ColouringParser.setColouring( object, sharedData, node.findValue( SCENE_PARAM_COLOURING, null ) );
        UvMapParser.setUvMap( object, sharedData, node.findValue( SCENE_PARAM_UV_MAP, null ) );
        verticesApplier.accept( object, sharedData, node.findValue( SCENE_PARAM_VERTICES, null ) );
        return object;
    }
    
    private Terrain2D< ? > createTerrain2dInstance( SharedData sharedData, String className ) {
        return createGraphicObjectInstance(
            ClassParser::getTerrainClass2D,
            sharedData,
            SharedData::getTerrainClass2D,
            className
        );
    }
    
    private PositionableObject2D< ?, ? > createObject2dInstance( SharedData sharedData, String className ) {
        return createGraphicObjectInstance(
            ClassParser::getObjectClass2D,
            sharedData,
            SharedData::getObjectClass2D,
            className
        );
    }
    
    private Terrain3D< ? > createTerrain3dInstance( SharedData sharedData, String className ) {
        return createGraphicObjectInstance(
            ClassParser::getTerrainClass3D,
            sharedData,
            SharedData::getTerrainClass3D,
            className
        );
    }
    
    private PositionableObject3D< ?, ? > createObject3dInstance( SharedData sharedData, String className ) {
        return createGraphicObjectInstance(
            ClassParser::getObjectClass3D,
            sharedData,
            SharedData::getObjectClass3D,
            className
        );
    }
    
    private < ObjectT extends GraphicObject< ?, ?, ? > > ObjectT createGraphicObjectInstance(
        ClassParser.ClassProvider< ObjectT > provider,
        SharedData sharedData,
        BiFunction< SharedData, String, Class< ? extends ObjectT > > sharedDataExtractor,
        String className
    ) {
        try {
            var aClass = provider.get(
                sharedData,
                sharedDataExtractor,
                className
            );
            return aClass.getDeclaredConstructor().newInstance();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
    
}