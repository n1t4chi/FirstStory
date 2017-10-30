/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.ArrayBuffer;
import com.firststory.firstoracle.window.shader.ShaderProgram;
import com.sun.prism.es2.JFXGLContext;
import cuchaz.jfxgl.controls.OpenGLPane;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author: n1t4chi
 */
public abstract class GridRenderer implements OpenGLPane.Renderer {

    public static GridRenderer DummyGridRenderer = new GridRenderer() {
        @Override
        public void render( JFXGLContext context ) { }
    };

    public static class ConcreteGridRenderer extends GridRenderer {

        private ShaderProgram shaderProgram;
        private ArrayBuffer mainAxes = new ArrayBuffer();
        private ArrayBuffer interAxes = new ArrayBuffer();

        @Override
        public void render( JFXGLContext context ) {

            renderGridArray( mainAxes, 3, 1, 1, 1 );
            renderGridArray( interAxes, 1, 0.75f, 0.75f, 0.75f );
            renderGridArray( smallNegativeAxes, 0.3f, 0.5f, 1f, 0.25f );
            renderGridArray( smallPositiveAxes, 0.3f, 1f, 0.5f, 0.25f );
        }

        private ArrayBuffer smallPositiveAxes = new ArrayBuffer();
        private ArrayBuffer smallNegativeAxes = new ArrayBuffer();
        private Vector3f zeros = new Vector3f( 0, 0, 0 );
        private Vector3f ones = new Vector3f( 1, 1, 1 );
        private Vector4f colour = new Vector4f( 0, 0, 0, 0 );

            public ConcreteGridRenderer(
            ShaderProgram shaderProgram,
            int gridSize,
            int interAxesStep
        )
        {
            this.shaderProgram = shaderProgram;
            int interAxesSize = gridSize / interAxesStep;
            int smallPositiveAxesSize = ( gridSize - interAxesSize ) / 2;
            int smallNegativeAxesSize = smallPositiveAxesSize;

            //3 axes * 2 perpendicular lines * 2 sides * 2 point * 3 coordinates * 2 gridsize = 144
            interAxesSize *= 144;
            smallNegativeAxesSize *= 144 * 2 * gridSize;
            smallPositiveAxesSize *= 144 * 2 * gridSize;
            float[] mainAxesArray = {
                -gridSize, 0, 0,
                gridSize, 0, 0,//X

                0, -gridSize, 0,
                0, gridSize, 0,//Y

                0, 0, -gridSize,
                0, 0, gridSize//Z
            };
            float[] interAxesArray = new float[interAxesSize];
            float[] smallPositiveAxesArray = new float[smallPositiveAxesSize];
            float[] smallNegativeAxesArray = new float[smallNegativeAxesSize];
            int interAxesArrayIt = 0;
            int smallPositiveAxesArrayIt = 0;
            int smallNegativeAxesArrayIt = 0;

            for ( int i = -gridSize; i <= gridSize; i++ ) {
                if ( i == 0 ) {
                    continue;
                }
                float[] axes = {
                    //perpendicular to Z axis
                    i, 0, -gridSize,
                    i, 0, gridSize,
                    -i, 0, -gridSize,
                    -i, 0, gridSize,

                    0, i, -gridSize,
                    0, i, gridSize,
                    0, -i, -gridSize,
                    0, -i, gridSize,

                    //perpendicular to Y axis
                    i, -gridSize, 0,
                    i, gridSize, 0,
                    -i, -gridSize, 0,
                    -i, gridSize, 0,

                    0, -gridSize, i,
                    0, gridSize, i,
                    0, -gridSize, -i,
                    0, gridSize, -i,

                    //perpendicular to Z axis
                    -gridSize, i, 0,
                    gridSize, i, 0,
                    -gridSize, -i, 0,
                    gridSize, -i, 0,

                    -gridSize, 0, i,
                    gridSize, 0, i,
                    -gridSize, 0, -i,
                    gridSize, 0, -i,
                    };
                if ( Math.abs( i % interAxesStep ) != 0 ) {
                    boolean positive = false;
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
                } else {
                    for ( int j = 0; j < axes.length; j++ ) {
                        interAxesArray[interAxesArrayIt + j] = axes[j];
                    }
                    interAxesArrayIt += axes.length;
                }
            }

            mainAxes.create().load( mainAxesArray );
            interAxes.create().load( interAxesArray );
            smallPositiveAxes.create().load( smallPositiveAxesArray );
            smallNegativeAxes.create().load( smallNegativeAxesArray );

        }

        private void renderGridArray(
            ArrayBuffer buffer,
            float width,
            float red,
            float green,
            float blue
        )
        {
            colour.set( red, green, blue, 1 );
            bindUniformData();
            buffer.bind();
            renderLines( buffer, width );
        }

        private void renderLines( ArrayBuffer buffer, float width ) {
            GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
            GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
            GL11.glLineWidth( width );
            GL11.glDrawArrays( GL11.GL_LINES, 0, buffer.getLength() / 2 );
        }

        private void bindUniformData() {
            shaderProgram.bindPosition( zeros );
            shaderProgram.bindScale( ones );
            shaderProgram.bindRotation( zeros );
            shaderProgram.bindOverlayColour( colour );
            shaderProgram.bindMaxAlphaChannel( 0.75f );
        }

    }
}
