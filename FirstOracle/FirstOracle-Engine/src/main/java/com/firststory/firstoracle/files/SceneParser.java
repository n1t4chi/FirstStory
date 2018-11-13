/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.files.dataParsers.*;
import com.firststory.firstoracle.files.structure.Composite;
import com.firststory.firstoracle.files.structure.Roots;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.PositionableObject2DImpl;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.PositionableObject3DImpl;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.scene.*;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
    
        var sharedData = new SharedData( roots.find( PARAM_SHARED_DATA ) );
        
        return new ScenePair<>(
            parseScene2D( roots, sharedData, scene2dSupplier ),
            parseScene3D( roots, sharedData, scene3dSupplier )
        );
    }
    
    private < S2D extends RegistrableScene2D > S2D parseScene2D(
        Roots roots,
        SharedData sharedData,
        SceneSupplier< Index2D, S2D > scene2dSupplier
    ) {
        return parseScene(
            roots.find( NAME_SCENE_2D ),
            sharedData,
            scene2dSupplier,
            this::toObject2D,
            this::toTerrain2D,
            RegistrableScene2D::registerMultipleObjects2D,
            RegistrableScene2D::registerMultipleTerrains2D,
            FirstOracleConstants.INDEX_ZERO_2I,
            FirstOracleConstants.INDEX_ZERO_2I
    
        );
    }
    
    private < S3D extends RegistrableScene3D > S3D parseScene3D(
        Roots roots,
        SharedData sharedData,
        SceneSupplier< Index3D, S3D > scene3dSupplier
    ) {
        return parseScene(
            roots.find( NAME_SCENE_3D ),
            sharedData,
            scene3dSupplier,
            this::toObject3D,
            this::toTerrain3D,
            RegistrableScene3D::registerMultipleObjects3D,
            RegistrableScene3D::registerMultipleTerrains3D,
            FirstOracleConstants.INDEX_ZERO_3I,
            FirstOracleConstants.INDEX_ZERO_3I
            
        );
    }
    
    private <
        Scene,
        IndexT extends Index,
        PositionableObjectT extends PositionableObject< ? , ?, ? >,
        TerrainT extends Terrain< ? , ?, ? >
    > Scene parseScene(
        Composite sceneNode,
        SharedData sharedData,
        SceneSupplier< IndexT, Scene > sceneSupplier,
        BiFunction< SharedData, Composite, PositionableObjectT > toObjectT,
        BiFunction< SharedData, Composite, TerrainT > toTerrainT,
        BiConsumer< Scene, Collection< PositionableObjectT > > registerObject,
        TriConsumer< Scene, TerrainT, Collection< IndexT >  > registerTerrain,
        IndexT size,
        IndexT shift

    ) {
        Collection< PositionableObjectT > objects = sceneNode.findComposite( NAME_OBJECTS )
            .getComposites().stream()
            .map( composite -> toObjectT.apply( sharedData, composite ) )
            .collect( Collectors.toList() )
        ;
    
        var s3D = sceneSupplier.create( size, shift );
        registerObject.accept( s3D, objects );
        return s3D;
    }
    
    private PositionableObject2D<?, ?> toObject2D( SharedData sharedData, Composite node ) {
        return toObject(
            sharedData,
            node,
            this::createObject2dInstance,
            PositionableObject2DImpl.class,
            TransformationParser::setTransformations2D,
            VerticesParser::setVertices2D
        );
    }
    
    private Terrain3D< ? > toTerrain3D( SharedData sharedData, Composite node ) {
        return null;
    }
    
    private PositionableObject3D<?, ?> toObject3D( SharedData sharedData, Composite node ) {
        return toObject(
            sharedData,
            node,
            this::createObject3dInstance,
            PositionableObject3DImpl.class,
            TransformationParser::setTransformations3D,
            VerticesParser::setVertices3D
        );
    }
    
    private Terrain2D< ? > toTerrain2D( SharedData sharedData, Composite node ) {
        return null;
    }
    
    private < ObjectT extends PositionableObject< ?, ?, ? > > ObjectT toObject(
        SharedData sharedData,
        Composite node,
        BiFunction< SharedData, String, ObjectT > objectCreator,
        Class< ? extends ObjectT > defaultClass,
        PentaConsumer< ObjectT, SharedData, String, String, String > transformationApplier,
        TriConsumer< ObjectT, SharedData, String > verticesApplier
    ) {
        var object = objectCreator.apply( sharedData, node.findValue( PARAM_OBJECT_CLASS, defaultClass.getName() ) );
        transformationApplier.accept(
            object,
            sharedData,
            node.findValue( PARAM_OBJECT_POSITION, null ),
            node.findValue( PARAM_OBJECT_ROTATION, null ),
            node.findValue( PARAM_OBJECT_SCALE, null )
        );
        TextureParser.setTexture( object, sharedData, node.findValue( PARAM_OBJECT_TEXTURE, null ) );
        ColouringParser.setColouring( object, sharedData, node.findValue( PARAM_OBJECT_COLOURING, null ) );
        UvMapParser.setUvMap( object, sharedData, node.findValue( PARAM_OBJECT_UV_MAP, null ) );
        verticesApplier.accept( object, sharedData, node.findValue( PARAM_OBJECT_VERTICES, null ) );
        return object;
    }
    
    private PositionableObject2D< ?, ? > createObject2dInstance( SharedData sharedData, String className ) {
        return createObjectInstance(
            ClassParser::getObjectClass2D,
            sharedData,
            SharedData::getClass2D,
            className
        );
    }
    
    private PositionableObject3D< ?, ? > createObject3dInstance( SharedData sharedData, String className ) {
        return createObjectInstance(
            ClassParser::getObjectClass3D,
            sharedData,
            SharedData::getClass3D,
            className
        );
    }
    
    private < ObjectT extends PositionableObject< ?, ?, ? > > ObjectT createObjectInstance(
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