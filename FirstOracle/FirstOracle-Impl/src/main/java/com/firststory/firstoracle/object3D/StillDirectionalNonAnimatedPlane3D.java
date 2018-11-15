/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.rendering.RenderData;

import java.util.List;

/**
 * @author n1t4chi
 */
public class StillDirectionalNonAnimatedPlane3D extends NonAnimatedPlane3D {
    
    @Override
    public List< RenderData > getRenderData(
        double timeSnapshot,
        double cameraRotation
    ) {
        getTransformations().setRotation( 0, 45 + ( float ) cameraRotation,0 );
        return super.getRenderData( timeSnapshot, cameraRotation );
    }
}
