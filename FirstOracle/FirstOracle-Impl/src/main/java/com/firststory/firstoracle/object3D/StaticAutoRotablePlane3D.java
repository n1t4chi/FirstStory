/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticAutoRotablePlane3D
    extends
        AutoRotablePlane3D
    implements
        StaticObject3D< MutablePositionable3DTransformations, Plane3DVertices >
{
    private Texture texture;
    private UvMap uvMap;
    
    public StaticAutoRotablePlane3D() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
    @Override
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void setTexture( Texture texture ) {
        this.texture = texture;
        this.uvMap = PlaneUvMap.getPlaneUvMap(
            texture.getDirections(),
            texture.getFrames(),
            texture.getColumns(),
            texture.getRows()
        );
    }
    
    @Override
    public UvMap getUvMap() {
        return uvMap;
    }
}
