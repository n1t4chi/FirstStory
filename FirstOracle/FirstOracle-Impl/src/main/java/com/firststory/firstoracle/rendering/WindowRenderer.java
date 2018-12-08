/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.scene.*;

import java.util.*;
import java.util.logging.*;

import static com.firststory.firstoracle.rendering.DummyGrid2D.DUMMY_GRID_2D;
import static com.firststory.firstoracle.rendering.DummyGrid3D.DUMMY_GRID_3D;

/**
 * @author n1t4chi
 */
public class WindowRenderer implements Renderer {
    
    private static final Logger logger = FirstOracleConstants.getLogger( WindowRenderer.class );
    
    public static WindowRenderer provide( SceneProvider sceneProvider ) {
        var grid2DRenderer = createGridRenderer(
            PropertiesUtil.getProperty( PropertiesUtil.WINDOW_RENDERER_GRID_2D_RENDERER_CLASS_NAME_PROPERTY ),
            Grid2D.class, DUMMY_GRID_2D
        );
        var grid3DRenderer = createGridRenderer(
            PropertiesUtil.getProperty( PropertiesUtil.WINDOW_RENDERER_GRID_3D_RENDERER_CLASS_NAME_PROPERTY ),
            Grid3D.class, DUMMY_GRID_3D
        );
        return new WindowRenderer(
            grid2DRenderer,
            grid3DRenderer,
            sceneProvider
        );
    }
    
    private static < T extends Grid > T createGridRenderer(
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
    
    private final Grid2D grid2D;
    private final Grid3D grid3D;
    private final SceneProvider sceneProvider;
    private final MutableCameraDataProvider cameraDataProvider = new MutableCameraDataProvider();
    
    public WindowRenderer(
        Grid2D grid2D,
        Grid3D grid3D,
        SceneProvider sceneProvider
    ) {
        this.grid2D = grid2D;
        this.grid3D = grid3D;
        this.sceneProvider = sceneProvider;
    }
    
    @Override
    public void init() {
    }
    
    @Override
    public void dispose() {
        grid3D.dispose();
        grid2D.dispose();
    }
    
    @Override
    public void render( RenderingContext renderingContext, double currentRenderTime ) {
    
        var scene = sceneProvider.getNextScene();
        cameraDataProvider.setRotations( scene );
        var cameraRotation2D = cameraDataProvider.getCameraRotation2D();
        var cameraRotation3D = cameraDataProvider.getCameraRotation3D();
    
        renderingContext.renderBackground(
            scene.getBackground().getBackgroundCamera(),
            scene.getBackground().getBackgroundColour(),
            scene.getBackground().getBackgroundRenderData( currentRenderTime, 0 )
        );
        renderingContext.renderScene2D(
            scene.getScene2D().getScene2DCamera(),
            mergeLists(
                grid2D.toRenderDataList(),
                scene.getScene2D().getObjects2DRenderData( currentRenderTime, cameraRotation2D )
            )
        );
        renderingContext.renderScene3D(
            scene.getScene3D().getScene3DCamera(),
            mergeLists(
                grid3D.toRenderDataList(),
                scene.getScene3D().getObjects3DRenderData( currentRenderTime, cameraRotation3D )
            )
        );
        renderingContext.renderOverlay(
            scene.getOverlay().getOverlayCamera(),
            scene.getOverlay().getOverlayRenderData( currentRenderTime, 0 )
        );
    }
    
    private List< RenderData > mergeLists( List< RenderData > list1, List< RenderData > list2 ) {
        if( list1.isEmpty() ) {
            return list2;
        }
        if( list2.isEmpty() ) {
            return list1;
        }
        var list = new ArrayList<>( list1 );
        list.addAll( list2 );
        return list;
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
