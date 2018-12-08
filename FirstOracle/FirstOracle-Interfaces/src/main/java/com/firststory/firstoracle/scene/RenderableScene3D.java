/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.*;

/**
 * @author n1t4chi
 */
public interface RenderableScene3D {
    
    Camera3D getScene3DCamera();
    
    Collection< PositionableObject3D< ?, ? > > getObjects3D();
    
    Terrain3D< ?, ? >[][][] getTerrains3D();
    
    Index3D getTerrain3DShift();
    
    default List< RenderData> getObjects3DRenderData( double currentRenderTime, double cameraRotation ) {
        List< RenderData > list = new ArrayList<>(  );
        
        var terrainShift = getTerrain3DShift();
        var terrains = getTerrains3D();
        for ( int x = 0, xLength = terrains.length; x < xLength; x++ ) {
            var terrainsYZ = terrains[ x ];
            for ( int y = 0, yLength = terrainsYZ.length; y < yLength; y++ ) {
                var terrainsZ = terrainsYZ[ y ];
                for ( int z = 0, zLength = terrainsZ.length; z < zLength; z++ ) {
                    var terrain = terrainsZ[ z ];
                    if( terrain != null ) {
                        list.addAll( terrain.getRenderData(
                            terrain.getPositionCalculator().indexToPosition( x, y, z, terrainShift ),
                            currentRenderTime,
                            cameraRotation )
                        );
                    }
                }
            }
        }
        
        for ( var object : getObjects3D() ) {
            list.addAll( object.getRenderData( currentRenderTime, cameraRotation ) );
        }
        return list;
    }
}
