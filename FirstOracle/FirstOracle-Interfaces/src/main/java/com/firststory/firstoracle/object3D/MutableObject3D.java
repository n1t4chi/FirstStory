/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public abstract class MutableObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    implements Object3D< Transformations, Vertices >
{
    
    private Texture texture;
    private UvMap uvMap;
    private Vertices vertices;
    private Transformations transformations;
    
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
    public Transformations getTransformations() {
        return transformations;
    }
    
    @Override
    public Vertices getVertices() {
        return vertices;
    }
    
    public void setVertices( Vertices vertices ) {
        this.vertices = vertices;
    }
    
    public void setTransformations( Transformations transformations ) {
        this.transformations = transformations;
    }
}
