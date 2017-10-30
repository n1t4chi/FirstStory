/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.camera.IsometricCamera;

/**
 * @author: n1t4chi
 */
public class RenderedScene {
    IsometricCamera camera;

    public IsometricCamera getCamera() {
        return camera;
    }

    public void setCamera( IsometricCamera camera ) {
        this.camera = camera;
    }
}
