/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public interface HexPrismGrid
    extends Terrain3D< HexPrismVertices, HexPrismPositionCalculator >, HexPrism< Identity3DTransformations >
{
    
    @Override
    default HexPrismPositionCalculator getPositionCalculator() {
        return HexPrismPositionCalculator.instance;
    }
}
