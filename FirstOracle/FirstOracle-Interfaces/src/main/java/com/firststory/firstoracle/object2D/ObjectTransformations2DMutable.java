/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object3D.ObjectTransformations3D;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector2fc;

/**
 * @author: n1t4chi
 */
public class ObjectTransformations2DMutable implements ObjectTransformations2D {
    
    private final Vector2f position;
    private final Vector2f scale;
    private final Vector2f rotation;
    
    public ObjectTransformations2DMutable() {
        scale = new Vector2f( 1, 1 );
        rotation = new Vector2f( 0, 0 );
        position = new Vector2f( 0, 0 );
    }
    
    @Override
    public Vector2fc getScale() { return scale; }
    
    @Override
    public Vector2fc getRotation() { return rotation; }
    
    @Override
    public Vector2fc getPosition() { return position; }
    
    public void setPosition( Vector2fc position ) { this.position.set( position ); }
    
    public void setRotation( Vector2fc rotation ) { this.rotation.set( rotation ); }
    
    public void setScale( Vector2fc scale ) { this.scale.set( scale ); }
    
    public void setPosition( float x, float y ) { position.set( x, y ); }
    
    public void setPositionX( float x ) { position.set( x, position.y ); }
    
    public void setPositionY( float y ) { position.set( position.x, y ); }
    
    public void setScale( float x, float y ) { scale.set( x, y ); }
    
    public void setScaleX( float x ) { scale.set( x, scale.y ); }
    
    public void setScaleY( float y ) { scale.set( scale.x, y ); }
    
    public void setRotation( float x, float y ) { rotation.set( x, y ); }
    
    public void setRotationX( float x ) { rotation.set( x, rotation.y ); }
    
    public void setRotationY( float y ) { rotation.set( rotation.x, y ); }
}
