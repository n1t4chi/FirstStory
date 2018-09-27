/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.scene.RenderedScene;
import org.joml.Vector4f;

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
    private final MutableCameraDataProvider cameraDataProvider = new MutableCameraDataProvider();
    private RenderingContext renderingContext;
    private double currentRenderTime;
    
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
    
    @Override
    public void init() {
        grid3DRenderer.init();
        grid2DRenderer.init();
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
    
        var scene = sceneProvider.getNextScene();
        cameraDataProvider.setRotations( scene );
        
        setBackgroundColour( scene );
        renderBackgroundAnd2dScene( scene );
        render3dScene( scene );
        renderOverlay( scene );
    }
    
    private void setBackgroundColour( RenderedScene scene ) {
        renderingContext.setBackgroundColour( scene.getBackgroundColour() );
    }
    
    private void renderBackgroundAnd2dScene( RenderedScene scene ) {
        renderingContext.useRendering2D( scene.getCamera2D(), false );
        scene.renderBackground( renderingContext, currentRenderTime, cameraDataProvider );
        renderGrid2D();
        scene.renderScene2D( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    private void renderGrid2D() {
        grid2DRenderer.render( renderingContext, currentRenderTime );
    }
    
    private void render3dScene( RenderedScene scene ) {
        renderingContext.useRendering3D( scene.getCamera3D(), true );
        renderGrid3D();
        scene.renderScene3D( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    private void renderGrid3D(  ) {
        grid3DRenderer.render( renderingContext, currentRenderTime );
    }
    
    private void renderOverlay( RenderedScene scene ) {
        renderingContext.useRendering2D( IdentityCamera2D.getCamera(), false );
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
