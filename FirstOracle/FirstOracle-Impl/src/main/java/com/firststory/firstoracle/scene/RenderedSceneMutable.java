/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class RenderedSceneMutable implements RenderableScene {
    
    private RenderableBackground background;
    private RenderableScene2D scene2D;
    private RenderableScene3D scene3D;
    private RenderableOverlay overlay;

    private WindowSettings settings;
    
    public RenderedSceneMutable( WindowSettings settings ) {
        this.settings = settings;
        background = EmptyRenderedBackground.provide();
        overlay = EmptyRenderedOverlay.provide();
        scene2D =  EmptyRenderedObjects2D.provide();
        scene3D = EmptyRenderedObjects3D.provide();
    }

    public RenderedSceneMutable(
        WindowSettings settings,
        Camera2D camera2D,
        IsometricCamera3D isometricCamera3D,
        Vector4fc backgroundColour,
        RenderableBackground background,
        RenderableScene2D scene2D,
        RenderableScene3D scene3D,
        RenderableOverlay overlay
    ) {
        this.settings = settings;
        this.background = background;
        this.scene2D = scene2D;
        this.scene3D = scene3D;
        this.overlay = overlay;
    }
    
    @Override
    public void dispose() {
    
    }

    @Override
    public RenderableBackground getBackground() {
        return background;
    }

    public void setBackground( RenderableBackground background ) {
        this.background = background;
    }

    @Override
    public RenderableScene2D getScene2D() {
        return scene2D;
    }

    public void setScene2D( RenderableScene2D scene2D ) {
        this.scene2D = scene2D;
    }

    @Override
    public RenderableScene3D getScene3D() {
        return scene3D;
    }

    public void setScene3D( RenderableScene3D scene3D ) {
        this.scene3D = scene3D;
    }

    @Override
    public RenderableOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay( RenderableOverlay overlay ) {
        this.overlay = overlay;
    }

    public WindowSettings getSettings() {
        return settings;
    }
    
    public void setSettings( WindowSettings settings ) {
        this.settings = settings;
    }
}
