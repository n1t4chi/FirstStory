/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.*;

/**
 * @author n1t4chi
 */
public class OptimisedRegistrableScene3DImpl extends RegistrableScene3DImpl {
    
    private Position3DCalculator calculator;

    public OptimisedRegistrableScene3DImpl( Index3D terrainSize, Index3D terrainShift ) {
        super( terrainSize, terrainShift );
    }
    
    public OptimisedRegistrableScene3DImpl( Terrain3D< ?, ? >[][][] terrains, Index3D terrainShift ) {
        super( terrains, terrainShift );
        findPositionCalculator( terrains );
    }
    
    @Override
    public void registerTerrain3D( Terrain3D< ?, ? > terrain, Index3D index ) {
        calculator = terrain.getPositionCalculator();
        super.registerTerrain3D( terrain, index );
    }
    
    @Override
    public List< RenderData > getObjects3DRenderData( double currentRenderTime, double cameraRotation ) {
        List< RenderData > list = new ArrayList<>(  );
    
        var shift = getTerrain3DShift();
        var terrainsXYZ = getTerrains3D();
        var objects2D = getObjects3D();
    
        var view = new CameraView( getScene3DCamera() );
        var plane = view.getPlaneForZ( 0 );
    
        var minPosX = plane.minDim1_Floor();
        var maxPosX = plane.maxDim1_Ceil();
        var minUnboundedIndexX = calculator.xPositionToIndex( minPosX, shift ) - 2;
        var maxUnboundedIndexX = calculator.xPositionToIndex( maxPosX, shift ) + 2;
    
        if( maxUnboundedIndexX >= 0 || minUnboundedIndexX  < terrainsXYZ.length ) {
            var minIndexX = boundValue( minUnboundedIndexX, terrainsXYZ );
            var maxIndexX = boundValue( maxUnboundedIndexX, terrainsXYZ );
        
            for ( var indexX = minIndexX; indexX <= maxIndexX; indexX++ ) {
                var terrainsYZ = terrainsXYZ[ indexX ];
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
                    if( maxUnboundedIndexY >= 0 || minUnboundedIndexY  < terrainsXYZ.length ) {
                        var minIndexY = boundValue( minUnboundedIndexY, terrainsYZ );
                        var maxIndexY = boundValue( maxUnboundedIndexY, terrainsYZ );
                        for ( var indexY = minIndexY; indexY <= maxIndexY; indexY++ ) {
                            var terrainsZ = terrainsYZ[ indexY ];
                            var posY = calculator.yIndexToPosition( indexY, shift );
                            var bounds3 = plane.getBoundsAtDim1( posY-1 );
                            var bounds4 = plane.getBoundsAtDim1( posY+1 );
                            if( bounds3 != null || bounds4 != null ) {
                                if( bounds3 == null ) {
                                    bounds3 = bounds4;
                                }
                                if( bounds4 == null ) {
                                    bounds4 = bounds3;
                                }
                                var minPosZ = Math.min( bounds3.getMin(), bounds4.getMin() );
                                var maxPosZ = Math.max( bounds3.getMax(), bounds4.getMax() );
                                var minUnboundedIndexZ = calculator.zPositionToIndex( minPosZ, shift ) - 2;
                                var maxUnboundedIndexZ = calculator.zPositionToIndex( maxPosZ, shift ) + 2;
                                if( maxUnboundedIndexZ >= 0 || minUnboundedIndexZ  < terrainsZ.length ) {
                                    var minIndexZ = boundValue( minUnboundedIndexZ, terrainsZ );
                                    var maxIndexZ = boundValue( maxUnboundedIndexZ, terrainsZ );
                                    for ( var indexZ = minIndexZ; indexZ <= maxIndexZ; indexZ++ ) {
                                        var terrain = terrainsZ[ indexZ ];
                                        if( terrain != null ) {
                                            list.addAll( terrain.getRenderData(
                                                terrain.indexToPosition( indexX, indexY, indexZ, shift ),
                                                currentRenderTime,
                                                cameraRotation )
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    
        for ( var object : objects2D ) {
            if( view.shouldDisplay2D( object.getBBO() ) ) {
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
    
    private void findPositionCalculator( Terrain3D< ?, ? >[][][] terrains ) {
        for ( var terrainsYZ : terrains ) {
            for ( var terrainsZ : terrainsYZ ) {
                for ( var terrain : terrainsZ ) {
                    if( terrain != null ) {
                        calculator = terrain.getPositionCalculator();
                        break;
                    }
                }
            }
        }
    }
}
