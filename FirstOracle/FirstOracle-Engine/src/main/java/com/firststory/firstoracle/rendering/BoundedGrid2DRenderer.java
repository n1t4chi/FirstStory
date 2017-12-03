/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class BoundedGrid2DRenderer implements Grid2DRenderer {
    
    private final ShaderProgram2D shaderProgram;
    private final Vertices2D mainAxes;
    private final Vertices2D interAxes;
    private final Vertices2D smallPositiveAxes;
    private final Vertices2D smallNegativeAxes;
    private final Vector2f zeros = new Vector2f( 0, 0 );
    private final Vector2f ones = new Vector2f( 1, 1 );
    private final Vector4f colour = new Vector4f( 0, 0, 0, 0 );
    
    public BoundedGrid2DRenderer(
        ShaderProgram2D shaderProgram, int gridSize, int interAxesStep, int smallAxesStep
    ) {
        this.shaderProgram = shaderProgram;
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
    public void render() {
        renderGridArray( mainAxes, 1f, 1, 0f, 0f, 1f );
        renderGridArray( interAxes, 0.5f, 0f, 0f, 1f, 0.75f );
        renderGridArray( smallPositiveAxes, 0.1f, 0.25f, 1f, 0.25f, 0.5f );
        renderGridArray( smallNegativeAxes, 0.1f, 1f, 0.25f, 0.25f, 0.5f );
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
        Vertices2D buffer, float width, float red, float green, float blue, float alpha
    ) {
        colour.set( red, green, blue, alpha );
        bindUniformData();
        int length = buffer.bind( 0 );
        GL11.glLineWidth( width );
        GL11.glDrawArrays( GL11.GL_LINES, 0, length );
    }
    
    private void bindUniformData() {
        shaderProgram.bindPosition( zeros );
        shaderProgram.bindScale( ones );
        shaderProgram.bindRotation( 0 );
        shaderProgram.bindOverlayColour( colour );
        shaderProgram.bindMaxAlphaChannel( 0.75f );
    }
}
