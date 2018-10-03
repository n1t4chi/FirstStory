/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.CubeUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class NonAnimatedCube
    extends AbstractPositionableObject3D< Mutable3DTransformations, CubeVertices >
    implements Cube< Mutable3DTransformations >,
    NonAnimatedObject3D< Mutable3DTransformations, CubeVertices >,
    MutableTextureObject3D< Mutable3DTransformations, CubeVertices >,
    PositionableObject3D< Mutable3DTransformations, CubeVertices >
{
    {
        setTransformations( new Mutable3DTransformations() );
    }
    Texture texture;
    Mutable3DTransformations transformations;
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return CubeUvMap.getCubeUvMap();
    }
    
    @Override
    public Mutable3DTransformations getTransformations() {
        return transformations;
    }
    
    @Override
    public void setTransformations( Mutable3DTransformations transformations ) {
        this.transformations = transformations;
    }
}
