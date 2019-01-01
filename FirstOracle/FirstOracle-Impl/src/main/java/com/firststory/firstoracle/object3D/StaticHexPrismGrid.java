/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.*;

/**
 * @author n1t4chi
 */
public class StaticHexPrismGrid
    extends
        AbstractTerrain3D< HexPrismVertices, HexPrismPositionCalculator >
    implements
        HexPrismGrid,
        StaticObject3D< Identity3DTransformations, HexPrismVertices >,
        MutableTextureObject3D< Identity3DTransformations, HexPrismVertices >
{
    
    @Override
    public UvMap getUvMap() {
        return HexPrismUvMap.getHexPrismUvMap();
    }
}

