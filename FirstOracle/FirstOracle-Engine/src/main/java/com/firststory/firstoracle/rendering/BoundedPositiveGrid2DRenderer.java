/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.object2D.Vertices2D;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class BoundedPositiveGrid2DRenderer implements Grid2DRenderer {

    private final float[] interAxesArray;
    private final float[] smallAxesArray;
    private final float[] mainAxesArray;
    private final ShaderProgram2D shaderProgram;
    private final Vertices2D mainAxes;
    private final Vertices2D interAxes;
    private final Vertices2D smallAxes;
    private final Vector2f zeros = new Vector2f( 0, 0 );
    private final Vector2f ones = new Vector2f( 1, 1 );
    private final Vector4f colour = new Vector4f( 0, 0, 0, 0 );

    public BoundedPositiveGrid2DRenderer(
        ShaderProgram2D shaderProgram, int gridWidth, int gridHeight, int interAxesStep
    )
    {
        this.shaderProgram = shaderProgram;

        mainAxesArray = new float[]{ 0, 0, gridWidth, 0, 0, 0, 0, gridHeight };

        int interAxesSize = ( gridWidth / interAxesStep + gridHeight / interAxesStep ) * 4;
        int smallPositiveAxesSize = ( gridHeight + gridWidth ) * 4 - interAxesSize;

        interAxesArray = new float[interAxesSize];
        smallAxesArray = new float[smallPositiveAxesSize];
        int interAxesIt = 0;
        int smallAxesIt = 0;

        for ( int i = 1; i <= gridWidth; i++ ) {
            float[] verticalAxes = createVerticalAxes( i, gridHeight );
            if ( Math.abs( i % interAxesStep ) == 0 ) {
                interAxesIt = addAxesToArray( interAxesIt, interAxesArray, verticalAxes );
            } else {
                smallAxesIt = addAxesToArray( smallAxesIt, smallAxesArray, verticalAxes );
            }
        }
        for ( int i = 1; i <= gridHeight; i++ ) {
            float[] horizontalAxes = createHorizontalAxes( i, gridWidth );
            if ( Math.abs( i % interAxesStep ) == 0 ) {
                interAxesIt = addAxesToArray( interAxesIt, interAxesArray, horizontalAxes );
            } else {
                smallAxesIt = addAxesToArray( smallAxesIt, smallAxesArray, horizontalAxes );
            }
        }
        System.err.println( Arrays.toString( interAxesArray ) );
        System.err.println( "" );
        System.err.println( Arrays.toString( smallAxesArray ) );
        System.err.println( "" );

        mainAxes = new Vertices2D( new float[][]{ mainAxesArray } );
        interAxes = new Vertices2D( new float[][]{ interAxesArray } );
        smallAxes = new Vertices2D( new float[][]{ smallAxesArray } );
    }

    public int addAxesToArray( int interAxesArrayIt, float[] array, float[] axes ) {
        System.arraycopy( axes, 0, array, interAxesArrayIt, axes.length );
        return interAxesArrayIt + axes.length;
    }

    @Override
    public void init() { }

    @Override
    public void dispose() {
        mainAxes.close();
        interAxes.close();
        smallAxes.close();
    }

    @Override
    public void render() {
        renderGridArray( mainAxes, 1f, 1, 0f, 0f, 1f );
        renderGridArray( interAxes, 0.5f, 0f, 0f, 1f, 0.75f );
        renderGridArray( smallAxes, 0.1f, 0.25f, 1f, 0.25f, 0.5f );
    }

    private float[] createHorizontalAxes( int i, int gridWidth ) {
        return new float[]{ 0, i, gridWidth, i };
    }

    private float[] createVerticalAxes( int i, int gridHeight ) {
        return new float[]{ i, 0, i, gridHeight };
    }

    private void renderGridArray(
        Vertices2D buffer, float width, float red, float green, float blue, float alpha
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
        shaderProgram.bindRotation( 0 );
        shaderProgram.bindOverlayColour( colour );
        shaderProgram.bindMaxAlphaChannel( 0.75f );
    }
}
