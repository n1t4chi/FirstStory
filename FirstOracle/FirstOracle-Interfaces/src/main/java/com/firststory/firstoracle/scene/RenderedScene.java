/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.rendering.Object2DRenderer;
import com.firststory.firstoracle.rendering.Object3DRenderer;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface RenderedScene {
    
    default void renderScene2D( Object2DRenderer renderer ) {
        getScene2D().render( renderer );
    }
    
    RenderedObjects2D getScene2D();
    
    default void renderScene3D( Object3DRenderer renderer ) {
        getScene3D().render( renderer );
    }
    
    RenderedObjects3D getScene3D();
    
    default void renderBackground( Object2DRenderer renderer ) {
        getBackground().render( renderer );
    }
    
    RenderedObjects2D getBackground();
    
    default void renderOverlay( Object2DRenderer renderer ) {
        getOverlay().render( renderer );
    }
    
    RenderedObjects2D getOverlay();
    
    Camera3D getCamera3D();
    
    Camera2D getCamera2D();
    
    Vector4fc getBackgroundColour();
}
