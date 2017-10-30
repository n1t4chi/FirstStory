/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.IdentityCamera;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.window.shader.ShaderProgram;
import com.sun.prism.es2.JFXGLContext;
import cuchaz.jfxgl.controls.OpenGLPane;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author: n1t4chi
 */
public class Renderer implements OpenGLPane.Renderer {

    private static final Camera IDENTITY_CAMERA = new IdentityCamera();
    private ShaderProgram shaderProgram;
    private Texture emptyTexture;
    private int frameCount;
    private double lastFrameUpdate;
    private double lastFpsUpdate;
    private int lastFps;
    private GridRenderer gridRenderer;
    private SceneProvider sceneProvider;

    public Renderer(
        ShaderProgram shaderProgram, GridRenderer gridRenderer, SceneProvider sceneProvider
    )
    {
        this.shaderProgram = shaderProgram;
        this.gridRenderer = gridRenderer;
        this.sceneProvider = sceneProvider;
    }

    public void dispose() {
        shaderProgram.dispose();
    }

    public void init() {
        try {
            emptyTexture = new Texture(
                new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB )
            );
            emptyTexture.load();
        }
        catch ( IOException ex ) {
            throw new RuntimeException( "Can't load texture:", ex );
        }

        frameCount = 0;
        lastFps = 0;
        lastFrameUpdate = GLFW.glfwGetTime();
        lastFpsUpdate = lastFrameUpdate;

        GL11.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
        enableAttributes();

        disableDepth();
        shaderProgram.bindCamera( IDENTITY_CAMERA );
    }

    @Override
    public void render( JFXGLContext context ) {
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT ); // clear the framebuffer
        frameCount++;
        RenderedScene scene = sceneProvider.getNextScene();

        initialRenderIn2D( scene );

        enableDepth();
        shaderProgram.bindCamera( scene.getCamera() );
        gridRenderer.render( context );
        renderIn3D( scene );

        disableDepth();
        shaderProgram.bindCamera( IDENTITY_CAMERA );
        closingRenderIn2D( scene );

    }

    private void closingRenderIn2D( RenderedScene scene ) {
    }

    private void renderIn3D( RenderedScene scene ) {
    }

    private void initialRenderIn2D( RenderedScene scene ) {
    }

    private void enableAttributes() {
        GL20.glEnableVertexAttribArray( 0 );
        GL20.glEnableVertexAttribArray( 1 );
    }

    private void enableDepth()
    {
        GL11.glDepthMask( true );
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }

    private void disableDepth()
    {
        GL11.glDepthMask( false );
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
}
