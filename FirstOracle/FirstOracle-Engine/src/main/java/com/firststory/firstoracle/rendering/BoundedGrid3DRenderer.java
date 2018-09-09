/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.Vertices3D;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @author n1t4chi
 */
public class BoundedGrid3DRenderer implements Grid3DRenderer {
    
    private static final LineData LINES_MAIN = new LineData( 1f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = new LineData( 0.5f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_POSITIVE = new LineData( 0.1f, 0.25f, 1f, 0.25f, 0.5f );
    private static final LineData LINES_NEGATIVE = new LineData( 0.1f, 1f, 0.25f, 0.25f, 0.5f );
    private final Vertices3D mainAxes;
    private final Vertices3D interAxes;
    private final Vertices3D smallPositiveAxes;
    private final Vertices3D smallNegativeAxes;
    private final Vector3f zeros = new Vector3f( 0, 0, 0 );
    private final Vector3f ones = new Vector3f( 1, 1, 1 );
    private final Vector4f colour = new Vector4f( 0, 0, 0, 0 );
    
    public BoundedGrid3DRenderer( int gridSize, int interAxesStep, int smallAxesStep ) {
        int interAxesSize = gridSize / interAxesStep / 2;
        int smallPositiveAxesSize = ( gridSize - interAxesSize ) / 2 / smallAxesStep;
        int smallNegativeAxesSize = smallPositiveAxesSize;
        
        //3 axes * 2 perpendicular lines * 2 sides * 2 point * 3 coordinates * 2 gridsize = 144
        interAxesSize *= 144;
        smallNegativeAxesSize *= 144 * 2 * gridSize;
        smallPositiveAxesSize *= 144 * 2 * gridSize;
        //X
        //Y
        //Z
        float[] mainAxesArray = new float[]{
            -gridSize, 0, 0, gridSize, 0, 0,//X
        
            0, -gridSize, 0, 0, gridSize, 0,//Y
        
            0, 0, -gridSize, 0, 0, gridSize//Z
        };
        float[] interAxesArray = new float[interAxesSize];
        float[] smallPositiveAxesArray = new float[smallPositiveAxesSize];
        float[] smallNegativeAxesArray = new float[smallNegativeAxesSize];
        int interAxesArrayIt = 0;
        int smallPositiveAxesArrayIt = 0;
        int smallNegativeAxesArrayIt = 0;
        
        for ( int i = 1; i <= gridSize; i++ ) {
            if ( i == 0 ) {
                continue;
            }
            
            if ( java.lang.Math.abs( i % interAxesStep ) == 0 ) {
                float[] axes = createAxes( gridSize, i );
                System.arraycopy( axes, 0, interAxesArray, interAxesArrayIt, axes.length );
                interAxesArrayIt += axes.length;
            } else if ( java.lang.Math.abs( i % smallAxesStep ) == 0 ) {
                boolean positive = false;
                float[] axes = createAxes( gridSize, i );
                for ( int j = 0; j < axes.length; j++ ) {
                    if ( j % 6 == 0 ) {
                        positive = !positive;
                    }
                    if ( positive ) {
                        smallPositiveAxesArray[ smallPositiveAxesArrayIt ] = axes[ j ];
                        smallPositiveAxesArrayIt++;
                    } else {
                        smallNegativeAxesArray[ smallNegativeAxesArrayIt ] = axes[ j ];
                        smallNegativeAxesArrayIt++;
                    }
                }
            }
        }
        mainAxes = new Vertices3D( new float[][]{ mainAxesArray } );
        interAxes = new Vertices3D( new float[][]{ interAxesArray } );
        smallPositiveAxes = new Vertices3D( new float[][]{ smallPositiveAxesArray } );
        smallNegativeAxes = new Vertices3D( new float[][]{ smallNegativeAxesArray } );
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
        mainAxes.close();
        interAxes.close();
        smallPositiveAxes.close();
        smallNegativeAxes.close();
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {
        renderGridArray( renderingContext, mainAxes, LINES_MAIN );
        renderGridArray( renderingContext, interAxes, LINES_INTER );
        renderGridArray( renderingContext, smallPositiveAxes, LINES_POSITIVE );
        renderGridArray( renderingContext, smallNegativeAxes, LINES_NEGATIVE );
    }
    
    private float[] createAxes( int gridSize, int i ) {
        return new float[]{
            //perpendicular to Z axis
            i, 0, -gridSize, i, 0, gridSize, -i, 0, -gridSize, -i, 0, gridSize,
            
            0, i, -gridSize, 0, i, gridSize, 0, -i, -gridSize, 0, -i, gridSize,
            
            //perpendicular to Y axis
            i, -gridSize, 0, i, gridSize, 0, -i, -gridSize, 0, -i, gridSize, 0,
            
            0, -gridSize, i, 0, gridSize, i, 0, -gridSize, -i, 0, gridSize, -i,
            
            //perpendicular to Z axis
            -gridSize, i, 0, gridSize, i, 0, -gridSize, -i, 0, gridSize, -i, 0,
            
            -gridSize, 0, i, gridSize, 0, i, -gridSize, 0, -i, gridSize, 0, -i,
        };
    }
    
    private void renderGridArray(
        RenderingContext renderingContext, Vertices3D buffer, LineData lineData
    ) {
        renderingContext.render3D( renderer ->
            renderer.renderVerticesAsLines(
                buffer,
                0,
                FirstOracleConstants.VECTOR_ZERO_3F,
                FirstOracleConstants.VECTOR_ONES_3F,
                FirstOracleConstants.VECTOR_ZERO_3F,
                lineData
            )
        );
    }
}
