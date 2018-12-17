/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.rendering.RenderData;
import org.joml.Vector4f;

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
        var objects3D = getObjects3D();
    
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
                                if( terrain != null && isNotHidden( terrainsXYZ, indexX, indexY, indexZ, terrainSize ) ) {
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
        var queue = new PriorityQueue< Pair >();
        var invMatrix = view.getInvMatrix();
        var vec = new Vector4f();
        for ( var object : objects3D ) {
            if( view.shouldDisplay( object.getBBO() ) ) {
                var renderData = object.getRenderData( currentRenderTime, cameraRotation );
                var position = object.getPosition();
                vec.set( position.x(), position.y(), position.z(), 1 );
                vec.mul( invMatrix );
                queue.add( new Pair( renderData, vec.z() ) );
            }
        }
        Pair pair;
        while( ( pair = queue.poll() ) != null )
        {
           list.addAll( pair.renderData );
        }
        return list;
    }
    
    private boolean isNotHidden(
        Terrain3D< ?, ? >[][][] terrainsXYZ,
        int indexX,
        int indexY,
        int indexZ,
        Index3D terrainSize
    ) {
        if( indexX <= 0 || indexZ <= 0)
        {
            return true;
        }
        if( indexX >= terrainSize.x()-1 || indexY >= terrainSize.y()-1 || indexZ >= terrainSize.z()-1 )
        {
            return true;
        }
        for( var x = indexX-1; x <= indexX+1 ; x++)
        {
            for( var z = indexZ-1; z <= indexZ+1 ; z++)
            {
                if( terrainsXYZ[ x ][ indexY ][ z ] == null )
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    private class Pair implements Comparable< Pair > {
        private Collection< RenderData > renderData;
        private float distance;
        
    
        private Pair(
            Collection< RenderData > renderData,
            float distance
        ) {
            this.renderData = renderData;
            this.distance = distance;
        }
    
        @Override
        public int compareTo( Pair o ) {
            return Float.compare( o.distance, distance );
        }
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
