/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.LineData;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.data.LineData.lines;
import static com.firststory.firstoracle.data.Position3D.pos3;

/**
 * @author n1t4chi
 */
public class BoundedGrid3D implements Grid3D {
    
    private static final LineData LINES_MAIN = lines( 1f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = lines( 0.5f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_POSITIVE = lines( 0.1f, 0.25f, 1f, 0.25f, 0.5f );
    private static final LineData LINES_NEGATIVE = lines( 0.1f, 1f, 0.25f, 0.25f, 0.5f );
    private final Vertices3D mainAxes;
    private final Vertices3D interAxes;
    private final Vertices3D smallPositiveAxes;
    private final Vertices3D smallNegativeAxes;
    private final List< RenderData > renderDatas;
    
    public BoundedGrid3D( int gridSize, int interAxesStep, int smallAxesStep ) {
        var mainAxes = Arrays.asList(
            //X
            pos3( -gridSize, 0, 0 ),
            pos3( gridSize, 0, 0 ),
            //Y
            pos3( 0, -gridSize, 0 ),
            pos3( 0, gridSize, 0 ),
            //Z
            pos3( 0, 0, -gridSize ),
            pos3( 0, 0, gridSize )
        );
        List< Position3D > interAxes = new ArrayList<>(  );
        List< Position3D > smallPositiveAxes = new ArrayList<>(  );
        List< Position3D > smallNegativeAxes = new ArrayList<>(  );
        
        for ( var i = 1; i <= gridSize; i++ ) {
            if ( i == 0 ) {
                continue;
            }
            
            if ( java.lang.Math.abs( i % interAxesStep ) == 0 ) {
                var axes = createAxes( gridSize, i );
                interAxes.addAll( axes );
            } else if ( java.lang.Math.abs( i % smallAxesStep ) == 0 ) {
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
        this.mainAxes = new Vertices3D( singletonArray(  mainAxes ) );
        this.interAxes = new Vertices3D( singletonArray(  interAxes ) );
        this.smallPositiveAxes = new Vertices3D( singletonArray(  smallPositiveAxes ) );
        this.smallNegativeAxes = new Vertices3D( singletonArray(  smallNegativeAxes ) );
    
        renderDatas = Arrays.asList(
            createRenderData( this.mainAxes, LINES_MAIN ),
            createRenderData( this.interAxes, LINES_INTER ),
            createRenderData( this.smallPositiveAxes, LINES_POSITIVE ),
            createRenderData( this.smallNegativeAxes, LINES_NEGATIVE )
        );
    }
    
    @Override
    public List< RenderData > toRenderDataList() {
        return renderDatas;
    }
    
    @Override
    public void dispose() {
        mainAxes.dispose();
        interAxes.dispose();
        smallPositiveAxes.dispose();
        smallNegativeAxes.dispose();
    }
    
    private List< Position3D > createAxes( int gridSize, int i ) {
        return Arrays.asList(
            //perpendicular to Z axis
            pos3( i, 0, -gridSize ),
           pos3( i, 0, gridSize ),
           pos3( -i, 0, -gridSize ),
           pos3( -i, 0, gridSize ),
            
           pos3( 0, i, -gridSize ),
           pos3( 0, i, gridSize ),
           pos3( 0, -i, -gridSize ),
           pos3( 0, -i, gridSize ),
            
            //perpendicular to Y axis
           pos3( i, -gridSize, 0 ),
           pos3( i, gridSize, 0 ),
           pos3( -i, -gridSize, 0 ),
           pos3( -i, gridSize, 0 ),
            
           pos3( 0, -gridSize, i ),
           pos3( 0, gridSize, i ),
           pos3( 0, -gridSize, -i ),
           pos3( 0, gridSize, -i ),
            
            //perpendicular to Z axis
           pos3( -gridSize, i, 0 ),
           pos3( gridSize, i, 0 ),
           pos3( -gridSize, -i, 0 ),
           pos3( gridSize, -i, 0 ),
            
           pos3( -gridSize, 0, i ),
           pos3( gridSize, 0, i ),
           pos3( -gridSize, 0, -i ),
           pos3( gridSize, 0, -i )
        );
    }
    
    private RenderData createRenderData(
        Vertices3D vertices,
        LineData lineData
    ) {
        return RenderData.renderLineData(
            vertices,
            0,
            FirstOracleConstants.POSITION_ZERO_3F,
            FirstOracleConstants.SCALE_ONE_3F,
            FirstOracleConstants.ROTATION_ZERO_3F,
            lineData
        ).build();
    }
}
