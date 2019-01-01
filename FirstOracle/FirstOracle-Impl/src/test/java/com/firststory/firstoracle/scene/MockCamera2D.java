/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.MovableCamera2D;

/**
 * @author n1t4chi
 */
public class MockCamera2D extends MovableCamera2D {
    
    public MockCamera2D(
        float width,
        float XY,
        float rotation
    ) {
        super(
            WindowSettings.builder().setWidth( 100 ).setHeight( 100 ).build(),
            width/2,
            -XY,
            -XY,
            rotation
        );
    }
}
