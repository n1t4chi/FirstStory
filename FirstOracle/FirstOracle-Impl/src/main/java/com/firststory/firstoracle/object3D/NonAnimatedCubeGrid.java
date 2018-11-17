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
public class NonAnimatedCubeGrid
    extends
        AbstractTerrain3D< CubeVertices >
    implements
        CubeGrid,
        NonAnimatedObject3D< Identity3DTransformations, CubeVertices >,
        MutableTextureObject3D< Identity3DTransformations, CubeVertices >
{
    
    Texture texture;
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public UvMap getUvMap() {
        return CubeUvMap.getCubeUvMap();
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
    }
}

