/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.data.Scale3D;

/**
 * @author n1t4chi
 */
public class Mutable3DTransformations implements PositionableObject3DTransformations {
    
    private Position3D position = FirstOracleConstants.POSITION_ZERO_3F;
    private Scale3D scale = FirstOracleConstants.SCALE_ONE_3F;
    private Rotation3D rotation = FirstOracleConstants.ROTATION_ZERO_3F;
    
    @Override
    public Scale3D getScale() {
        return scale;
    }
    
    public void setScale( Scale3D scale ) {
        this.scale = scale;
    }
    
    @Override
    public Rotation3D getRotation() {
        return rotation;
    }
    
    public void setRotation( Rotation3D rotation ) {
        this.rotation = rotation;
    }
    
    @Override
    public Position3D getPosition() {
        return position;
    }
    
    public void setPosition( Position3D position ) {
        this.position = position;
    }
    
    public void setPosition( float x, float y, float z ) {
        setPosition( Position3D.pos3( x, y, z ) );
    }
    
    public void setPositionX( float x ) {
        setPosition( x, position.y(), position.z() );
    }
    
    public void setPositionY( float y ) {
        setPosition( position.x(), y, position.z() );
    }
    
    public void setPositionZ( float z ) {
        setPosition( position.x(), position.y(), z );
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
