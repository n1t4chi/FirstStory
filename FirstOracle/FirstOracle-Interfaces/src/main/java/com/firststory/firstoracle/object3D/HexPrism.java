/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.HexPrismUvMap;
import com.firststory.firstoracle.object.UvMap;

/**
 * @author n1t4chi
 */
public interface HexPrism< Transformations extends Object3DTransformations >
    extends Object3D< Transformations, HexPrismVertices >
{
    
    HexPrismVertices VERTICES = HexPrismVertices.getHexPrismVertices();
    HexPrismUvMap UV_MAP = HexPrismUvMap.getHexPrismUvMap();
    
    @Override
    default HexPrismVertices getVertices() {
        return VERTICES;
    }
    
    @Override
    default UvMap getUvMap() {
        return UV_MAP;
    }
    
    @Override
    default int getCurrentVertexFrame( double currentTimeSnapshot ) {
        return 0;
    }
}
