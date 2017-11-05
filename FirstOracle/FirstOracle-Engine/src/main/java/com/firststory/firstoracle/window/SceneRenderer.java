/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.Object2D;
import com.firststory.firstoracle.object2D.ObjectTransformations2D;
import com.firststory.firstoracle.object3D.Object3D;
import com.firststory.firstoracle.object3D.ObjectTransformations3D;
import com.firststory.firstoracle.rendering.GraphicRenderer;
import com.firststory.firstoracle.rendering.Object2DRenderer;
import com.firststory.firstoracle.rendering.Object3DRenderer;
import com.firststory.firstoracle.rendering.SceneProvider;
import com.firststory.firstoracle.scene.RenderedScene;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author: n1t4chi
 */
public class SceneRenderer implements GraphicRenderer,
    Object2DRenderer,
    Object3DRenderer, FpsNotifier
{

    public static final Vector4f BORDER_COLOUR = new Vector4f( 1f, 0f, 0f, 0.75f );
    private final boolean useTexture;
    private final boolean drawBorder;
    private ShaderProgram3D shaderProgram3D;
    private ShaderProgram2D shaderProgram2D;
    private Texture emptyTexture;
    private int frameCount;
    private double lastFrameUpdate;
    private double lastFpsUpdate;
    private int lastFps;
    private GridRenderer gridRenderer;
    private SceneProvider sceneProvider;
    private ArrayList< FpsObserver > observers = new ArrayList<>( 5 );

    public SceneRenderer(
        ShaderProgram2D shaderProgram2D,
        ShaderProgram3D shaderProgram3D,
        GridRenderer gridRenderer,
        SceneProvider sceneProvider,
        boolean useTexture,
        boolean drawBorder
    )
    {
        this.shaderProgram2D = shaderProgram2D;
        this.shaderProgram3D = shaderProgram3D;
        this.gridRenderer = gridRenderer;
        this.sceneProvider = sceneProvider;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
    }

    public int getFpsCount() {
        return lastFps;
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
        lastFrameUpdate = GLFW.glfwGetTime();
        if ( frameCount % 100 == 0 ) {
            lastFps = ( int ) ( ( float ) frameCount / (lastFrameUpdate-lastFpsUpdate) );
            lastFpsUpdate = lastFrameUpdate;
            frameCount = 0;
            notifyObservers( lastFps );
        }
        frameCount++;

        RenderedScene scene = sceneProvider.getNextScene();

        setBackground( scene );
        renderBackgroundAnd2dScene( scene );
        render3dScene( scene );
        renderOverlay( scene );

        disableAttributes();
    }

    @Override
    public Collection< FpsObserver > getObservers() {
        return observers;
    }

    @Override
    public void render(
        Object2D object,
        Vector2fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    )
    {
        shaderProgram2D.bindPosition( objectPosition );
        shaderProgram2D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram2D.bindOverlayColour( objectOverlayColour );

        ObjectTransformations2D transformations = object.getTransformations();
        shaderProgram2D.bindRotation( transformations.getRotation() );
        shaderProgram2D.bindScale( transformations.getScale() );

        int bufferSize = object.bindCurrentVerticesAndGetSize();
        object.bindCurrentUvMap();

        if ( useTexture ) { object.getTexture().bind(); }

        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, bufferSize );

        if ( drawBorder ) {
            shaderProgram3D.bindMaxAlphaChannel( 1 );
            shaderProgram3D.bindOverlayColour( BORDER_COLOUR );
            GL11.glLineWidth( 1 );
            GL11.glDrawArrays( GL11.GL_LINE_LOOP, 0, bufferSize );
        }
    }

    @Override
    public void render(
        Object3D object,
        Vector3fc objectPosition,
        Vector4fc objectOverlayColour,
        float maxAlphaChannel
    )
    {
        shaderProgram3D.bindPosition( objectPosition );
        shaderProgram3D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram3D.bindOverlayColour( objectOverlayColour );

        ObjectTransformations3D transformations = object.getTransformations();
        shaderProgram3D.bindRotation( transformations.getRotation() );
        shaderProgram3D.bindScale( transformations.getScale() );

        int bufferSize = object.bindCurrentVerticesAndGetSize();
        object.bindCurrentUvMap();

        if ( useTexture ) { object.getTexture().bind(); }

        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, bufferSize );

        if ( drawBorder ) {
            shaderProgram3D.bindMaxAlphaChannel( 1 );
            shaderProgram3D.bindOverlayColour( BORDER_COLOUR );
            GL11.glLineWidth( 1 );
            GL11.glDrawArrays( GL11.GL_LINE_LOOP, 0, bufferSize );
        }
    }

    private void renderOverlay( RenderedScene scene ) {
        disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );
        scene.renderOverlay( this );
    }

    private void render3dScene( RenderedScene scene ) {
        enableDepth();
        shaderProgram3D.useProgram();
        shaderProgram3D.bindCamera( scene.getCamera3D() );

        emptyTexture.bind();
        gridRenderer.render();

        scene.renderScene3D( this );
    }

    private void renderBackgroundAnd2dScene( RenderedScene scene ) {
        disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );

        scene.renderBackground( this );
        scene.renderScene2D( this );
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
