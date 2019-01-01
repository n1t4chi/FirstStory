/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.rendering.RenderData;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public interface SceneTestUtils {
    
    float DIST_BUFFOR = 1;
    
    static Vector3fc vec3( float x, float y, float z ) {
        return new Vector3f( x, y, z );
    }
    
    static Set< RenderData > allObjectsData( Set< MockObject2D > allObjects ) {
        return objectsData( allObjects, renderData -> true );
    }
    
    static Set< RenderData > objectsDataStrictlyInside( Set< MockObject2D > allObjects, float size, float midXY ) {
        return objectsData( allObjects, renderData -> isStrictlyInside2D( renderData, size, midXY ) );
    }
    
    static Set< RenderData > objectsData( Set< MockObject2D > allObjects, Predicate< RenderData > predicate ) {
        return allObjects.stream()
            .map( MockObject2D::getRenderDataDirectly )
            .filter( predicate )
            .collect( Collectors.toSet() );
    }
    
    static Set< RenderData > terrainsDataStrictlyInside( Set< MockTerrain2D > allTerrains, int size, int midXY ) {
        return terrainsData( allTerrains, renderData -> isStrictlyInside2D( renderData, size, midXY ) );
    }
    
    static Set< RenderData > allTerrainsData( Set< MockTerrain2D > allTerrains ) {
        return terrainsData( allTerrains, renderData -> true );
    }
    
    static Set< RenderData > terrainsData( Set< MockTerrain2D > allTerrains, Predicate< RenderData > predicate ) {
        return allTerrains.stream()
            .map( MockTerrain2D::getCurrentRenderData )
            .filter( predicate )
            .collect( Collectors.toSet() );
    }
    
    static boolean isStrictlyInside2D( RenderData data, float width, float XY ){
        return isInside2D( data, width, XY, 0 );
    }
    
    static boolean isInside2D( RenderData data, float width, float XY ){
        return isInside2D( data, width, XY, DIST_BUFFOR );
    }
    
    static boolean isInside2D( RenderData data, float width, float XY, float buffor ){
        var halfWidth = width/2;
        var minX = data.getPosition().x();
        var maxX = data.getPosition().x() + data.getScale().x();
        var minY = data.getPosition().y();
        var maxY = data.getPosition().y() + data.getScale().y();
        var minDim = XY - halfWidth - buffor;
        var maxDim = XY + halfWidth + buffor;
        return FirstOracleConstants.objectWithinBoundary(
            minX, maxX,
            minY, maxY,
            minDim, maxDim,
            minDim, maxDim
        );
    }
    
    static MockObject2D obj2D( float x, float y ) {
        return new MockObject2D( x, x+1, y, y+1 );
    }
    
    static MockTerrain2D terr2D( int x, int y, Index2D shift ) {
        return new MockTerrain2D( x, y , shift );
    }
}
