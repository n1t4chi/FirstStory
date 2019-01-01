/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticCubeGrid
    extends
        AbstractTerrain3D< CubeVertices, CubePositionCalculator >
    implements
        CubeGrid,
        StaticObject3D< Identity3DTransformations, CubeVertices >,
        MutableTextureObject3D< Identity3DTransformations, CubeVertices >
{
    
    @Override
    public UvMap getUvMap() {
        return CubeUvMap.getCubeUvMap();
    }
}

