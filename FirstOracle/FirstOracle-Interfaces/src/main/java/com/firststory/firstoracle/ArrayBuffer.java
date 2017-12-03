/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle;

import org.lwjgl.opengl.GL15;

import java.io.Closeable;

/**
 * @author n1t4chi
 */
public class ArrayBuffer implements Closeable {
    
    private static int lastBoundBuffer = 0;

    private static int createBuffer() throws CannotCreateBufferException {
        int bufferID = GL15.glGenBuffers();
        if ( bufferID == 0 ) {
            throw new CannotCreateBufferException();
        }
        return bufferID;
    }

    private static void bindBuffer( int bufferID ) {
        if ( lastBoundBuffer != bufferID ) {
            GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, bufferID );
            lastBoundBuffer = bufferID;
        }
    }

    private static void loadArrayBuffer( int bufferID, float[] bufferData ) {
        bindBuffer( bufferID );
        //GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_STATIC_DRAW);
        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_DYNAMIC_DRAW );
    }
    
    private static void deleteBuffer( int bufferID, boolean checkBuffer ) throws
        BufferNotCreatedException {
        if ( checkBuffer && bufferID == 0 ) {
            throw new BufferNotCreatedException();
        }
        GL15.glDeleteBuffers( bufferID );
    }
    private int bufferID = 0;
    private boolean loaded = false;
    private int length;
    
    public int getLength() {
        return length;
    }

    public int getBufferID() {
        return bufferID;
    }
    
    public boolean isCreated() {
        return bufferID != 0;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public void create() throws CannotCreateBufferException {
        bufferID = ArrayBuffer.createBuffer();
    }
    
    public void bind() throws BufferNotCreatedException, BufferNotLoadedException {
        assertCreated();
        assertLoaded();
        ArrayBuffer.bindBuffer( bufferID );
    }
    
    public void load( float[] bufferData ) throws BufferNotCreatedException {
        assertCreated();
        ArrayBuffer.bindBuffer( bufferID );
        ArrayBuffer.loadArrayBuffer( bufferID, bufferData );
        length = bufferData.length;
        loaded = true;
    }
    
    public void delete() throws BufferNotCreatedException {
        deleteBuffer( bufferID, true );
        cleanUpFields();
    }
    
    @Override
    public void close() {
        deleteBuffer( bufferID, false );
        cleanUpFields();
    }
    
    private void assertCreated() {
        if ( bufferID < 1 ) {
            throw new BufferNotCreatedException();
        }
    }
    
    private void assertLoaded() {
        if ( !loaded ) {
            throw new BufferNotLoadedException();
        }
    }
    
    private void cleanUpFields() {
        bufferID = 0;
        length = 0;
        loaded = false;
    }
    
    public static class BufferException extends RuntimeException {
    }
    
    public static class BufferNotCreatedException extends BufferException {
    }
    
    public static class BufferNotLoadedException extends BufferException {
    }
    
    public static class CannotCreateBufferException extends BufferException {
    }
}
