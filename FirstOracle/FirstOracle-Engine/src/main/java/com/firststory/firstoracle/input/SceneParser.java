/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.input.Exceptions.ParseFailedException;
import com.firststory.firstoracle.input.objectParsers.ObjectParser;
import com.firststory.firstoracle.input.objectParsers.ObjectParser2D;
import com.firststory.firstoracle.input.objectParsers.ObjectParser3D;
import com.firststory.firstoracle.input.objectParsers.TerrainPair;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.Roots;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.Terrain;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.scene.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.*;

import static com.firststory.firstoracle.input.ParseUtils.*;

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
            configuration.getTerrainSize2D(),
            configuration.getTerrainShift2D(),
            scene2dSupplier,
            new ObjectParser2D(),
            RegistrableScene2D::registerMultipleObjects2D,
            RegistrableScene2D::registerMultipleTerrains2D,
            this::getTerrainSize2D
        );
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
            configuration.getTerrainSize3D(),
            configuration.getTerrainShift3D(),
            scene3dSupplier,
            new ObjectParser3D(),
            RegistrableScene3D::registerMultipleObjects3D,
            RegistrableScene3D::registerMultipleTerrains3D,
            this::getTerrainSize3D
        );
    }
    
    private <
        Scene,
        IndexType extends Index,
        PositionableObjectType extends PositionableObject< ?, ?, ? >,
        TerrainType extends Terrain< ?, ?, ?, IndexType, ? >
    > Scene parseScene(
        Composite sceneNode,
        SharedData sharedData,
        @Nullable IndexType size,
        IndexType shift,
        SceneSupplier< IndexType, Scene > sceneSupplier,
        ObjectParser< ?, PositionableObjectType, TerrainType, IndexType > parser,
        BiConsumer< Scene, Collection< PositionableObjectType > > registerObject,
        TriConsumer< Scene, TerrainType, Collection< IndexType >  > registerTerrain,
        BiFunction< IndexType, Collection< TerrainPair< TerrainType, IndexType > >, IndexType > determineFinalSize
    ) {
        var objects = parser.getObjects( sceneNode, sharedData );
        var terrains = parser.getTerrains( sceneNode, sharedData );
        var terrainSize = determineFinalSize.apply( size, terrains.values() );
    
        var scene = sceneSupplier.create( terrainSize, shift );
        terrains.forEach( ( name, pair ) ->
            registerTerrain.accept( scene, pair.getTerrain(), pair.getIndices() )
        );
        registerObject.accept( scene, objects.values() );
        return scene;
    }
    
    private Index2D getTerrainSize2D(
        @Nullable Index2D size,
        Collection< TerrainPair< Terrain2D< ? >, Index2D > > terrains
    ) {
        return getTerrainSize(
            size,
            terrains,
            Index2D.id2( -1, -1 ),
            Index2D::max,
            Index2D::leftFits,
            Index2D::increment
        );
    }
    
    private Index3D getTerrainSize3D(
        @Nullable Index3D size,
        Collection< TerrainPair< Terrain3D< ? >, Index3D > > terrains
    ) {
        return getTerrainSize(
            size,
            terrains,
            Index3D.id3( -1, -1, -1 ),
            Index3D::max,
            Index3D::leftFits,
            Index3D::increment
        );
    }
    
    private < IndexType extends Index, TerrainType extends Terrain< ?, ?, ?, IndexType, ? > > IndexType getTerrainSize(
        @Nullable IndexType size,
        Collection< TerrainPair< TerrainType, IndexType > > terrains,
        IndexType negativeOneSize,
        BinaryOperator< IndexType > sumSize,
        BiPredicate< IndexType, IndexType > leftFitsInRight,
        Function< IndexType, IndexType > incrementSize
    ) {
        var terrainSize = size;
        var minArraySize = incrementSize.apply( terrains
            .stream()
            .map( TerrainPair::getIndices )
            .flatMap( Collection::stream )
            .reduce( negativeOneSize, sumSize )
        );
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
}