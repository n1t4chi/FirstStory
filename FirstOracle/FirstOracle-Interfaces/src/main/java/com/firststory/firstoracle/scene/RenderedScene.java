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
 * @author: n1t4chi
 */
public interface RenderedScene {

    default void renderBackground( Object2DRenderer renderer ) {
        getBackground().render( renderer );
    }

    default void renderScene2D( Object2DRenderer renderer ) {
        getScene2D().render( renderer );
    }

    default void renderScene3D( Object3DRenderer renderer ) {
        getScene3D().render( renderer );
    }

    default void renderOverlay( Object2DRenderer renderer ) {
        getOverlay().render( renderer );
    }

    Camera3D getCamera3D();

    Camera2D getCamera2D();

    Vector4fc getBackgroundColour();

    RenderedObjects2D getBackground();

    RenderedObjects2D getScene2D();

    RenderedObjects3D getScene3D();

    RenderedObjects2D getOverlay();
}
