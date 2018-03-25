/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.object2D.Object2D;
import com.firststory.firstoracle.object2D.Object2DTransformations;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.Object3D;
import com.firststory.firstoracle.object3D.Object3DTransformations;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.scene.RenderedScene;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author n1t4chi
 */
public class WindowRenderingContext implements RenderingContext {
    
    private static final Vector4f BORDER_COLOUR = new Vector4f( 1f, 0f, 0f, 0.75f );
    private final boolean useTexture;
    private final boolean drawBorder;
    private final ShaderProgram2D shaderProgram2D;
    private final ShaderProgram3D shaderProgram3D;
    private final Grid2DRenderer grid2DRenderer;
    private final Grid3DRenderer grid3DRenderer;
    private final SceneProvider sceneProvider;
    private final Multi2DRenderer object2DRenderer = new Object2DRendererImpl();
    private final Multi3DRenderer object3DRenderer = new Object3DRendererImpl();
    private UvMap emptyUvMap;
    private Texture emptyTexture;
    private double cameraRotation2D;
    private double cameraRotation3D;
    private VertexAttributeLoader loader;
    private double currentRenderTime;
    
    public WindowRenderingContext(
        ShaderProgram2D shaderProgram2D,
        ShaderProgram3D shaderProgram3D,
        Grid2DRenderer grid2DRenderer,
        Grid3DRenderer grid3DRenderer,
        SceneProvider sceneProvider,
        boolean useTexture,
        boolean drawBorder
    )
    {
        this.shaderProgram2D = shaderProgram2D;
        this.shaderProgram3D = shaderProgram3D;
        this.grid2DRenderer = grid2DRenderer;
        this.grid3DRenderer = grid3DRenderer;
        this.sceneProvider = sceneProvider;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
    }
    
    public ShaderProgram2D getShaderProgram2D() {
        return shaderProgram2D;
    }
    
    public ShaderProgram3D getShaderProgram3D() {
        return shaderProgram3D;
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
            emptyUvMap = new UvMap( new float[1][1][3] );
            grid3DRenderer.init();
            grid2DRenderer.init();
        } catch ( IOException ex ) {
            throw new RuntimeException( "Can't load texture:", ex );
        }
        disableDepth();
    }
    
    @Override
    public void dispose() {
        shaderProgram2D.dispose();
        shaderProgram3D.dispose();
        grid3DRenderer.dispose();
        grid2DRenderer.dispose();
    }
    
    @Override
    public void render( VertexAttributeLoader loader, double currentRenderTime ) {
        this.loader = loader;
        this.currentRenderTime = currentRenderTime;
        enableAttributes();
        
        RenderedScene scene = sceneProvider.getNextScene();
        cameraRotation2D = scene.getCamera2D().getGeneralRotation();
        cameraRotation3D = scene.getCamera3D().getGeneralRotation();
        
        setBackground( scene );
        renderBackgroundAnd2dScene( loader, scene );
        render3dScene( loader, scene );
        renderOverlay( scene );
        
        disableAttributes();
    }
    
    private void render2DObject(
        Object2D object, Vector2fc objectPosition, Vector4fc objectOverlayColour, float maxAlphaChannel
    )
    {
        shaderProgram2D.bindPosition( objectPosition );
        shaderProgram2D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram2D.bindOverlayColour( objectOverlayColour );
        
        Object2DTransformations transformations = object.getTransformations();
        shaderProgram2D.bindRotation( transformations.getRotation() );
        shaderProgram2D.bindScale( transformations.getScale() );
        
        int bufferSize = object.bindCurrentVerticesAndGetSize( loader, currentRenderTime );
        object.bindCurrentUvMap( loader, currentRenderTime, cameraRotation2D );
        
        if ( useTexture ) {
            object.getTexture().bind();
        }
        
        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, bufferSize );
        
        if ( drawBorder ) {
            shaderProgram2D.bindMaxAlphaChannel( 1 );
            shaderProgram2D.bindOverlayColour( BORDER_COLOUR );
            GL11.glLineWidth( 1f );
            GL11.glDrawArrays( GL11.GL_LINE_LOOP, 0, bufferSize );
        }
    }
    
    private void render3DObject(
        Object3D object, Vector3fc objectPosition, Vector4fc objectOverlayColour, float maxAlphaChannel
    )
    {
        shaderProgram3D.bindMaxAlphaChannel( maxAlphaChannel );
        shaderProgram3D.bindOverlayColour( objectOverlayColour );
        
        shaderProgram3D.bindPosition( objectPosition );
        Object3DTransformations transformations = object.getTransformations();
        shaderProgram3D.bindRotation( transformations.getRotation() );
        shaderProgram3D.bindScale( transformations.getScale() );
        
        int bufferSize = object.bindCurrentVerticesAndGetSize( loader, currentRenderTime );
        object.bindCurrentUvMap( loader, currentRenderTime, cameraRotation3D );
        
        if ( useTexture ) {
            object.getTexture().bind();
        }
        
        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, bufferSize );
        
        if ( drawBorder ) {
            shaderProgram3D.bindMaxAlphaChannel( 1 );
            shaderProgram3D.bindOverlayColour( BORDER_COLOUR );
            GL11.glLineWidth( 1 );
            GL11.glDrawArrays( GL11.GL_LINE_LOOP, 0, bufferSize );
        }
    }
    
    private void enableAttributes() {
        GL20.glEnableVertexAttribArray( 0 );
        GL20.glEnableVertexAttribArray( 1 );
    }
    
    private void setBackground( RenderedScene scene ) {
        Vector4fc backgroundColour = scene.getBackgroundColour();
        GL11.glClearColor( backgroundColour.x(), backgroundColour.y(), backgroundColour.z(), backgroundColour.w() );
    }
    
    private void renderBackgroundAnd2dScene( VertexAttributeLoader loader, RenderedScene scene ) {
        disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );
        scene.renderBackground( object2DRenderer );
        renderGrid2D( loader );
        scene.renderScene2D( object2DRenderer );
    }
    
    private void renderGrid2D( VertexAttributeLoader loader ) {
        emptyTexture.bind();
        emptyUvMap.bind( loader,0, 0 );
        grid2DRenderer.render( loader, currentRenderTime );
    }
    
    private void render3dScene( VertexAttributeLoader loader, RenderedScene scene ) {
        enableDepth();
        shaderProgram3D.useProgram();
        shaderProgram3D.bindCamera( scene.getCamera3D() );
        renderGrid3D( loader );
        scene.renderScene3D( object3DRenderer );
    }
    
    private void renderGrid3D( VertexAttributeLoader loader ) {
        emptyTexture.bind();
        emptyUvMap.bind( loader,0, 0 );
        grid3DRenderer.render( loader, currentRenderTime );
    }
    
    private void renderOverlay( RenderedScene scene ) {
        disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( IdentityCamera2D.getCamera() );
        scene.renderOverlay( object2DRenderer );
    }
    
    private void disableAttributes() {
        GL20.glDisableVertexAttribArray( 0 );
        GL20.glDisableVertexAttribArray( 1 );
    }
    
    private void enableDepth() {
        GL11.glDepthMask( true );
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    private void disableDepth() {
        GL11.glDepthMask( false );
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    private class Object2DRendererImpl implements Multi2DRenderer {
    
        @Override
        public void renderObject(
            Object2D object,
            Vector2fc objectPosition,
            Vector4fc objectOverlayColour,
            float maxAlphaChannel
        )
        {
            render2DObject( object, objectPosition, objectOverlayColour, maxAlphaChannel );
        }
    
        @Override
        public void renderTerrain(
            Terrain2D terrain,
            Vector2fc terrainPosition,
            Vector4fc terrainOverlayColour,
            float maxAlphaChannel
        )
        {
            render2DObject( terrain, terrainPosition, terrainOverlayColour, maxAlphaChannel );
        }
    }
    
    private class Object3DRendererImpl implements Multi3DRenderer {
        
        @Override
        public void renderObject(
            Object3D object,
            Vector3fc objectPosition,
            Vector4fc objectOverlayColour,
            float maxAlphaChannel
        )
        {
            render3DObject( object, object.getTransformations().getPosition(), objectOverlayColour, maxAlphaChannel );
        }
        
        @Override
        public void renderTerrain(
            Terrain3D terrain,
            Vector3fc terrainPosition,
            Vector4fc terrainOverlayColour,
            float maxAlphaChannel
        )
        {
            render3DObject( terrain, terrainPosition, terrainOverlayColour, maxAlphaChannel );
        }
    }
}
