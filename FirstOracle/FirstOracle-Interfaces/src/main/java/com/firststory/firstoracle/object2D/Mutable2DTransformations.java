/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object.MutableObjectTransformations;

/**
 * @author n1t4chi
 */
public class Mutable2DTransformations implements
    Object2DTransformations,
    MutableObjectTransformations< Scale2D, Rotation2D >
{
    private Scale2D scale = FirstOracleConstants.SCALE_ONE_2F;
    private Rotation2D rotation = FirstOracleConstants.ROTATION_ZERO_2F;
    
    @Override
    public Scale2D getScale() {
        return scale;
    }
    
    public void setScale( Scale2D scale ) {
        this.scale = scale;
    }
    
    @Override
    public Rotation2D getRotation() {
        return rotation;
    }
    
    public void setRotation( Rotation2D rotation ) {
        this.rotation = rotation;
    }
    
    public void setRotation( Float angle ) {
        setRotation( Rotation2D.rot2( angle ) );
    }
    
    public void setScale( float x, float y ) {
        setScale( Scale2D.scale2( x, y ) );
    }
    
    public void setScaleX( float x ) {
        setScale( x, scale.y() );
    }
    
    public void setScaleY( float y ) {
        setScale( scale.x(), y );
    }
}
