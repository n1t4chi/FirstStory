/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class NonAnimatedHexGrid
    extends
        AbstractTerrain2D< Hex2DVertices, HexPositionCalculator >
    implements
        Hex2DGrid,
        NonAnimatedObject2D< Identity2DTransformations, Hex2DVertices >,
        MutableTextureObject2D< Identity2DTransformations, Hex2DVertices >
{
    private Texture texture;
    
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
