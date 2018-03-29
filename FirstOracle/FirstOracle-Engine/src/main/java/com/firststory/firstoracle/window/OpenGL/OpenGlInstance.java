/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.OpenGL;

import com.firststory.firstoracle.data.ArrayBufferLoader;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to invoke OpenGL that (may) need synchronisation across all instances like rendering etc.
 * Code that uses methods of this instance should be via method {@link #invoke(Commands)}:
 * <code>instance.invoke{ instance-> {//method calls on instance\\} }</code>
 */
public class OpenGlInstance implements AutoCloseable{
    
    private static final ReentrantLock contextLock = new ReentrantLock(true);
    private final ArrayBufferLoader bufferLoader = new OpenGlArrayBufferLoader();
    private final OpenGLVertexAttributeLoader attributeLoader = new OpenGLVertexAttributeLoader( bufferLoader );
    private final OpenGlTextureLoader textureLoader = new OpenGlTextureLoader();
    private final OpenGlShaderProgram2D shader2D = new OpenGlShaderProgram2D(  );
    private final OpenGlShaderProgram3D shader3D = new OpenGlShaderProgram3D(  );
    private final OpenGlRenderingContext renderingContext;
    private GLCapabilities capabilities;

    OpenGlInstance(){
        capabilities = GL.createCapabilities();
        enableFunctionality();
        if ( !OpenGlSupportChecker.isSupportEnough(capabilities) ) {
            throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
        }
        boolean drawBorder = "true".equals( System.getProperty( "DrawBorder", "false" ) );
        renderingContext = new OpenGlRenderingContext(
            attributeLoader,
            textureLoader,
            shader2D,
            shader3D,
            "true".equals( System.getProperty( "UseTexture", "true" ) ),
            drawBorder,
            new Vector4f( 1,0,0,1 )
        );
    }

    public OpenGlRenderingContext getRenderingContext() {
        return renderingContext;
    }

    public OpenGlShaderProgram2D getShader2D() {
        return shader2D;
    }

    public OpenGlShaderProgram3D getShader3D() {
        return shader3D;
    }
    
    public OpenGlTextureLoader getTextureLoader() {
        return textureLoader;
    }
    
    public OpenGLVertexAttributeLoader getAttributeLoader() {
        return attributeLoader;
    }
    
    public ArrayBufferLoader getBufferLoader() {
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

    public void compileShaders() throws IOException {
        shader2D.compile();
        shader3D.compile();
    }
    
    /**
     * Aquires lock across all OpenGlInstances, will block thread until lock is aquired.
     * @return this instance
     */
    private OpenGlInstance aquireLock(){
//        System.err.println("trying to aquire lock by"+Thread.currentThread());
        contextLock.lock();
//        System.err.println("lock aquired by"+Thread.currentThread());
        return this;
    }

    /**
     * Releases lock to other OpenGlInstances
     * releasing lock without prior lock aquisition will fail and exception will be thrown
     */
    private void releaseLock(){
        contextLock.unlock();
//        System.err.println("lock released by"+Thread.currentThread());
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
