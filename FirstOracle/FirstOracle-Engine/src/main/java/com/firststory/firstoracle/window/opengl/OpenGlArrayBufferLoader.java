/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.data.ArrayBufferProvider;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
class OpenGlArrayBufferLoader implements ArrayBufferProvider<OpenGlArrayBuffer> {
    private final HashSet< OpenGlArrayBuffer > buffers = new HashSet<>();
    private int lastBoundBuffer = 0;
    
    @Override
    public OpenGlArrayBuffer create( float[] array ) throws CannotCreateBufferException {
        var buffer = new OpenGlArrayBuffer( this );
        buffers.add( buffer );
        buffer.create();
        buffer.load( array );
        return buffer;
    }
    
    int createBuffer() {
        var bufferID = GL15.glGenBuffers();
        if ( bufferID == 0 ) {
            throw new CannotCreateBufferException();
        }
        return bufferID;
    }
    
    void bind( Integer bufferID ) {
        if ( lastBoundBuffer != bufferID ) {
            GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, bufferID );
            lastBoundBuffer = bufferID;
        }
    }
    
    void load( Integer bufferID, float[] bufferData ) {
        bind( bufferID );
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_STATIC_DRAW);
//        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_DYNAMIC_DRAW );
    }
    
    void delete( Integer bufferID ) {
        GL15.glDeleteBuffers( bufferID );
    }
    
    @Override
    public void close() {
        new ArrayList<>( buffers ).forEach( OpenGlArrayBuffer::delete );
        buffers.clear();
    }
}
