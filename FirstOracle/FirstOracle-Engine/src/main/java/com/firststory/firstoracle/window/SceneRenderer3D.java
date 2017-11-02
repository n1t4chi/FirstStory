/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.ArrayBuffer;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderedScene;
import com.firststory.firstoracle.rendering.Renderer3D;
import com.firststory.firstoracle.rendering.SceneProvider;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author: n1t4chi
 */
public class SceneRenderer3D implements Renderer3D {

    protected ShaderProgram3D shaderProgram3D;
    protected ShaderProgram2D shaderProgram2D;
    protected Texture emptyTexture;
    protected int frameCount;
    protected double lastFrameUpdate;
    protected double lastFpsUpdate;
    protected int lastFps;
    protected GridRenderer3D gridRenderer;
    private SceneProvider sceneProvider;

    public SceneRenderer3D(
        ShaderProgram2D shaderProgram2D,
        ShaderProgram3D shaderProgram3D,
        GridRenderer3D gridRenderer,
        SceneProvider sceneProvider
    )
    {
        this.shaderProgram2D = shaderProgram2D;
        this.shaderProgram3D = shaderProgram3D;
        this.gridRenderer = gridRenderer;
        this.sceneProvider = sceneProvider;
    }

    public ShaderProgram2D getShaderProgram2D() {
        return shaderProgram2D;
    }

    public ShaderProgram3D getShaderProgram3D() {
        return shaderProgram3D;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public double getLastFrameUpdate() {
        return lastFrameUpdate;
    }

    public double getLastFpsUpdate() {
        return lastFpsUpdate;
    }

    public int getLastFps() {
        return lastFps;
    }

    public void bindEmptyTexture() {
        emptyTexture.bind();
    }

    @Override
    public void init() {
        try {
            BufferedImage image = new BufferedImage( 50, 50, BufferedImage.TYPE_INT_ARGB );
            Graphics graphics = image.getGraphics();
            graphics.setColor( Color.BLUE );
            graphics.fillRect( 0, 0, 50, 50 );
            graphics.dispose();
            emptyTexture = new Texture( image );
            emptyTexture.load();
            gridRenderer.init();
        } catch ( IOException ex ) {
            throw new RuntimeException( "Can't load texture:", ex );
        }

        frameCount = 0;
        lastFps = 0;
        lastFrameUpdate = GLFW.glfwGetTime();
        lastFpsUpdate = lastFrameUpdate;
        disableDepth();
    }

    @Override
    public void dispose() {
        shaderProgram2D.dispose();
        shaderProgram3D.dispose();
        gridRenderer.dispose();
    }

    @Override
    public void render() {
        enableAttributes();
        frameCount++;
        RenderedScene scene = sceneProvider.getNextScene();
        setBackground( scene );

        disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );
        initialRenderIn2D( scene );

        enableDepth();
        emptyTexture.bind();
        shaderProgram3D.useProgram();
        shaderProgram3D.bindCamera( scene.getIsometricCamera3D() );
        gridRenderer.render();
        renderIn3D( scene );

        disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );
        closingRenderIn2D( scene );

        disableAttributes();
    }

    private void setBackground( RenderedScene scene ) {
        Vector4fc backgroundColour = scene.getBackgroundColour();
        GL11.glClearColor(
            backgroundColour.x(),
            backgroundColour.y(),
            backgroundColour.z(),
            backgroundColour.w()
        );
    }

    private void renderIn3D( RenderedScene scene ) {

    }

    private void initialRenderIn2D( RenderedScene scene ) {
        shaderProgram2D.bindMaxAlphaChannel( 1 );
        shaderProgram2D.bindPosition( new Vector2f( 0, 0 ) );
        shaderProgram2D.bindRotation( 0 );
        shaderProgram2D.bindScale( new Vector2f( 1, 1 ) );
        shaderProgram2D.bindOverlayColour( new Vector4f( 0, 1, 1, 1 ) );

        ArrayBuffer buffer = new ArrayBuffer();
        buffer.create();
        float[] bufferData = { -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, };
        buffer.load( bufferData );
        buffer.bind();
        GL20.glVertexAttribPointer( 0, 2, GL11.GL_FLOAT, false, 0, 0 );

        ArrayBuffer uv = new ArrayBuffer();
        uv.create();
        uv.load( new float[]{ 0, 0, 1, 1, 0, 1 } );
        uv.bind();
        GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
        bindEmptyTexture();

        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, 1 );

        shaderProgram2D.bindOverlayColour( new Vector4f( 1, 0, 0, 0.5f ) );
        GL11.glLineWidth( 2 );
        GL11.glDrawArrays( GL11.GL_LINE_LOOP, 0, 1 );
        uv.delete();
        buffer.delete();
    }

    protected void closingRenderIn2D( RenderedScene scene ) {

    }

    private void enableAttributes() {
        GL20.glEnableVertexAttribArray( 0 );
        GL20.glEnableVertexAttribArray( 1 );
    }

    private void disableAttributes() {
        GL20.glDisableVertexAttribArray( 0 );
        GL20.glDisableVertexAttribArray( 1 );
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
