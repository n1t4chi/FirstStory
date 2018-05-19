/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.scene.RenderedScene;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Vector4f;

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
    private UvMap emptyUvMap;
    private Texture emptyTexture;
    private MutableCameraDataProvider cameraDataProvider = new MutableCameraDataProvider();
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
    ) {
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
            emptyTexture = new Texture( createEmptyTexture() );
            emptyUvMap = new UvMap( new float[1][1][3] );
            grid3DRenderer.init();
            grid2DRenderer.init();
        } catch ( IOException ex ) {
            throw new RuntimeException( "Can't load texture:", ex );
        }
    }
    
    private BufferedImage createEmptyTexture() {
        BufferedImage image = new BufferedImage( 50, 50, BufferedImage.TYPE_INT_ARGB );
        Graphics graphics = image.getGraphics();
        graphics.setColor( Color.BLUE );
        graphics.fillRect( 0, 0, 50, 50 );
        graphics.dispose();
        return image;
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
        cameraDataProvider.setRotations( scene );
        
        setBackground( scene );
        renderBackgroundAnd2dScene( scene );
        render3dScene( scene );
        renderOverlay( scene );
        
        renderingContext.disableVertexAttributes();
    }
    
    private void setBackground( RenderedScene scene ) {
        renderingContext.setBackgroundColour( scene.getBackgroundColour() );
    }
    
    private void renderBackgroundAnd2dScene( RenderedScene scene ) {
        renderingContext.disableDepth();
        shaderProgram2D.useProgram();
        shaderProgram2D.bindCamera( scene.getCamera2D() );
        scene.renderBackground( renderingContext, currentRenderTime, cameraDataProvider );
        renderGrid2D();
        scene.renderScene2D( renderingContext, currentRenderTime, cameraDataProvider );
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
        scene.renderScene3D( renderingContext, currentRenderTime, cameraDataProvider );
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
        scene.renderOverlay( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    private static class MutableCameraDataProvider implements CameraDataProvider {
        
        private double rotation2D;
        private double rotation3D;
        
        @Override
        public double getCameraRotation2D() {
            return rotation2D;
        }
        
        @Override
        public double getCameraRotation3D() {
            return rotation3D;
        }
    
        void setRotations( RenderedScene scene ) {
            rotation2D = scene.getCamera2D().getGeneralRotation();
            rotation3D = scene.getCamera3D().getGeneralRotation();
        }
    }
}
