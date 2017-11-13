/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.ArrayBuffer;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class BoundedGrid2DRenderer implements Grid2DRenderer {

    private final float[] interAxesArray;
    private final float[] smallPositiveAxesArray;
    private final float[] smallNegativeAxesArray;
    private final float[] mainAxesArray;
    private final ShaderProgram2D shaderProgram;
    private final ArrayBuffer mainAxes = new ArrayBuffer();
    private final ArrayBuffer interAxes = new ArrayBuffer();
    private final ArrayBuffer smallPositiveAxes = new ArrayBuffer();
    private final ArrayBuffer smallNegativeAxes = new ArrayBuffer();
    private final Vector2f zeros = new Vector2f( 0, 0 );
    private final Vector2f ones = new Vector2f( 1, 1 );
    private final Vector4f colour = new Vector4f( 0, 0, 0, 0 );

    public BoundedGrid2DRenderer(
        ShaderProgram2D shaderProgram, int gridSize, int interAxesStep, int smallAxesStep
    )
    {
        this.shaderProgram = shaderProgram;
        int interAxesSize = gridSize / interAxesStep / 2;
        int smallPositiveAxesSize = ( gridSize - interAxesSize ) / 2 / smallAxesStep;
        int smallNegativeAxesSize = smallPositiveAxesSize;

        //2 axes 2 points 2 coords 4 lines per axe = 32
        interAxesSize *= 64;
        smallNegativeAxesSize *= 64 * 2 * gridSize;
        smallPositiveAxesSize *= 64 * 2 * gridSize;
        //X
        //Y
        //Z
        mainAxesArray = new float[]{ -gridSize, 0,
                                     gridSize, 0,//X
                                     0, -gridSize,
                                     0, gridSize,//Y
        };
        interAxesArray = new float[interAxesSize];
        smallPositiveAxesArray = new float[smallPositiveAxesSize];
        smallNegativeAxesArray = new float[smallNegativeAxesSize];
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
                        smallPositiveAxesArray[smallPositiveAxesArrayIt] = axes[j];
                        smallPositiveAxesArrayIt++;
                    } else {
                        smallNegativeAxesArray[smallNegativeAxesArrayIt] = axes[j];
                        smallNegativeAxesArrayIt++;
                    }
                }
            }
        }
    }

    private float[] createAxes( int gridSize, int i ) {
        return new float[]{
            i,0,i,gridSize,
            i,0,i,-gridSize,
            0,i,gridSize,i,
            0,i,-gridSize,i,
            -i,0,-i,gridSize,
            -i,0,-i,-gridSize,
            0,-i,gridSize,-i,
            0,-i,-gridSize,-i
        };
    }

    @Override
    public void render() {
        renderGridArray( mainAxes, 1f, 1, 0f, 0f, 1f );
        renderGridArray( interAxes, 0.5f, 0f, 0f, 1f, 0.75f );
        renderGridArray( smallNegativeAxes, 0.1f, 0.25f, 1f, 0.25f, 0.5f );
        renderGridArray( smallPositiveAxes, 0.1f, 1f, 0.25f, 0.25f, 0.5f );
    }

    @Override
    public void init() {
        mainAxes.create().load( mainAxesArray );
        interAxes.create().load( interAxesArray );
        smallPositiveAxes.create().load( smallPositiveAxesArray );
        smallNegativeAxes.create().load( smallNegativeAxesArray );
    }

    @Override
    public void dispose() {
        mainAxes.delete();
        interAxes.delete();
        smallPositiveAxes.delete();
        smallNegativeAxes.delete();
    }

    private void renderGridArray(
        ArrayBuffer buffer, float width, float red, float green, float blue, float alpha
    )
    {
        colour.set( red, green, blue, alpha );
        bindUniformData();
        buffer.bind();
        renderLines( buffer, width );
    }

    private void renderLines( ArrayBuffer buffer, float width ) {
        GL20.glVertexAttribPointer( 0, 2, GL11.GL_FLOAT, false, 0, 0 );
        GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
        GL11.glLineWidth( width );
        GL11.glDrawArrays( GL11.GL_LINES, 0, buffer.getLength() / 2 );
    }

    private void bindUniformData() {
        shaderProgram.bindPosition( zeros );
        shaderProgram.bindScale( ones );
        shaderProgram.bindRotation( 0 );
        shaderProgram.bindOverlayColour( colour );
        shaderProgram.bindMaxAlphaChannel( 0.75f );
    }
}
