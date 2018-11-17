/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.PlaneUvMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public class NonAnimatedPlane3D
    extends
        AbstractPositionableObject3D< MutablePositionable3DTransformations, Plane3DVertices >
    implements
        Plane3D< MutablePositionable3DTransformations >,
        NonAnimatedObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        MutableTextureObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        PositionableObject3D< MutablePositionable3DTransformations, Plane3DVertices >,
        MutableTransformationsObject3D< Plane3DVertices >
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
