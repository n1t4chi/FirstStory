/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Vertex3D;
import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.singletonArray;
import static com.firststory.firstoracle.object.Vertex3D.vec3;
import static com.firststory.firstoracle.rendering.LineData.lines;

/**
 * @author n1t4chi
 */
public class BoundedGrid3DRenderer implements Grid3DRenderer {
    
    private static final LineData LINES_MAIN = lines( 1f, 1, 0f, 0f, 1f );
    private static final LineData LINES_INTER = lines( 0.5f, 0f, 0f, 1f, 0.75f );
    private static final LineData LINES_POSITIVE = lines( 0.1f, 0.25f, 1f, 0.25f, 0.5f );
    private static final LineData LINES_NEGATIVE = lines( 0.1f, 1f, 0.25f, 0.25f, 0.5f );
    private final Vertices3D mainAxes;
    private final Vertices3D interAxes;
    private final Vertices3D smallPositiveAxes;
    private final Vertices3D smallNegativeAxes;
    
    public BoundedGrid3DRenderer( int gridSize, int interAxesStep, int smallAxesStep ) {
        var mainAxes = Arrays.asList(
            //X
            vec3( -gridSize, 0, 0 ),
            vec3( gridSize, 0, 0 ),
            //Y
            vec3( 0, -gridSize, 0 ),
            vec3( 0, gridSize, 0 ),
            //Z
            vec3( 0, 0, -gridSize ),
            vec3( 0, 0, gridSize )
        );
        List< Vertex3D > interAxes = new ArrayList<>(  );
        List< Vertex3D > smallPositiveAxes = new ArrayList<>(  );
        List< Vertex3D > smallNegativeAxes = new ArrayList<>(  );
        
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
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
        mainAxes.dispose();
        interAxes.dispose();
        smallPositiveAxes.dispose();
        smallNegativeAxes.dispose();
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {
        renderGridArray( renderingContext, mainAxes, LINES_MAIN );
        renderGridArray( renderingContext, interAxes, LINES_INTER );
        renderGridArray( renderingContext, smallPositiveAxes, LINES_POSITIVE );
        renderGridArray( renderingContext, smallNegativeAxes, LINES_NEGATIVE );
    }
    
    private List< Vertex3D > createAxes( int gridSize, int i ) {
        return Arrays.asList(
            //perpendicular to Z axis
            vec3( i, 0, -gridSize ),
           vec3( i, 0, gridSize ),
           vec3( -i, 0, -gridSize ),
           vec3( -i, 0, gridSize ),
            
           vec3( 0, i, -gridSize ),
           vec3( 0, i, gridSize ),
           vec3( 0, -i, -gridSize ),
           vec3( 0, -i, gridSize ),
            
            //perpendicular to Y axis
           vec3( i, -gridSize, 0 ),
           vec3( i, gridSize, 0 ),
           vec3( -i, -gridSize, 0 ),
           vec3( -i, gridSize, 0 ),
            
           vec3( 0, -gridSize, i ),
           vec3( 0, gridSize, i ),
           vec3( 0, -gridSize, -i ),
           vec3( 0, gridSize, -i ),
            
            //perpendicular to Z axis
           vec3( -gridSize, i, 0 ),
           vec3( gridSize, i, 0 ),
           vec3( -gridSize, -i, 0 ),
           vec3( gridSize, -i, 0 ),
            
           vec3( -gridSize, 0, i ),
           vec3( gridSize, 0, i ),
           vec3( -gridSize, 0, -i ),
           vec3( gridSize, 0, -i )
        );
    }
    
    private void renderGridArray(
        RenderingContext renderingContext,
        Vertices3D buffer,
        LineData lineData
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
