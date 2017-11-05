/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * @author: n1t4chi
 */
public class RenderedSceneMutable implements RenderedScene {

    private Camera2D camera2D;
    private IsometricCamera3D isometricCamera3D;
    private Vector4fc backgroundColour;

    private RenderedObjects2D background;
    private RenderedObjects2D scene2D;
    private RenderedObjects3D scene3D;
    private RenderedObjects2D overlay;

    public RenderedSceneMutable() {
        camera2D = new IdentityCamera2D();
        isometricCamera3D = new IsometricCamera3D( 10, 0, 0, 0, 1, 0, 0, 1 );
        backgroundColour = new Vector4f( 1, 1, 1, 1 );
        background = scene2D = overlay = new RenderedObjects2D() {};
        scene3D = new RenderedObjects3D() {};
    }

    public RenderedSceneMutable(
        Camera2D camera2D,
        IsometricCamera3D isometricCamera3D,
        Vector4fc backgroundColour,
        RenderedObjects2D background,
        RenderedObjects2D scene2D,
        RenderedObjects3D scene3D,
        RenderedObjects2D overlay
    )
    {
        this.camera2D = camera2D;
        this.isometricCamera3D = isometricCamera3D;
        this.backgroundColour = backgroundColour;
        this.background = background;
        this.scene2D = scene2D;
        this.scene3D = scene3D;
        this.overlay = overlay;
    }

    @Override
    public IsometricCamera3D getCamera3D() {
        return isometricCamera3D;
    }

    public void setIsometricCamera3D( IsometricCamera3D isometricCamera3D ) {
        this.isometricCamera3D = isometricCamera3D;
    }

    @Override
    public Camera2D getCamera2D() {
        return camera2D;
    }

    public void setCamera2D( Camera2D camera2D ) {
        this.camera2D = camera2D;
    }

    @Override
    public Vector4fc getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour( Vector4fc backgroundColour ) {
        this.backgroundColour = backgroundColour;
    }

    @Override
    public RenderedObjects2D getBackground() {
        return background;
    }

    public void setBackground( RenderedObjects2D background ) {
        this.background = background;
    }

    @Override
    public RenderedObjects2D getScene2D() {
        return scene2D;
    }

    public void setScene2D( RenderedObjects2D scene2D ) {
        this.scene2D = scene2D;
    }

    @Override
    public RenderedObjects3D getScene3D() {
        return scene3D;
    }

    public void setScene3D( RenderedObjects3D scene3D ) {
        this.scene3D = scene3D;
    }

    @Override
    public RenderedObjects2D getOverlay() {
        return overlay;
    }

    public void setOverlay( RenderedObjects2D overlay ) {
        this.overlay = overlay;
    }
}
