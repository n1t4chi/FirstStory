/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.rendering.RenderData;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.IntStream;

import static com.firststory.firstoracle.scene.SceneTestUtils.*;

class OptimisedRegistrableScene2DImplTest {
    
    /*
        Scene -> objects [0,20]x[0,20]
        Scene -> terrains [5,15]x[5,15]
     */
    private static final int MIN_TER_XY = 5;
    private static final Index2D TERRAIN_SHIFT = Index2D.id2( MIN_TER_XY, MIN_TER_XY );
    private static final int MAX_TER_XY = 15;
    
    private static final int SIZE_TER_XY = MAX_TER_XY - MIN_TER_XY;
    private static final int MID_TER_XY = ( MIN_TER_XY + MAX_TER_XY ) / 2;
    
    private static final int MIN_OBJ_XY = 0;
    private static final int MAX_OBJ_XY = 20;
    
    private static final int SIZE_OBJ_XY = MAX_OBJ_XY - MIN_OBJ_XY;
    private static final int MID_OBJ_XY = ( MIN_OBJ_XY + MAX_OBJ_XY ) / 2;
    
    private final OptimisedRegistrableScene2DImpl instance = new OptimisedRegistrableScene2DImpl(
        Index2D.id2( SIZE_TER_XY, SIZE_TER_XY ),
        TERRAIN_SHIFT
    );
    
    private final Set< MockObject2D > allObjects = new HashSet<>(  );
    private final Set< MockTerrain2D > allTerrains = new HashSet<>(  );
    
    @BeforeEach
    void setUp() {
        
        IntStream.range( MIN_OBJ_XY, MAX_OBJ_XY ).forEach( x ->
            IntStream.range( MIN_OBJ_XY, MAX_OBJ_XY ).forEach( y -> {
                    var object = obj2D( x, y );
                    instance.registerObject2D( object );
                    allObjects.add( object );
                }
            )
        );
        IntStream.range( 0, SIZE_TER_XY ).forEach( x ->
            IntStream.range( 0, SIZE_TER_XY ).forEach( y -> {
                var terr = terr2D( x, y, TERRAIN_SHIFT );
                instance.registerTerrain2D( terr, x, y );
                allTerrains.add( terr );
            } )
        );
    }
    
    /**
     * Camera [0, 20]x[0,20]
     */
    @Test
    void givenCameraDisplayingAllObjects_returnsThemAll() {
        instance.setScene2DCamera( new MockCamera2D( SIZE_OBJ_XY, MID_TER_XY, 0 ) );
        var objects2DRenderData = instance.getObjects2DRenderData( 0, 0 );
        
        var allObjectsData = allObjectsData();
        var allTerrainsData = allTerrainsData();
        Assertions.assertEquals( allObjects.size() + allTerrains.size(), objects2DRenderData.size() );
        Assertions.assertTrue( objects2DRenderData.containsAll( allObjectsData ) );
        Assertions.assertTrue( objects2DRenderData.containsAll( allTerrainsData ) );
    }
    
    /**
     * Camera [5, 15]x[5, 15]
     */
    @Test
    void givenCameraDisplayingOnlyTerrainPart_returnsAboutTwiceTheTerrains() {
        instance.setScene2DCamera( new MockCamera2D( SIZE_TER_XY, MID_OBJ_XY, 0 ) );
        var objects2DRenderData = instance.getObjects2DRenderData( 0, 0 );
    
        var terrainsData = terrainsDataStrictlyInside( SIZE_TER_XY, MID_OBJ_XY );
        var objectsData = objectsDataStrictlyInside( SIZE_TER_XY, MID_OBJ_XY );
        Assertions.assertTrue( objects2DRenderData.size() >= ( terrainsData.size() + objectsData.size() ) );
        Assertions.assertTrue( objects2DRenderData.containsAll( terrainsData ) );
        Assertions.assertTrue( objects2DRenderData.containsAll( objectsData ) );
        objects2DRenderData.forEach( renderData -> {
            Assertions.assertTrue( isInside2D( renderData, SIZE_TER_XY, MID_OBJ_XY ) );
        } );
    }
    
    /**
     * Camera [7.5, 12.5]x[7.5, 12.5]
     */
    @Test
    void givenCameraDisplayingQuarterOfTerrains_returnsAboutHalfOfTerrains() {
        var size = SIZE_TER_XY / 2.0f;
        instance.setScene2DCamera( new MockCamera2D( size, MID_OBJ_XY, 0 ) );
        var objects2DRenderData = instance.getObjects2DRenderData( 0, 0 );
        
        var terrainsData = terrainsDataStrictlyInside( size, MID_OBJ_XY );
        var objectsData = objectsDataStrictlyInside( size, MID_OBJ_XY );
        Assertions.assertTrue( objects2DRenderData.size() >= ( terrainsData.size() + objectsData.size() ), "" +
            "was " + objects2DRenderData.size() + " should be at least " + ( terrainsData.size() + objectsData.size() )
        );
        Assertions.assertTrue( objects2DRenderData.containsAll( terrainsData ) );
        Assertions.assertTrue( objects2DRenderData.containsAll( objectsData ) );
        objects2DRenderData.forEach( renderData -> {
            Assertions.assertTrue( isInside2D( renderData, size, MID_OBJ_XY ) );
        } );
    }
    
    private Set< RenderData > allObjectsData() {
        return SceneTestUtils.allObjectsData( allObjects );
    }
    
    private Set< RenderData > objectsDataStrictlyInside( float size, float midXY ) {
        return SceneTestUtils.objectsDataStrictlyInside( allObjects, size, midXY );
    }
    
    private Set< RenderData > terrainsDataStrictlyInside( float size, float midXY ) {
        return terrainsData( allTerrains, renderData -> isStrictlyInside2D( renderData, size, midXY ) );
    }
    
    private Set< RenderData > allTerrainsData() {
        return SceneTestUtils.allTerrainsData( allTerrains );
    }
    
}