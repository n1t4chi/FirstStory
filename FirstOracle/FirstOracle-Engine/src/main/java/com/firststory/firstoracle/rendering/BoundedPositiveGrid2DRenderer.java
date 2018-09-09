/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Vertices2D;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * @author n1t4chi
 */
public class BoundedPositiveGrid2DRenderer implements Grid2DRenderer {
    private static final LineData LINES_MAIN = new LineData( 1f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = new LineData( 0.5f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_SMALL = new LineData( 0.1f, 0.25f, 1f, 0.25f, 0.5f );

    private final Vector2f zeros = new Vector2f( 0, 0 );
    private final Vector2f ones = new Vector2f( 1, 1 );
    private final Vector4f colour = new Vector4f( 0, 0, 0, 0 );
    private boolean reload;
    private int gridWidth;
    private int gridHeight;
    private int intermediateAxesStep;
    private Vertices2D mainAxes;
    private Vertices2D interAxes;
    private Vertices2D smallAxes;
    private boolean render;
    
    public BoundedPositiveGrid2DRenderer(  int gridWidth, int gridHeight, int intermediateAxesStep ) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.intermediateAxesStep = intermediateAxesStep;
        this.reload = true;
    }
    
    public int getGridWidth() {
        return gridWidth;
    }
    
    public void setGridWidth( int gridWidth ) {
        if ( gridWidth >= 0 && this.gridWidth != gridWidth ) {
            reload = true;
            this.gridWidth = gridWidth;
        }
    }
    
    public int getGridHeight() {
        return gridHeight;
    }
    
    public void setGridHeight( int gridHeight ) {
        if ( gridHeight >= 0 && this.gridHeight != gridHeight ) {
            reload = true;
            this.gridHeight = gridHeight;
        }
    }
    
    public int getIntermediateAxesStep() {
        return intermediateAxesStep;
    }
    
    public void setIntermediateAxesStep( int intermediateAxesStep ) {
        if ( intermediateAxesStep > 0 && this.intermediateAxesStep != intermediateAxesStep ) {
            reload = true;
            this.intermediateAxesStep = intermediateAxesStep;
        }
    }
    
    public int addAxesToArray( int interAxesArrayIt, float[] array, float[] axes ) {
        System.arraycopy( axes, 0, array, interAxesArrayIt, axes.length );
        return interAxesArrayIt + axes.length;
    }
    
    @Override
    public void init() {
        if ( reload ) {
            dispose();
            createGrid();
        }
    }
    
    @Override
    public void dispose() {
        if ( mainAxes != null ) {
            mainAxes.close();
            mainAxes = null;
        }
        if ( interAxes != null ) {
            interAxes.close();
            interAxes = null;
        }
        if ( smallAxes != null ) {
            smallAxes.close();
            smallAxes = null;
        }
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {
        init();
        if( render ) {
            renderGridArray( renderingContext, mainAxes, LINES_MAIN );
            renderGridArray( renderingContext, interAxes, LINES_INTER );
            renderGridArray( renderingContext, smallAxes, LINES_SMALL );
        }
    }
    
    private void createGrid() {
        if ( gridHeight == 0 || gridWidth == 0 ) {
            render = false;
        }
        render= true;
        float[] mainAxesArray = new float[]{
            0, 0, gridWidth, 0, 0, 0, 0, gridHeight, 0, gridHeight, gridWidth, gridHeight,
            gridWidth, 0, gridWidth, gridHeight
        };
        
        int interAxesSize = (
            gridWidth / intermediateAxesStep + gridHeight / intermediateAxesStep
        ) * 4;
        int smallPositiveAxesSize = ( gridHeight + gridWidth ) * 4 - interAxesSize;
        
        float[] interAxesArray = new float[ interAxesSize ];
        float[] smallAxesArray = new float[ smallPositiveAxesSize ];
        int interAxesIt = 0;
        int smallAxesIt = 0;
        
        for ( int i = 1; i < gridWidth; i++ ) {
            float[] verticalAxes = createVerticalAxes( i, gridHeight );
            if ( Math.abs( i % intermediateAxesStep ) == 0 ) {
                interAxesIt = addAxesToArray( interAxesIt, interAxesArray, verticalAxes );
            } else {
                smallAxesIt = addAxesToArray( smallAxesIt, smallAxesArray, verticalAxes );
            }
        }
        for ( int i = 1; i < gridHeight; i++ ) {
            float[] horizontalAxes = createHorizontalAxes( i, gridWidth );
            if ( Math.abs( i % intermediateAxesStep ) == 0 ) {
                interAxesIt = addAxesToArray( interAxesIt, interAxesArray, horizontalAxes );
            } else {
                smallAxesIt = addAxesToArray( smallAxesIt, smallAxesArray, horizontalAxes );
            }
        }
        
        mainAxes = new Vertices2D( new float[][]{ mainAxesArray } );
        interAxes = new Vertices2D( new float[][]{ interAxesArray } );
        smallAxes = new Vertices2D( new float[][]{ smallAxesArray } );
        reload = false;
    }
    
    private float[] createHorizontalAxes( int i, int gridWidth ) {
        return new float[]{ 0, i, gridWidth, i };
    }
    
    private float[] createVerticalAxes( int i, int gridHeight ) {
        return new float[]{ i, 0, i, gridHeight };
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
