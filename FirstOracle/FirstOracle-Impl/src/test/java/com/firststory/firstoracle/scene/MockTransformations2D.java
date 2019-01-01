/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object2D.PositionableObject2DTransformations;

/**
 * @author n1t4chi
 */
class MockTransformations2D implements PositionableObject2DTransformations {
    
    private final Position2D position;
    private final Scale2D scale;
    private final Rotation2D rotation;
    
    MockTransformations2D( float x, float y, float scaleX, float scaleY ) {
        position = Position2D.pos2( x, y );
        scale = Scale2D.scale2( scaleX, scaleY );
        rotation = Rotation2D.rot2( 0 );
    }
    
    @Override
    public Position2D getPosition() {
        return position;
    }
    
    @Override
    public Scale2D getScale() {
        return scale;
    }
    
    @Override
    public Rotation2D getRotation() {
        return rotation;
    }
}
