/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 * @author: n1t4chi
 */
public class MutableObject3D implements Object3D {
    Texture texture;
    UVMap uvMap;
    Vertices vertices;
    ObjectTransformations objectTransformations;
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public UVMap getUvMap() {
        return uvMap;
    }
    
    public void setUvMap( UVMap uvMap ) {
        this.uvMap = uvMap;
    }
    
    @Override
    public Vertices getVertices() {
        return vertices;
    }
    
    public void setVertices( Vertices vertices ) {
        this.vertices = vertices;
    }
    
    @Override
    public ObjectTransformations getTransformations() {
        return objectTransformations;
    }
    
    public void setObjectTransformations( ObjectTransformations objectTransformations ) {
        this.objectTransformations = objectTransformations;
    }
}
