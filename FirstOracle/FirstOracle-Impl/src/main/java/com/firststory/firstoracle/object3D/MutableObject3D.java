/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 * @author n1t4chi
 */
public abstract class MutableObject3D implements Object3D {
    Texture texture;
    UvMap uvMap;
    Vertices3D vertices;
    ObjectTransformations3D objectTransformations3D;
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
    
    public void setUvMap( UvMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    @Override
    public Vertices3D getVertices() {
        return vertices;
    }
    
    public void setVertices( Vertices3D vertices ) {
        this.vertices = vertices;
    }
    
    @Override
    public ObjectTransformations3D getTransformations() {
        return objectTransformations3D;
    }

    public void setObjectTransformations3D( ObjectTransformations3D objectTransformations3D ) {
        this.objectTransformations3D = objectTransformations3D;
    }
}
