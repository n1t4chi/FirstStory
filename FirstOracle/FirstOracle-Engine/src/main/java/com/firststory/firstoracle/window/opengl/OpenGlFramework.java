/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.data.ArrayBufferProvider;
import com.firststory.firstoracle.rendering.RenderingCommands;
import com.firststory.firstoracle.rendering.RenderingFramework;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to invoke OpenGL that (may) need synchronisation across all instances like rendering etc.
 * Code that uses methods of this instance should be via method {@link #invoke(RenderingCommands)}:
 * <code>instance.invoke{ instance-&gt; {//method calls on instance\\} }</code>
 */
public class OpenGlFramework implements RenderingFramework {
    
    private static final ReentrantLock contextLock = new ReentrantLock(true);
    private final ArrayBufferProvider bufferLoader = new OpenGlArrayBufferLoader();
    private final OpenGLVertexAttributeLoader attributeLoader = new OpenGLVertexAttributeLoader( bufferLoader );
    private final OpenGlTextureLoader textureLoader = new OpenGlTextureLoader();
    private final OpenGlShaderProgram2D shader2D = new OpenGlShaderProgram2D(  );
    private final OpenGlShaderProgram3D shader3D = new OpenGlShaderProgram3D(  );
    private final OpenGlRenderingContext renderingContext;
    private GLCapabilities capabilities;

    OpenGlFramework(){
        capabilities = GL.createCapabilities();
        
        enableFunctionality();
        if ( !OpenGlSupportChecker.isSupportEnough(capabilities) ) {
            throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
        }
        boolean drawBorder = PropertiesUtil.isPropertyTrue( "DrawBorder" );
        renderingContext = new OpenGlRenderingContext(
            attributeLoader,
            textureLoader,
            shader2D,
            shader3D,
            !PropertiesUtil.isPropertyTrue( PropertiesUtil.DISABLE_TEXTURES_PROPERTY ),
            drawBorder,
            new Vector4f( 1,0,0,1 )
        );
    }

    @Override
    public OpenGlRenderingContext getRenderingContext() {
        return renderingContext;
    }

    @Override
    public OpenGlShaderProgram2D getShader2D() {
        return shader2D;
    }

    @Override
    public OpenGlShaderProgram3D getShader3D() {
        return shader3D;
    }
    
    @Override
    public OpenGlTextureLoader getTextureLoader() {
        return textureLoader;
    }
    
    @Override
    public OpenGLVertexAttributeLoader getAttributeLoader() {
        return attributeLoader;
    }
    
    @Override
    public ArrayBufferProvider getBufferLoader() {
        return bufferLoader;
    }
    
    @Override
    public void clearScreen() {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
    }

    @Override
    public void updateViewPort( int x, int y, int width, int height ) {
        GL11.glViewport( x, y, width, height );
    }
    
    @Override
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
    
    @Override
    public void invoke( RenderingCommands renderingCommands ) throws Exception{
        try(OpenGlFramework instance = aquireLock()){
            renderingCommands.execute( instance );
        }
    }

    @Override
        public void compileShaders() throws IOException {
        shader2D.compile();
        shader3D.compile();
    }
    
    /**
     * Aquires lock across all OpenGlInstances, will block thread until lock is aquired.
     * @return this instance
     */
    private OpenGlFramework aquireLock(){
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
    
}
