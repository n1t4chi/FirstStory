/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class OptimisedRegistrableScene3DImpl extends RegistrableScene3DImpl {

    public OptimisedRegistrableScene3DImpl( Index3D terrainSize, Index3D terrainShift ) {
        super( terrainSize, terrainShift );
    }
    
    public OptimisedRegistrableScene3DImpl( Terrain3D< ? >[][][] terrain, Index3D terrainShift ) {
        super( terrain, terrainShift );
    }
    
    @Override
    public List< RenderData > getObjects3DRenderData( double currentRenderTime, double cameraRotation ) {
        List< RenderData > list = new ArrayList<>(  );
    
        var terrainShift = getTerrain3DShift();
        var terrains = getTerrains3D();
    
        var view = new CameraView( getScene3DCamera() );
    
        var yLength = getTerrainSize().y();
        
        for( var y = 0; y < yLength ; y++ ) {
            var plane = view.getPlaneForY( y );
            
            var minX = boundValue( plane.minDim1_Floor(), terrains, terrainShift.x() );
            var maxX = boundValue( plane.maxDim1_Ceil(), terrains, terrainShift.x() );
            
            for( var x = minX; x < maxX ; x++ ) {
                var bounds = plane.getBoundsAtDim1( x );
    
                var terrainsZ = terrains[ x ][ y ];
                var minZ = boundValue( bounds.getMin(), terrainsZ, terrainShift.z() );
                var maxZ = boundValue( bounds.getMax(), terrainsZ, terrainShift.z() );
                
                for( var z = minZ; z < maxZ ; z++ ) {
                    var terrain = terrainsZ[ z ];
                    if( terrain != null ) {
                        list.addAll( terrain.getRenderData(
                            terrain.computePosition( x, y, z, terrainShift ),
                            currentRenderTime,
                            cameraRotation )
                        );
                    }
                }
            }
        }
    
        for ( var object : getObjects3D() ) {
            if( view.shouldDisplay( object.getBBO() ) ) {
                list.addAll( object.getRenderData( currentRenderTime, cameraRotation ) );
            }
        }
        return list;
    }
    
    private int boundValue( int value, Object[] array, int shift ) {
        var ret = value - shift;
        if( ret < 0 ) {
            return 0;
        } else if ( ret > array.length ) {
            return array.length;
        }
        return ret;
    }
}
