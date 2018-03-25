/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to invoke OpenGL that (may) need synchronisation across all instances like rendering etc.
 * Code that uses methods of this instance should be via method {@link #invoke(Commands)}:
 * <code>instance.invoke{ instance-> {//method calls on instance\\} }</code>
 */
public class OpenGlInstance implements AutoCloseable{
    
    private static final ReentrantLock contextLock = new ReentrantLock(true);
    private GLCapabilities capabilities;
    private final OpenGlArrayBufferLoader bufferLoader = new OpenGlArrayBufferLoader();
    private final OpenGLVertexAttributeLoader attributeLoader = new OpenGLVertexAttributeLoader( bufferLoader );
    
    OpenGlInstance(){
        capabilities = GL.createCapabilities();
        enableFunctionality();
        if ( !OpenGlSupportChecker.isSupportEnough(capabilities) ) {
            throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
        }
    }
    
    public OpenGLVertexAttributeLoader getAttributeLoader() {
        return attributeLoader;
    }
    
    public OpenGlArrayBufferLoader getBufferLoader() {
        return bufferLoader;
    }
    
    public void clearScreen() {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
    }

    public void updateViewPort( int x, int y, int width, int height ) {
        GL11.glViewport( x, y, width, height );
    }
    
    public void setCurrentCapabilitesToThisThread(){
        GL.setCapabilities( capabilities );
    }
    
    /**
     * Aquires lock across all OpenGlInstances, will block thread until lock is aquired.
     * @return this instance
     */
    private OpenGlInstance aquireLock(){
//        System.err.println("trying to aquire lock by"+Thread.currentThread());
//        contextLock.lock();
//        System.err.println("lock aquired by"+Thread.currentThread());
        return this;
    }
    
    /**
     * Releases lock to other OpenGlInstances
     * releasing lock without prior lock aquisition will fail and exception will be thrown
     */
    private void releaseLock(){
//        contextLock.unlock();
//        System.err.println("lock released by"+Thread.currentThread());
    }
    
    /**
     * Releases lock to other OpenGlInstances
     * @see #releaseLock()
     */
    @Override
    public void close() {
        releaseLock();
    }

    public void invoke( Commands commands) throws Exception{
        try(OpenGlInstance instance = aquireLock()){
            commands.execute( instance );
        }
    }
    
    private void enableFunctionality() {
        GL11.glEnable( GL11.GL_CULL_FACE );
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glEnable( GL11.GL_BLEND );
        GL11.glEnable( GL11.GL_TEXTURE_2D );
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glFrontFace( GL11.GL_CCW );
        ARBVertexArrayObject.glBindVertexArray( ARBVertexArrayObject.glGenVertexArrays() );
    }
    
    public interface Commands {
        void execute( OpenGlInstance openGlInstance ) throws Exception;
    }
    
    
}
