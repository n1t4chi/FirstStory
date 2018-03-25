/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.CannotCreateBufferException;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
class OpenGlArrayBufferLoader implements AutoCloseable{
    private HashSet<Integer> boundBuffers = new HashSet<>();
    private int lastBoundBuffer = 0;
    
    public int create() throws CannotCreateBufferException {
        int bufferID = GL15.glGenBuffers();
        if ( bufferID == 0 ) {
            throw new CannotCreateBufferException();
        }
        boundBuffers.add( bufferID );
        return bufferID;
    }
    
    public void bind( int bufferID ) {
        if ( lastBoundBuffer != bufferID ) {
            GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, bufferID );
            lastBoundBuffer = bufferID;
        }
    }
    
    public void load( int bufferID, float[] bufferData ) {
        bind( bufferID );
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_STATIC_DRAW);
//        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_DYNAMIC_DRAW );
    }
    
    public void delete( int bufferID ) throws BufferNotCreatedException {
        if ( bufferID == 0 ) {
            throw new BufferNotCreatedException();
        }
        GL15.glDeleteBuffers( bufferID );
        boundBuffers.remove( bufferID );
    }
    
    @Override
    public void close() {
        new ArrayList<>( boundBuffers ).forEach( this::delete );
    }
}
