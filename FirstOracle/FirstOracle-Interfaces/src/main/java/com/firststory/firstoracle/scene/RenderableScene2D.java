/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author n1t4chi
 */
public interface RenderableScene2D {
    
    Camera2D getScene2DCamera();
    
    Collection< PositionableObject2D< ?, ? > > getObjects2D();
    
    Terrain2D< ? >[][] getTerrains2D();
    
    Index2D getTerrain2DShift();
    
    default List< RenderData> getObjects2DRenderData( double currentRenderTime, double cameraRotation ) {
        List< RenderData > list = new ArrayList<>(  );
    
        var terrainShift = getTerrain2DShift();
        var terrains = getTerrains2D();
        for ( int x = 0, xLength = terrains.length; x < xLength; x++ ) {
            var terrainsY = terrains[ x ];
            for ( int y = 0, yLength = terrainsY.length; y < yLength; y++ ) {
                var terrain = terrainsY[ y ];
                if( terrain != null ) {
                    list.addAll(
                        terrain.getRenderData( terrain.computePosition( x, y, terrainShift ),
                        currentRenderTime,
                        cameraRotation )
                    );
                }
            }
        }
    
        for ( var object : getObjects2D() ) {
            list.addAll( object.getRenderData( currentRenderTime, cameraRotation ) );
        }
        return list;
    }
}
