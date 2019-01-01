/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object3D.PositionableObject3DTransformations;

/**
 * @author n1t4chi
 */
class MockTransformations3D implements PositionableObject3DTransformations {
    
    private final Position3D position;
    private final Scale3D scale;
    private final Rotation3D rotation;
    
    MockTransformations3D( float x, float y, float z, float scaleX, float scaleY, float scaleZ ) {
        position = Position3D.pos3( x, y, z );
        scale = Scale3D.scale3( scaleX, scaleY, scaleZ );
        rotation = Rotation3D.rot3( 0, 0, 0 );
    }
    
    @Override
    public Position3D getPosition() {
        return position;
    }
    
    @Override
    public Scale3D getScale() {
        return scale;
    }
    
    @Override
    public Rotation3D getRotation() {
        return rotation;
    }
}
