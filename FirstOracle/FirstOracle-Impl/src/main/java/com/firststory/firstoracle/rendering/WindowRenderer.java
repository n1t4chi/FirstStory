/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.ReflectionUtils;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.scene.RenderableScene;
import com.firststory.firstoracle.scene.SceneProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.firststory.firstoracle.rendering.DummyGrid2DRenderer.DUMMY_GRID_2D_RENDERER;
import static com.firststory.firstoracle.rendering.DummyGrid3DRenderer.DUMMY_GRID_3D_RENDERER;

/**
 * @author n1t4chi
 */
public class WindowRenderer implements Renderer {
    
    private static final Logger logger = FirstOracleConstants.getLogger( WindowRenderer.class );
    
    public static WindowRenderer provide( SceneProvider sceneProvider ) {
        var grid2DRenderer = createGridRenderer(
            PropertiesUtil.getProperty( PropertiesUtil.WINDOW_RENDERER_GRID_2D_RENDERER_CLASS_NAME_PROPERTY ),
            Grid2DRenderer.class,
            DUMMY_GRID_2D_RENDERER
        );
        var grid3DRenderer = createGridRenderer(
            PropertiesUtil.getProperty( PropertiesUtil.WINDOW_RENDERER_GRID_3D_RENDERER_CLASS_NAME_PROPERTY ),
            Grid3DRenderer.class,
            DUMMY_GRID_3D_RENDERER
        );
        return new WindowRenderer(
            grid2DRenderer,
            grid3DRenderer, sceneProvider
        );
    }
    
    private static < T extends Renderer > T createGridRenderer(
        String rendererClassName,
        Class< T > gridTypeClass,
        T defaultInstance
    ) {
        if ( rendererClassName != null ) {
            try {
                return ReflectionUtils.createInstanceViaMethod( ReflectionUtils.extractClassForName( rendererClassName, gridTypeClass ) );
            } catch ( Exception ex ) {
                logger.log( Level.WARNING, "Cannot create grid renderer for class name: " + rendererClassName, ex );
            }
        }
        return defaultInstance;
    }
    
    private final Grid2DRenderer grid2DRenderer;
    private final Grid3DRenderer grid3DRenderer;
    private final SceneProvider sceneProvider;
    private final MutableCameraDataProvider cameraDataProvider = new MutableCameraDataProvider();
    private RenderingContext renderingContext;
    private double currentRenderTime;
    
    public WindowRenderer(
        Grid2DRenderer grid2DRenderer,
        Grid3DRenderer grid3DRenderer,
        SceneProvider sceneProvider
    ) {
        this.grid2DRenderer = grid2DRenderer;
        this.grid3DRenderer = grid3DRenderer;
        this.sceneProvider = sceneProvider;
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
    
    private void setBackgroundColour( RenderableScene scene ) {
        renderingContext.setBackgroundColour( scene.getBackground().getBackgroundColour() );
    }
    
    private void renderBackgroundAnd2dScene( RenderableScene scene ) {
        renderingContext.useRendering2D( scene.getBackground().getBackgroundCamera(), false );
        scene.renderBackground( renderingContext, currentRenderTime, cameraDataProvider );
        renderingContext.useRendering2D( scene.getScene2D().getScene2DCamera(), false );
        renderGrid2D();
        scene.renderScene2D( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    private void renderGrid2D() {
        grid2DRenderer.render( renderingContext, currentRenderTime );
    }
    
    private void render3dScene( RenderableScene scene ) {
        renderingContext.useRendering3D( scene.getScene3D().getScene3DCamera(), true );
        renderGrid3D();
        scene.renderScene3D( renderingContext, currentRenderTime, cameraDataProvider );
    }
    
    private void renderGrid3D(  ) {
        grid3DRenderer.render( renderingContext, currentRenderTime );
    }
    
    private void renderOverlay( RenderableScene scene ) {
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
    
        void setRotations( RenderableScene scene ) {
            rotation2D = scene.getScene2D().getScene2DCamera().getGeneralRotation();
            rotation3D = scene.getScene3D().getScene3DCamera().getGeneralRotation();
        }
    }
}
