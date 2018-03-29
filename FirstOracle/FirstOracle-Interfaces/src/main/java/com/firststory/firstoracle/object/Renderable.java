/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;

public interface Renderable {
    void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    );
}
