/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public abstract class MutableObject2D implements Object2D {
    Texture texture;
    UvMap uvMap;
    Vertices2D vertices;
    ObjectTransformations2D objectTransformations2D;
    
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
    public Vertices2D getVertices() {
        return vertices;
    }
    
    public void setVertices( Vertices2D vertices ) {
        this.vertices = vertices;
    }
    
    @Override
    public ObjectTransformations2D getTransformations() {
        return objectTransformations2D;
    }
    
    public void setObjectTransformations2D( ObjectTransformations2D objectTransformations2D ) {
        this.objectTransformations2D = objectTransformations2D;
    }
}
