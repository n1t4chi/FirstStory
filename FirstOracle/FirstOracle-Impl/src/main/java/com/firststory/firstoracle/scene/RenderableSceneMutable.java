/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.WindowSettings;

/**
 * @author n1t4chi
 */
public class RenderableSceneMutable implements RenderableScene {
    
    private RenderableBackground background;
    private RenderableScene2D scene2D;
    private RenderableScene3D scene3D;
    private RenderableOverlay overlay;

    private WindowSettings settings;
    
    public RenderableSceneMutable( WindowSettings settings ) {
        this(
            settings,
            EmptyRenderableBackground.provide(),
            EmptyRenderableScene2D.provide(),
            EmptyRenderableScene3D.provide(),
            EmptyRenderableOverlay.provide()
        );
    }

    public RenderableSceneMutable(
        WindowSettings settings,
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
    public void dispose() {}

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
