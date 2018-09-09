/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Vertices2D;

/**
 * @author n1t4chi
 */
public class BoundedGrid2DRenderer implements Grid2DRenderer {
    
    private static final LineData LINES_MAIN = new LineData( 1f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = new LineData( 0.5f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_POSITIVE = new LineData( 0.1f, 0.25f, 1f, 0.25f, 0.5f );
    private static final LineData LINES_NEGATIVE = new LineData( 0.1f, 1f, 0.25f, 0.25f, 0.5f );
    private final Vertices2D mainAxes;
    private final Vertices2D interAxes;
    private final Vertices2D smallPositiveAxes;
    private final Vertices2D smallNegativeAxes;
    
    public BoundedGrid2DRenderer( int gridSize, int interAxesStep, int smallAxesStep ) {
        int interAxesSize = gridSize / interAxesStep;
        int smallPositiveAxesSize = ( gridSize - interAxesSize ) / smallAxesStep;
        int smallNegativeAxesSize = smallPositiveAxesSize;
        
        //2 axes 2 points 2 coords 4 lines per axe = 16
        interAxesSize *= 2 * 16;
        smallNegativeAxesSize *= 16;
        smallPositiveAxesSize *= 16;
        //X
        //Y
        //Z
        float[] mainAxesArray = new float[]{
            -gridSize, 0, gridSize, 0,//X
            0, -gridSize, 0, gridSize,//Y
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
            if ( Math.abs( i % interAxesStep ) == 0 ) {
                float[] axes = createAxes( gridSize, i );
                System.arraycopy( axes, 0, interAxesArray, interAxesArrayIt, axes.length );
                interAxesArrayIt += axes.length;
            } else if ( Math.abs( i % smallAxesStep ) == 0 ) {
                boolean positive = false;
                float[] axes = createAxes( gridSize, i );
                for ( int j = 0; j < axes.length; j++ ) {
                    if ( j % 4 == 0 ) {
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
        mainAxes = new Vertices2D( new float[][]{ mainAxesArray } );
        interAxes = new Vertices2D( new float[][]{ interAxesArray } );
        smallPositiveAxes = new Vertices2D( new float[][]{ smallPositiveAxesArray } );
        smallNegativeAxes = new Vertices2D( new float[][]{ smallNegativeAxesArray } );
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
            i, 0, i, gridSize,
            i, 0, i, -gridSize,
            0, i, gridSize, i,
            0, i, -gridSize, i,
            -i, 0, -i, gridSize,
            -i, 0, -i, -gridSize,
            0, -i, gridSize, -i,
            0, -i, -gridSize, -i
        };
    }
    
    private void renderGridArray(
        RenderingContext renderingContext, Vertices2D buffer, LineData lineData
    ) {
        renderingContext.render2D( renderer ->
            renderer.renderVerticesAsLines(
                buffer,
                0,
                FirstOracleConstants.VECTOR_ZERO_2F,
                FirstOracleConstants.VECTOR_ONES_2F,
                0f,
                lineData
            )
        );
    }
}
