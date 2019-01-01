/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class OptimisedRegistrableScene2DImpl extends RegistrableScene2DImpl {
    
    public OptimisedRegistrableScene2DImpl( int terrainXSize, int terrainYSize, Index2D terrainShift ) {
        super( terrainXSize, terrainYSize, terrainShift );
    }
    
    public OptimisedRegistrableScene2DImpl( Terrain2D< ? >[][] terrains, Index2D terrainShift ) {
        super( terrains, terrainShift );
    }
    
    @Override
    public List< RenderData > getObjects2DRenderData(
        double currentRenderTime,
        double cameraRotation
    ) {
        List< RenderData > list = new ArrayList<>(  );
        
        var terrainShift = getTerrain2DShift();
        var terrains = getTerrains2D();
        var objects2D = getObjects2D();
        
        var view = new CameraView( getScene2DCamera() );
        
        var plane = view.getPlaneForZ( 0 );
        
        var minX = Math.max( 0, plane.minX_Floor() - terrainShift.x() );
        var maxX = Math.min( terrains.length, plane.maxX_Ceil() - terrainShift.x() );
        for ( var x = minX; x < maxX; x++ ) {
            var terrainsY = terrains[ x ];
            var bounds = plane.getBoundsAtX( x + terrainShift.x() );
            var minY = boundValue( terrainsY, bounds.getMin(), terrainShift.y() );
            var maxY = boundValue( terrainsY, bounds.getMax(), terrainShift.y() );
//            System.err.println( x + terrainShift.x() + " @ -> " + bounds );
//            System.err.println( x + " @ -> ( " + minY + ",  " + maxY + " )" );
            for ( var y = minY; y < maxY; y++ ) {
                var terrain = terrainsY[ y ];
                list.addAll( terrain.getRenderData( terrain.computePosition( x, y, terrainShift ), currentRenderTime, cameraRotation ) );
            }
        }
    
        for ( var object : objects2D ) {
            if( view.shouldDisplay( object.getBBO() ) ) {
                list.addAll( object.getRenderData( currentRenderTime, cameraRotation ) );
            }
        }
        return list;
    }
    
    private int boundValue( Terrain2D< ? >[] array, int value, int shift ) {
        var ret = value - shift;
        if( ret < 0 ) {
            return 0;
        } else if ( ret > array.length ) {
            return array.length;
        }
        return ret;
    }
}
