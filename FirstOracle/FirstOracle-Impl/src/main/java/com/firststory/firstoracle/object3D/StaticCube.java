/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticCube
    extends
        AbstractPositionableObject3D< MutablePositionable3DTransformations, CubeVertices >
    implements Cube< MutablePositionable3DTransformations >,
        StaticObject3D< MutablePositionable3DTransformations, CubeVertices >,
        MutableTextureObject3D< MutablePositionable3DTransformations, CubeVertices >,
        PositionableObject3D< MutablePositionable3DTransformations, CubeVertices >,
        MutableTransformationsObject3D< CubeVertices >
{
    private Texture texture;
    
    public StaticCube() {
        setTransformations( new MutablePositionable3DTransformations() );
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
        return CubeUvMap.getCubeUvMap();
    }
}
