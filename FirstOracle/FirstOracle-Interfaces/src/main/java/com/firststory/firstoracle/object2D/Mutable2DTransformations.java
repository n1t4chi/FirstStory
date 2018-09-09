/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public class Mutable2DTransformations implements PositionableObject2DTransformations {
    
    private final Vector2f position;
    private final Vector2f scale;
    private float rotation;
    
    public Mutable2DTransformations() {
        scale = new Vector2f( 1, 1 );
        rotation = 0;
        position = new Vector2f( 0, 0 );
    }
    
    @Override
    public Vector2fc getScale() {
        return scale;
    }
    
    public void setScale( Vector2fc scale ) {
        this.scale.set( scale );
    }
    
    @Override
    public Float getRotation() {
        return rotation;
    }
    
    public void setRotation( float rotation ) {
        this.rotation = rotation;
    }
    
    @Override
    public Vector2fc getPosition() {
        return position;
    }
    
    public void setPosition( Vector2fc position ) {
        this.position.set( position );
    }
    
    public void setPosition( float x, float y ) {
        position.set( x, y );
    }
    
    public void setPositionX( float x ) {
        position.set( x, position.y );
    }
    
    public void setPositionY( float y ) {
        position.set( position.x, y );
    }
    
    public void setScale( float x, float y ) {
        scale.set( x, y );
    }
    
    public void setScaleX( float x ) {
        scale.set( x, scale.y );
    }
    
    public void setScaleY( float y ) {
        scale.set( scale.x, y );
    }
}
