/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.DirectionController;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.List;

/**
 * @author n1t4chi
 */
public class DirectionalNonAnimatedPlane3D
    extends
        NonAnimatedPlane3D
    implements
        DirectableObject3D< MutablePositionable3DTransformations, Plane3DVertices >
{
    private DirectionController directionController = direction -> 0;
    
    @Override
    public DirectionController getDirectionController() {
        return directionController;
    }
    
    @Override
    public void setDirectionController( DirectionController directionController ) {
        this.directionController = directionController;
    }
    
    @Override
    public List< RenderData > getRenderData( double timeSnapshot, double cameraRotation ) {
        getTransformations().setRotation( 0, 45 + ( float ) cameraRotation,0 );
        return super.getRenderData( timeSnapshot, cameraRotation );
    }
}
