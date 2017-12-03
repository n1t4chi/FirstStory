/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class Mutable3DTransformations implements Object3DTransformations {
    
    private final Vector3f position;
    private final Vector3f scale;
    private final Vector3f rotation;
    
    public Mutable3DTransformations() {
        scale = new Vector3f( 1, 1, 1 );
        rotation = new Vector3f( 0, 0, 0 );
        position = new Vector3f( 0, 0, 0 );
    }
    
    @Override
    public Vector3fc getScale() {
        return scale;
    }
    
    public void setScale( Vector3fc scale ) {
        this.scale.set( scale );
    }
    
    @Override
    public Vector3fc getRotation() {
        return rotation;
    }
    
    public void setRotation( Vector3fc rotation ) {
        this.rotation.set( rotation );
    }
    
    @Override
    public Vector3fc getPosition() {
        return position;
    }
    
    public void setPosition( Vector3fc position ) {
        this.position.set( position );
    }
    
    public void setPosition( float x, float y, float z ) {
        position.set( x, y, z );
    }
    
    public void setPositionX( float x ) {
        position.set( x, position.y, position.z );
    }
    
    public void setPositionY( float y ) {
        position.set( position.x, y, position.z );
    }
    
    public void setPositionZ( float z ) {
        position.set( position.x, position.y, z );
    }
    
    public void setScale( float x, float y, float z ) {
        scale.set( x, y, z );
    }
    
    public void setScaleX( float x ) {
        scale.set( x, scale.y, scale.z );
    }
    
    public void setScaleY( float y ) {
        scale.set( scale.x, y, scale.z );
    }
    
    public void setScaleZ( float z ) {
        scale.set( scale.x, scale.y, z );
    }
    
    public void setRotation( float x, float y, float z ) {
        rotation.set( x, y, z );
    }
    
    public void setRotationX( float x ) {
        rotation.set( x, rotation.y, rotation.z );
    }
    
    public void setRotationY( float y ) {
        rotation.set( rotation.x, y, rotation.z );
    }
    
    public void setRotationZ( float z ) {
        rotation.set( rotation.x, rotation.y, z );
    }
}
