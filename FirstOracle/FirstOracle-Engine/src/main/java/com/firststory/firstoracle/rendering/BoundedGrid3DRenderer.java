/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object3D.Vertices3D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

public class BoundedGrid3DRenderer implements Grid3DRenderer {

    private final float[] interAxesArray;
    private final float[] smallPositiveAxesArray;
    private final float[] smallNegativeAxesArray;
    private final float[] mainAxesArray;
    private final ShaderProgram3D shaderProgram;
    private final Vertices3D mainAxes;
    private final Vertices3D interAxes;
    private final Vertices3D smallPositiveAxes;
    private final Vertices3D smallNegativeAxes;
    private final Vector3f zeros = new Vector3f( 0, 0, 0 );
    private final Vector3f ones = new Vector3f( 1, 1, 1 );
    private final Vector4f colour = new Vector4f( 0, 0, 0, 0 );

    public BoundedGrid3DRenderer(
        ShaderProgram3D shaderProgram, int gridSize, int interAxesStep, int smallAxesStep
    )
    {
        this.shaderProgram = shaderProgram;
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
        mainAxesArray = new float[]{ -gridSize, 0, 0, gridSize, 0, 0,//X

                                     0, -gridSize, 0, 0, gridSize, 0,//Y

                                     0, 0, -gridSize, 0, 0, gridSize//Z
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
                        smallPositiveAxesArray[smallPositiveAxesArrayIt] = axes[j];
                        smallPositiveAxesArrayIt++;
                    } else {
                        smallNegativeAxesArray[smallNegativeAxesArrayIt] = axes[j];
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

    @Override
    public void init() { }

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

    private void renderGridArray(
        Vertices3D buffer, float width, float red, float green, float blue, float alpha
    )
    {
        colour.set( red, green, blue, alpha );
        bindUniformData();
        int length = buffer.bind( 0 );
        GL11.glLineWidth( width );
        GL11.glDrawArrays( GL11.GL_LINES, 0, length );
    }

    private void bindUniformData() {
        shaderProgram.bindPosition( zeros );
        shaderProgram.bindScale( ones );
        shaderProgram.bindRotation( zeros );
        shaderProgram.bindOverlayColour( colour );
        shaderProgram.bindMaxAlphaChannel( 0.75f );
    }
}
