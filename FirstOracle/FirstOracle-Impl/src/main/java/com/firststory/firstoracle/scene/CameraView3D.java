/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.object.BoundingBox;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class CameraView3D extends CameraView {
    
    public CameraView3D( Camera camera ) {
        super( camera );
    }
    
    @Override
    float dim1( Vector3fc vector ) {
        return vector.x();
    }
    
    @Override
    float dim2( Vector3fc vector ) {
        return vector.z();
    }
    
    @Override
    float minDim1Box( BoundingBox< ?, ?, ? > box ) {
        return box.getMinX();
    }
    
    @Override
    float maxDim1Box( BoundingBox< ?, ?, ? > box ) {
        return box.getMaxX();
    }
    
    @Override
    float minDim2Box( BoundingBox< ?, ?, ? > box ) {
        return box.getMinZ();
    }
    
    @Override
    float maxDim2Box( BoundingBox< ?, ?, ? > box ) {
        return box.getMaxZ();
    }
    
    @Override
    float minDimOtherBox( BoundingBox< ?, ?, ? > box ) {
        return box.getMinY();
    }
    
    @Override
    float maxDimOtherBox( BoundingBox< ?, ?, ? > box ) {
        return box.getMaxY();
    }
    
    @Override
    Vector3fc pointAtDimOther( Line3D line, float dimOther ) {
        return line.getPointAtY( dimOther );
    }
}
