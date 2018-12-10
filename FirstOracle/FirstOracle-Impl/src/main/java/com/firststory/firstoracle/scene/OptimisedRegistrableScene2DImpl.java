/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.*;

/**
 * @author n1t4chi
 */
public class OptimisedRegistrableScene2DImpl extends RegistrableScene2DImpl {
    
    private Position2DCalculator calculator;
    
    public OptimisedRegistrableScene2DImpl( Index2D terrainSize, Index2D terrainShift ) {
        super( terrainSize, terrainShift );
    }
    
    public OptimisedRegistrableScene2DImpl( Terrain2D< ?, ? >[][] terrains, Index2D terrainShift ) {
        super( terrains, terrainShift );
        findPositionCalculator( terrains );
    }
    
    @Override
    public void registerTerrain2D( Terrain2D< ?, ? > terrain, Index2D index ) {
        calculator = terrain.getPositionCalculator();
        super.registerTerrain2D( terrain, index );
    }
    
    @Override
    public List< RenderData > getObjects2DRenderData( double currentRenderTime, double cameraRotation ) {
        List< RenderData > list = new ArrayList<>(  );
        
        var shift = getTerrain2DShift();
        var terrainsXY = getTerrains2D();
        var objects2D = getObjects2D();
        
        var view = new CameraView2D( getScene2DCamera() );
        var plane = view.getPlaneForDimOther( 0 );
    
        var minPosX = plane.minDim1_Floor();
        var maxPosX = plane.maxDim1_Ceil();
        var minUnboundedIndexX = calculator.xPositionToIndex( minPosX, shift ) - 2;
        var maxUnboundedIndexX = calculator.xPositionToIndex( maxPosX, shift ) + 2;
        
        if( maxUnboundedIndexX >= 0 || minUnboundedIndexX  < terrainsXY.length ) {
            var minIndexX = boundValue( minUnboundedIndexX, terrainsXY );
            var maxIndexX = boundValue( maxUnboundedIndexX, terrainsXY );
            
            for ( var indexX = minIndexX; indexX <= maxIndexX; indexX++ ) {
                var terrainsY = terrainsXY[ indexX ];
                var posX = calculator.xIndexToPosition( indexX, shift );
                var bounds1 = plane.getBoundsAtDim1( posX-1 );
                var bounds2 = plane.getBoundsAtDim1( posX+1 );
                if( bounds1 != null || bounds2 != null ) {
                    if( bounds1 == null ) {
                        bounds1 = bounds2;
                    }
                    if( bounds2 == null ) {
                        bounds2 = bounds1;
                    }
                    var minPosY = Math.min( bounds1.getMin(), bounds2.getMin() );
                    var maxPosY = Math.max( bounds1.getMax(), bounds2.getMax() );
                    var minUnboundedIndexY = calculator.yPositionToIndex( minPosY, shift ) - 2;
                    var maxUnboundedIndexY = calculator.yPositionToIndex( maxPosY, shift ) + 2;
                    if( maxUnboundedIndexY >= 0 || minUnboundedIndexY  < terrainsY.length ) {
                        var minIndexY = boundValue( minUnboundedIndexY, terrainsY );
                        var maxIndexY = boundValue( maxUnboundedIndexY, terrainsY );
                        for ( var indexY = minIndexY; indexY <= maxIndexY; indexY++ ) {
                            var terrain = terrainsY[ indexY ];
                            if( terrain != null ) {
                                list.addAll( terrain.getRenderData(
                                    terrain.indexToPosition( indexX, indexY, shift ),
                                    currentRenderTime,
                                    cameraRotation )
                                );
                            }
                        }
                    }
                }
            }
        }
    
        for ( var object : objects2D ) {
            if( view.shouldDisplay( object.getBBO() ) ) {
                list.addAll( object.getRenderData( currentRenderTime, cameraRotation ) );
            }
        }
        return list;
    }
    
    private int boundValue( int value, Object[] array ) {
        if( value < 0 ) {
            return 0;
        } else if ( value > array.length-1 ) {
            return array.length-1;
        }
        return value;
    }
    
    private void findPositionCalculator( Terrain2D< ?, ? >[][] terrains ) {
        for ( var terrainsY : terrains ) {
            for ( var terrain : terrainsY ) {
                if( terrain != null ) {
                    calculator = terrain.getPositionCalculator();
                    break;
                }
            }
        }
    }
}
