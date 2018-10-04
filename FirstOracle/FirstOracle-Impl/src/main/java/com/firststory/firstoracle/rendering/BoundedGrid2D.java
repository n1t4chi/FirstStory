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
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.data.LineData.lines;
import static com.firststory.firstoracle.data.Position2D.pos2;

/**
 * @author n1t4chi
 */
public class BoundedGrid2D implements Grid2D {
    
    private static final LineData LINES_MAIN = lines( 1.5f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = lines( 1f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_POSITIVE = lines( 0.5f, 0.25f, 1f, 0.25f, 0.5f );
    private static final LineData LINES_NEGATIVE = lines( 0.5f, 1f, 0.25f, 0.25f, 0.5f );
    private final Vertices2D mainAxes;
    private final Vertices2D interAxes;
    private final Vertices2D smallPositiveAxes;
    private final Vertices2D smallNegativeAxes;
    private final List< RenderData > renderDatas;
    
    public BoundedGrid2D( int gridSize, int interAxesStep, int smallAxesStep ) {
        var mainAxes = Arrays.asList(
            //X
            pos2( -gridSize, 0 ),
            pos2( gridSize, 0 ),
            
            //Y
            pos2( 0, -gridSize ),
            pos2( 0, gridSize )
        );
    
        List< Position2D > interAxes = new ArrayList<>(  );
        List< Position2D > smallPositiveAxes = new ArrayList<>(  );
        List< Position2D > smallNegativeAxes = new ArrayList<>(  );
        
        for ( var i = 1; i <= gridSize; i++ ) {
            if ( i == 0 ) {
                continue;
            }
            if ( Math.abs( i % interAxesStep ) == 0 ) {
                var axes = createAxes( gridSize, i );
                interAxes.addAll( axes );
            } else if ( Math.abs( i % smallAxesStep ) == 0 ) {
                var positive = false;
                var axes = createAxes( gridSize, i );
                for ( var j = 0; j < axes.size(); j++ ) {
                    if ( j % 2 == 0 ) {
                        positive = !positive;
                    }
                    if ( positive ) {
                        smallPositiveAxes.add( axes.get( j ) );
                    } else {
                        smallNegativeAxes.add( axes.get( j ) );
                    }
                }
            }
        }
        this.mainAxes = new Vertices2D( singletonArray(  mainAxes ) );
        this.interAxes = new Vertices2D( singletonArray(  interAxes ) );
        this.smallPositiveAxes = new Vertices2D( singletonArray(  smallPositiveAxes ) );
        this.smallNegativeAxes = new Vertices2D( singletonArray( smallNegativeAxes ) );
    
        renderDatas = Arrays.asList(
            createRenderData( this.mainAxes, LINES_MAIN ),
            createRenderData( this.interAxes, LINES_INTER ),
            createRenderData( this.smallPositiveAxes, LINES_POSITIVE ),
            createRenderData( this.smallNegativeAxes, LINES_NEGATIVE )
        );
    }
    
    @Override
    public void dispose() {
        mainAxes.dispose();
        interAxes.dispose();
        smallPositiveAxes.dispose();
        smallNegativeAxes.dispose();
    }
    
    @Override
    public List< RenderData > toRenderDataList() {
        return renderDatas;
    }
    
    private List< Position2D > createAxes( int gridSize, int i ) {
        return Arrays.asList(
            pos2( i, 0 ),
            pos2( i, gridSize ),
            pos2( -i, 0 ),
            pos2( -i, gridSize ),
        
            pos2( i, 0 ),
            pos2( i, -gridSize ),
            pos2( -i, 0 ),
            pos2( -i, -gridSize ),
        
            pos2( 0, i ),
            pos2( gridSize, i ),
            pos2( 0, -i ),
            pos2( gridSize, -i ),
            
            pos2( 0, i ),
            pos2( -gridSize, i ),
            pos2( 0, -i ),
            pos2( -gridSize, -i )
        );
    }
    
    private RenderData createRenderData(
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
        ).build();
    }
}
