/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.rendering.FrameworkCommands;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFramework;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class is used to invoke OpenGL that (may) need synchronisation across all instances like rendering etc.
 * Code that uses methods of this instance should be via method {@link #invoke(FrameworkCommands)}:
 * <code>instance.invoke{ instance-&gt; {//method calls on instance\\} }</code>
 */
public class OpenGlFramework implements RenderingFramework, AutoCloseable {
    
    private static final ReentrantLock contextLock = new ReentrantLock(true);
    private final OpenGlArrayBufferLoader bufferLoader = new OpenGlArrayBufferLoader();
    private final OpenGlVertexAttributeLoader attributeLoader = new OpenGlVertexAttributeLoader( bufferLoader );
    private final OpenGlTextureLoader textureLoader = new OpenGlTextureLoader();
    private final OpenGlShaderProgram shader = new OpenGlShaderProgram();
    private final OpenGlRenderingContext renderingContext;
    private final GLCapabilities capabilities;

    OpenGlFramework(){
        capabilities = GL.createCapabilities();
        
        enableFunctionality();
        if ( !OpenGlSupportChecker.isSupportEnough(capabilities) ) {
            throw new RuntimeException( "OpenGL not supported enough to run this engine!" );
        }
        renderingContext = new OpenGlRenderingContext(
            attributeLoader,
            textureLoader,
            shader,
            !PropertiesUtil.isPropertyTrue( PropertiesUtil.DISABLE_TEXTURES_PROPERTY ),
            PropertiesUtil.isPropertyTrue( PropertiesUtil.DRAW_BORDER_PROPERTY )
        );
    }
    
    @Override
    public void updateViewPort( int x, int y, int width, int height ) {
        GL11.glViewport( x, y, width, height );
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
    public void render( Renderer renderer, double lastFrameUpdate ) {
        clearCanvas();
        renderingContext.enableVertexAttributes();
        renderer.render( renderingContext, lastFrameUpdate );
        renderingContext.disableVertexAttributes();
    }
    
    private void clearCanvas() {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT );
    }
    
    @Override
    public void invoke( FrameworkCommands renderingCommands ) throws Exception{
        clearCanvas();
        shader.useProgram();
        try( var instance = acquireLock()){
            renderingCommands.execute( instance );
        }
    }

    @Override
        public void compileShaders() throws IOException {
        shader.compile();
    }
    
    /**
     * Acquires lock across all OpenGlInstances, will block thread until lock is acquired.
     * @return this instance
     */
    private OpenGlFramework acquireLock(){
//        System.err.println("trying to acquire lock by"+Thread.currentThread());
        contextLock.lock();
//        System.err.println("lock acquired by"+Thread.currentThread());
        return this;
    }

    /**
     * Releases lock to other OpenGlInstances
     * releasing lock without prior lock acquisition will fail and exception will be thrown
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
