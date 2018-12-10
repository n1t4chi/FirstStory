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
        var terrainSize = getTerrainSize();
        var shift = getTerrain3DShift();
        var terrainsXYZ = getTerrains3D();
        var objects2D = getObjects3D();
    
        var view = new CameraView3D( getScene3DCamera() );
    
        for ( var indexY = 0; indexY < terrainSize.y(); indexY++ ) {
            var positionY = calculator.yIndexToPosition( indexY, shift );
            var plane = view.getPlaneForDimOther( positionY );
    
            var minPosX = plane.minDim1_Floor();
            var maxPosX = plane.maxDim1_Ceil();
            var minUnboundedIndexX = calculator.xPositionToIndex( minPosX, shift ) - 2;
            var maxUnboundedIndexX = calculator.xPositionToIndex( maxPosX, shift ) + 2;
    
            if( maxUnboundedIndexX >= 0 || minUnboundedIndexX  < terrainSize.x() ) {
                var minIndexX = boundValue( minUnboundedIndexX, terrainSize.x() );
                var maxIndexX = boundValue( maxUnboundedIndexX, terrainSize.x() );
        
                for ( var indexX = minIndexX; indexX <= maxIndexX; indexX++ ) {
                    var terrainsZ = terrainsXYZ[ indexX ][ indexY ];
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
                        var minPosZ = Math.min( bounds1.getMin(), bounds2.getMin() );
                        var maxPosZ = Math.max( bounds1.getMax(), bounds2.getMax() );
                        var minUnboundedIndexZ = calculator.zPositionToIndex( minPosZ, shift ) - 2;
                        var maxUnboundedIndexZ = calculator.zPositionToIndex( maxPosZ, shift ) + 2;
                        if( maxUnboundedIndexZ >= 0 || minUnboundedIndexZ  < terrainSize.x() ) {
                            var minIndexZ = boundValue( minUnboundedIndexZ, terrainSize.z() );
                            var maxIndexZ = boundValue( maxUnboundedIndexZ, terrainSize.z() );
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
    
        for ( var object : objects2D ) {
            if( view.shouldDisplay( object.getBBO() ) ) {
                list.addAll( object.getRenderData( currentRenderTime, cameraRotation ) );
            }
        }
        return list;
    }
    
    private int boundValue( int value, int arraySize) {
        if( value < 0 ) {
            return 0;
        } else if ( value > arraySize-1 ) {
            return arraySize-1;
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
