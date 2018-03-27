/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.object2D.Object2D;
import com.firststory.firstoracle.object2D.Object2DTransformations;
import com.firststory.firstoracle.object2D.Terrain2D;
import com.firststory.firstoracle.object3D.Object3D;
import com.firststory.firstoracle.object3D.Object3DTransformations;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.scene.RenderedScene;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author n1t4chi
 */
public class WindowRenderer implements Renderer {
    
    private static final Vector4f BORDER_COLOUR = new Vector4f( 1f, 0f, 0f, 0.75f );
    private final boolean useTexture;
    private final boolean drawBorder;
    private final Grid2DRenderer grid2DRenderer;
    private final Grid3DRenderer grid3DRenderer;
    private final SceneProvider sceneProvider;
    private final Multi2DRenderer object2DRenderer = new Object2DRendererImpl();
    private final Multi3DRenderer object3DRenderer = new Object3DRendererImpl();
    private UvMap emptyUvMap;
    private Texture emptyTexture;
    private double cameraRotation2D;
    private double cameraRotation3D;
    private RenderingContext renderingContext;
    private double currentRenderTime;
    private ShaderProgram2D shaderProgram2D;
    private ShaderProgram3D shaderProgram3D;
    
    public WindowRenderer(
        Grid2DRenderer grid2DRenderer,
        Grid3DRenderer grid3DRenderer,
        SceneProvider sceneProvider,
        boolean useTexture,
        boolean drawBorder
    )
    {
        this.grid2DRenderer = grid2DRenderer;
        this.grid3DRenderer = grid3DRenderer;
        this.sceneProvider = sceneProvider;
        this.useTexture = useTexture;
        this.drawBorder = drawBorder;
    }
    
    public void bindEmptyTexture( TextureBufferLoader loader ) {
        emptyTexture.bind( loader );
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
            emptyUvMap = new UvMap( new float[1][1][3] );
            grid3DRenderer.init();
            grid2DRenderer.init();
        } catch ( IOException ex ) {
            throw new RuntimeException( "Can't load texture:", ex );
        }
    }
    
    @Override
    public void dispose() {
        grid3DRenderer.dispose();
        grid2DRenderer.dispose();
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {
        this.renderingContext = renderingContext;
        this.currentRenderTime = currentRenderTime;
        this.shaderProgram2D = renderingContext.getShaderProgram2D();
        this.shaderProgram3D = renderingContext.getShaderProgram3D();

        renderingContext.enableVertexAttributes();
        
        RenderedScene scene = sceneProvider.getNextScene();
        cameraRotation2D = scene.getCamera2D().getGeneralRotation();
        cameraRotation3D = scene.getCamera3D().getGeneralRotation();
        
        setBackground( scene );
        renderBackgroundAnd2dScene( scene );
        render3dScene( scene );
        renderOverlay( scene );
        
        renderingContext.disableVertexAttributes();
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
        
        int bufferSize = object.bindCurrentVerticesAndGetSize(
            renderingContext.getVertexAttributeLoader(), currentRenderTime );
        object.bindCurrentUvMap( renderingContext.getVertexAttributeLoader(), currentRenderTime, cameraRotation2D );
        
        if ( useTexture ) {
            object.getTexture().bind( renderingContext.getTextureLoader() );
        }
    
        renderingContext.drawTriangles( bufferSize );
        
        if ( drawBorder ) {
            shaderProgram2D.bindMaxAlphaChannel( 1 );
            shaderProgram2D.bindOverlayColour( BORDER_COLOUR );
            renderingContext.setLineWidth( 1f );
            renderingContext.drawLineLoop( bufferSize );
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
        
        int bufferSize = object.bindCurrentVerticesAndGetSize(
            renderingContext.getVertexAttributeLoader(), currentRenderTime );
        object.bindCurrentUvMap( renderingContext.getVertexAttributeLoader(), currentRenderTime, cameraRotation3D );
        
        if ( useTexture ) {
            object.getTexture().bind( renderingContext.getTextureLoader() );
        }
    
        renderingContext.drawTriangles( bufferSize );
        
        if ( drawBorder ) {
            shaderProgram3D.bindMaxAlphaChannel( 1 );
            shaderProgram3D.bindOverlayColour( BORDER_COLOUR );
            renderingContext.setLineWidth( 1f );
            renderingContext.drawLineLoop( bufferSize );
        }
    }
    
    private void setBackground( RenderedScene scene ) {
        renderingContext.setBackgroundColour( scene.getBackgroundColour() );
    }
    
    private void renderBackgroundAnd2dScene( RenderedScene scene ) {
        renderingContext.disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );
        scene.renderBackground( object2DRenderer );
        renderGrid2D();
        scene.renderScene2D( object2DRenderer );
    }
    
    private void renderGrid2D() {
        emptyTexture.bind( renderingContext.getTextureLoader() );
        emptyUvMap.bind( renderingContext.getVertexAttributeLoader(),0, 0 );
        grid2DRenderer.render( renderingContext, currentRenderTime );
    }
    
    private void render3dScene( RenderedScene scene ) {
        renderingContext.enableDepth();
        shaderProgram3D.useProgram();
        shaderProgram3D.bindCamera( scene.getCamera3D() );
        renderGrid3D();
        scene.renderScene3D( object3DRenderer );
    }
    
    private void renderGrid3D(  ) {
        emptyTexture.bind( renderingContext.getTextureLoader() );
        emptyUvMap.bind( renderingContext.getVertexAttributeLoader(),0, 0 );
        grid3DRenderer.render( renderingContext, currentRenderTime );
    }
    
    private void renderOverlay( RenderedScene scene ) {
        renderingContext.disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( IdentityCamera2D.getCamera() );
        scene.renderOverlay( object2DRenderer );
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
