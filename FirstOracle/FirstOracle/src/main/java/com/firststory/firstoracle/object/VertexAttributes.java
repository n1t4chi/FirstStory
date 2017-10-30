/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.ArrayBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: n1t4chi
 */
public abstract class VertexAttributes implements Closeable {
    
    private static Map< Integer, Integer > lastBinds = new HashMap<>( 2 );
    
    private static void bindAttributePointer( ArrayBuffer buffer, int index, int vertexSize ) {
        if ( lastBinds.get( index ) == buffer.getBufferID() ) {
            buffer.bind();
            GL20.glVertexAttribPointer( index, vertexSize, GL11.GL_FLOAT, false, 0, 0 );
            lastBinds.put( index, buffer.getBufferID() );
        }
    }
    
    float[][] verticesByFrame;
    ArrayBuffer[] buffers;
    
    public VertexAttributes( float[][] verticesByFrame ) {
        setVertices( verticesByFrame );
    }
    
    /**
     * @param verticesByFrame
     */
    public void setVertices( float[][] verticesByFrame ) {
        if ( buffers != null ) {
            close();
        }
        this.verticesByFrame = verticesByFrame;
        buffers = new ArrayBuffer[verticesByFrame.length];
    }
    
    public void bind( int frame ) {
        checkBuffer( frame );
        bindAttributePointer( buffers[frame], getIndex(), getVertexSize() );
    }
    
    @Override
    public void close() {
        for ( ArrayBuffer buffer : buffers ) {
            buffer.close();
        }
    }
    
    protected abstract int getVertexSize();
    
    protected abstract int getIndex();
    
    private void checkBuffer( int frame ) {
        if ( buffers[frame] == null ) {
            ArrayBuffer buffer = new ArrayBuffer();
            buffer.create();
            buffer.load( verticesByFrame[frame] );
            buffers[frame] = buffer;
        }
    }
}
