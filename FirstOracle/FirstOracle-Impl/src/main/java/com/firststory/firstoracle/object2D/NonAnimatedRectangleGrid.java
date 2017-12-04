/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.PlaneUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;
import com.firststory.firstoracle.rendering.Terrain2DRenderer;

/**
 * @author n1t4chi
 */
public class NonAnimatedRectangleGrid implements RectangleGrid,
    NonAnimatedObject2D< Identity2DTransformations, Plane2DVertices, Terrain2DRenderer >,
    MutableTextureObject2D< Identity2DTransformations, Plane2DVertices, Terrain2DRenderer >
{
    
    Texture texture;
    
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
}
