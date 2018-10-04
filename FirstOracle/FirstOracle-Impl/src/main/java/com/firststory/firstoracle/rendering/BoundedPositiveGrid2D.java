/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.LineData;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.object2D.Vertices2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.data.LineData.lines;
import static com.firststory.firstoracle.data.Position2D.pos2;

/**
 * @author n1t4chi
 */
public class BoundedPositiveGrid2D implements Grid2D {
    
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
    private final List< RenderData > renderData = new ArrayList<>(  );
    private RenderData.RenderDataBuilder mainRenderDataBuilder;
    private RenderData.RenderDataBuilder interRenderDataBuilder;
    private RenderData.RenderDataBuilder smallRenderDataBuilder;
    private boolean firstTime = true;
    
    public BoundedPositiveGrid2D( int gridWidth, int gridHeight, int intermediateAxesStep ) {
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
    public List< RenderData > toRenderDataList() {
        if ( reload ) {
            dispose();
            createGrid();
        }
        return render ? renderData : Collections.emptyList();
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
        if( firstTime ) {
            mainRenderDataBuilder = createRenderData( this.mainAxes, LINES_MAIN );
            interRenderDataBuilder = createRenderData( this.interAxes, LINES_INTER );
            smallRenderDataBuilder = createRenderData( this.smallAxes, LINES_SMALL );
            renderData.add( mainRenderDataBuilder.build() );
            renderData.add( interRenderDataBuilder.build() );
            renderData.add( smallRenderDataBuilder.build() );
        } else {
            updateRenderData( mainRenderDataBuilder, this.mainAxes );
            updateRenderData( interRenderDataBuilder, this.interAxes );
            updateRenderData( smallRenderDataBuilder, this.smallAxes );
        }
        
        firstTime = false;
        reload = false;
    }
    
    private List< Position2D > createHorizontalAxes( int i, int gridWidth ) {
        return Arrays.asList( pos2( 0, i ), pos2( gridWidth, i ) );
    }
    
    private List< Position2D > createVerticalAxes( int i, int gridHeight ) {
        return Arrays.asList( pos2( i, 0 ), pos2( i, gridHeight ) );
    }
    
    
    private RenderData.RenderDataBuilder createRenderData(
        Vertices2D vertices,
        LineData lineData
    ) {
        return RenderData.renderLineData(
            vertices,
            0,
            FirstOracleConstants.POSITION_ZERO_2F,
            FirstOracleConstants.SCALE_ONE_2F,
            FirstOracleConstants.ROTATION_ZERO_2F,
            lineData
        );
    }
    private void updateRenderData(
        RenderData.RenderDataBuilder builder,
        Vertices2D vertices
    ) {
        builder.setVertices( vertices );
    }
}
