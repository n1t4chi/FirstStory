/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.PlaneUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.rendering.PositionableObject2DRenderer;

/**
 * @author n1t4chi
 */
public class NonAnimatedRectangle implements Rectangle< Mutable2DTransformations, PositionableObject2DRenderer >,
    NonAnimatedObject2D< Mutable2DTransformations, Plane2DVertices, PositionableObject2DRenderer >,
    MutableTextureObject2D< Mutable2DTransformations, Plane2DVertices, PositionableObject2DRenderer >,
    PositionableObject2D< Mutable2DTransformations, Plane2DVertices, PositionableObject2DRenderer >
{
    {
        setTransformations( new Mutable2DTransformations() );
    }
    Texture texture;
    Mutable2DTransformations transformations;
    
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
        return PlaneUvMap.getPlaneUvMap();
    }
    
    @Override
    public Mutable2DTransformations getTransformations() {
        return transformations;
    }
    
    @Override
    public void setTransformations( Mutable2DTransformations transformations ) {
        this.transformations = transformations;
    }
}
