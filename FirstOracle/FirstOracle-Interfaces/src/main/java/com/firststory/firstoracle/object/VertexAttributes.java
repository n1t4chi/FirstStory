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
 * @author n1t4chi
 */
public abstract class VertexAttributes implements Closeable {
    
    private static final Map< Integer, Integer > lastBinds = new HashMap<>( 2 );

    static void bindAttributePointer( ArrayBuffer buffer, int index, int vertexSize ) {
        Integer integer = lastBinds.get( index );
        if ( integer == null || integer != buffer.getBufferID() ) {
            buffer.bind();
            GL20.glVertexAttribPointer( index, vertexSize, GL11.GL_FLOAT, false, 0, 0 );
            lastBinds.put( index, buffer.getBufferID() );
        }
    }
    private final HashMap< Long, ArrayBuffer > buffers = new HashMap<>();
    
    @Override
    public void close() {
        for ( ArrayBuffer buffer : buffers.values() ) {
            buffer.close();
        }
        buffers.clear();
    }
    
    protected int bindBufferAndGetSize( long key ) {
        ArrayBuffer buffer = getBuffer( key );
        bindAttributePointer( buffer, getIndex(), getVertexSize() );
        return getVertexLength( buffer );
    }
    
    protected abstract int getIndex();
    
    protected abstract int getVertexSize();
    
    protected int getVertexLength( ArrayBuffer buffer ) {
        return buffer.getLength() / getVertexSize();
    }
    
    protected abstract float[] getArray( long key );
    
    protected int getVertexLength( long key ) {
        return buffers.get( key ).getLength() / getVertexSize();
    }
    
    private ArrayBuffer getBuffer( long key ) {
        ArrayBuffer buffer = buffers.get( key );
        if ( buffer == null ) {
            buffer = new ArrayBuffer();
            buffer.create();
            buffer.load( getArray( key ) );
            buffers.put( key, buffer );
        }
        return buffer;
    }
}
