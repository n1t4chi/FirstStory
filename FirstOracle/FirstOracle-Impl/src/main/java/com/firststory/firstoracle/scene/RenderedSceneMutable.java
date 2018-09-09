/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class RenderedSceneMutable implements RenderedScene {

    private Camera2D camera2D;
    private IsometricCamera3D isometricCamera3D;
    private Vector4fc backgroundColour;
    
    private RenderedObjects2D background;
    private RenderedScene2D scene2D;
    private RenderedScene3D scene3D;
    private RenderedObjects2D overlay;

    private WindowSettings settings;
    
    public RenderedSceneMutable( WindowSettings settings ) {
        this.settings = settings;
        camera2D = new MovableCamera2D( settings, 10, 0, 0, 0 );
        isometricCamera3D = new IsometricCamera3D( settings, 10, 0, 0, 0, 0, 0, 1 );
        backgroundColour = new Vector4f( 1, 1, 0, 1 );
        EmptyRenderedObjects2D emptyObjects = new EmptyRenderedObjects2D();
        background = this.overlay = emptyObjects;
        scene2D = emptyObjects;
        scene3D = new EmptyRenderedObjects3D();
    }

    public RenderedSceneMutable(
        WindowSettings settings,
        Camera2D camera2D,
        IsometricCamera3D isometricCamera3D,
        Vector4fc backgroundColour,
        RenderedObjects2D background,
        RenderedScene2D scene2D,
        RenderedScene3D scene3D,
        RenderedObjects2D overlay
    ) {
        this.settings = settings;
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
    
    public void setCamera3D( IsometricCamera3D camera3D ) {
        this.isometricCamera3D = camera3D;
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
    public RenderedScene2D getScene2D() {
        return scene2D;
    }

    public void setScene2D( RenderedScene2D scene2D ) {
        this.scene2D = scene2D;
    }

    @Override
    public RenderedScene3D getScene3D() {
        return scene3D;
    }

    public void setScene3D( RenderedScene3D scene3D ) {
        this.scene3D = scene3D;
    }

    @Override
    public RenderedObjects2D getOverlay() {
        return overlay;
    }

    public void setOverlay( RenderedObjects2D overlay ) {
        this.overlay = overlay;
    }

    public WindowSettings getSettings() {
        return settings;
    }
    
    public void setSettings( WindowSettings settings ) {
        this.settings = settings;
    }

    public void setIsometricCamera3D( IsometricCamera3D isometricCamera3D ) {
        this.isometricCamera3D = isometricCamera3D;
    }
    
}
