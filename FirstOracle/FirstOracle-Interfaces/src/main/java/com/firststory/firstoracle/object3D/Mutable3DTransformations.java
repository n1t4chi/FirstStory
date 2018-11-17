/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.object.MutableObjectTransformations;

/**
 * @author n1t4chi
 */
public class Mutable3DTransformations implements
    Object3DTransformations,
    MutableObjectTransformations< Scale3D, Rotation3D >
{
    private Scale3D scale = FirstOracleConstants.SCALE_ONE_3F;
    private Rotation3D rotation = FirstOracleConstants.ROTATION_ZERO_3F;
    
    @Override
    public Scale3D getScale() {
        return scale;
    }
    
    @Override
    public void setScale( Scale3D scale ) {
        this.scale = scale;
    }
    
    @Override
    public Rotation3D getRotation() {
        return rotation;
    }
    
    @Override
    public void setRotation( Rotation3D rotation ) {
        this.rotation = rotation;
    }
    
    public void setScale( float x, float y, float z ) {
        setScale( Scale3D.scale3( x, y, z ) );
    }
    
    public void setScaleX( float x ) {
        setScale( x, scale.y(), scale.z() );
    }
    
    public void setScaleY( float y ) {
        setScale( scale.x(), y, scale.z() );
    }
    
    public void setScaleZ( float z ) {
        setScale( scale.x(), scale.y(), z );
    }
    
    public void setRotation( float x, float y, float z ) {
        setRotation( Rotation3D.rot3( x, y, z ) );
    }
    
    public void setRotationX( float x ) {
        setRotation( x, rotation.oy(), rotation.oz() );
    }
    
    public void setRotationY( float y ) {
        setRotation( rotation.ox(), y, rotation.oz() );
    }
    
    public void setRotationZ( float z ) {
        setRotation( rotation.ox(), rotation.oy(), z );
    }
}
