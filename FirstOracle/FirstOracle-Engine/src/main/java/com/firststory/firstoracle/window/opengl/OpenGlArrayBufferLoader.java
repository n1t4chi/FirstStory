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
class OpenGlArrayBufferLoader implements ArrayBufferProvider<Integer> {
    private HashSet<Integer> buffers = new HashSet<>();
    private int lastBoundBuffer = 0;
    
    @Override
    public Integer create() throws CannotCreateBufferException {
        int bufferID = GL15.glGenBuffers();
        if ( bufferID == 0 ) {
            throw new CannotCreateBufferException();
        }
        buffers.add( bufferID );
        return bufferID;
    }
    
    @Override
    public void bind( Integer bufferID ) {
        if ( lastBoundBuffer != bufferID ) {
            GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, bufferID );
            lastBoundBuffer = bufferID;
        }
    }
    
    @Override
    public void load( Integer bufferID, float[] bufferData ) {
        bind( bufferID );
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_STATIC_DRAW);
//        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_DYNAMIC_DRAW );
    }
    
    @Override
    public void delete( Integer bufferID ) {
        GL15.glDeleteBuffers( bufferID );
        buffers.remove( bufferID );
    }
    
    @Override
    public void close() {
        new ArrayList<>( buffers ).forEach( this::delete );
    }
}
