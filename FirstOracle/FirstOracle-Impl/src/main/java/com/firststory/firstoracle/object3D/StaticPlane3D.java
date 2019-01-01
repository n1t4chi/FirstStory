/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticPlane3D
    extends
        SimplePlane3D
    implements
        StaticObject3D< MutablePositionable3DTransformations, Plane3DVertices >
{
    private UvMap uvMap;
    
    public StaticPlane3D() {
        setTransformations( new MutablePositionable3DTransformations() );
    }
    
    @Override
    public void setTexture( Texture texture ) {
        super.setTexture( texture );
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
