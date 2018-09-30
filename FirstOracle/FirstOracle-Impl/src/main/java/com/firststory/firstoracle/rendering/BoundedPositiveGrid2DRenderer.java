/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.data.Position2D;
import com.firststory.firstoracle.object2D.Vertices2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.object.data.Position2D.pos2;
import static com.firststory.firstoracle.rendering.LineData.lines;

/**
 * @author n1t4chi
 */
public class BoundedPositiveGrid2DRenderer implements Grid2DRenderer {
    
    private static final LineData LINES_MAIN = lines( 1f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = lines( 0.5f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_SMALL = lines( 0.1f, 0.25f, 1f, 0.25f, 0.5f );
    
    private boolean reload;
    private int gridWidth;
    private int gridHeight;
    private int intermediateAxesStep;
    private Vertices2D mainAxes;
    private Vertices2D interAxes;
    private Vertices2D smallAxes;
    private boolean render;
    
    public BoundedPositiveGrid2DRenderer( int gridWidth, int gridHeight, int intermediateAxesStep ) {
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
            mainAxes.dispose();
            mainAxes = null;
        }
        if ( interAxes != null ) {
            interAxes.dispose();
            interAxes = null;
        }
        if ( smallAxes != null ) {
            smallAxes.dispose();
            smallAxes = null;
        }
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {
        init();
        if ( render ) {
            renderGridArray( renderingContext, mainAxes, LINES_MAIN );
            renderGridArray( renderingContext, interAxes, LINES_INTER );
            renderGridArray( renderingContext, smallAxes, LINES_SMALL );
        }
    }
    
    private void createGrid() {
        if ( gridHeight == 0 || gridWidth == 0 ) {
            render = false;
        }
        render = true;
        var mainAxes = Arrays.asList(
            pos2( 0, 0 ),
            pos2( gridWidth, 0 ),
            
            pos2( 0, 0 ),
            pos2( 0, gridHeight ),
    
            pos2( gridWidth, gridHeight ),
            pos2( 0, gridHeight ),
    
            pos2( gridWidth, gridHeight ),
            pos2( gridWidth, 0 )
        );
    
        List< Position2D > interAxes = new ArrayList<>(  );
        List< Position2D > smallAxes = new ArrayList<>(  );
        
        for ( var i = 1; i < gridWidth; i++ ) {
            var verticalAxes = createVerticalAxes( i, gridHeight );
            if ( Math.abs( i % intermediateAxesStep ) == 0 ) {
                interAxes.addAll( verticalAxes );
            } else {
                smallAxes.addAll( verticalAxes );
            }
        }
        for ( var i = 1; i < gridHeight; i++ ) {
            var horizontalAxes = createHorizontalAxes( i, gridWidth );
            if ( Math.abs( i % intermediateAxesStep ) == 0 ) {
                interAxes.addAll( horizontalAxes );
            } else {
                smallAxes.addAll( horizontalAxes );
            }
        }
    
        this.mainAxes = new Vertices2D( singletonArray( mainAxes ) );
        this.interAxes = new Vertices2D( singletonArray( interAxes ) );
        this.smallAxes = new Vertices2D( singletonArray( smallAxes ) );
        reload = false;
    }
    
    private List< Position2D > createHorizontalAxes( int i, int gridWidth ) {
        return Arrays.asList( pos2( 0, i ), pos2( gridWidth, i ) );
    }
    
    private List< Position2D > createVerticalAxes( int i, int gridHeight ) {
        return Arrays.asList( pos2( i, 0 ), pos2( i, gridHeight ) );
    }
    
    private void renderGridArray(
        RenderingContext renderingContext,
        Vertices2D buffer,
        LineData lineData
    ) {
        renderingContext.render2D( renderer -> renderer.renderVerticesAsLines( buffer,
            0,
            FirstOracleConstants.POSITION_ZERO_2F,
            FirstOracleConstants.SCALE_ONE_2F,
            FirstOracleConstants.ROTATION_ZERO_2F,
            lineData
        ) );
    }
}
