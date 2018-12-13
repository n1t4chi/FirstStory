/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticHexagon
    extends
        AbstractPositionableObject2D< MutablePositionable2DTransformations, Hex2DVertices >
    implements
        Hexagon2D< MutablePositionable2DTransformations >,
        StaticObject2D< MutablePositionable2DTransformations, Hex2DVertices >,
        MutableTextureObject2D< MutablePositionable2DTransformations, Hex2DVertices >,
        MutableTransformationsObject2D< Hex2DVertices >
{
    private Texture texture;
    
    public StaticHexagon() {
        setTransformations( new MutablePositionable2DTransformations() );
    }
    
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
        return Hex2DUvMap.getHex2DUvMap();
    }
}
