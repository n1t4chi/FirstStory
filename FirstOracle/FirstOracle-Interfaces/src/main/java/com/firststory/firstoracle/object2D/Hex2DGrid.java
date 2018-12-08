/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public interface Hex2DGrid extends Terrain2D< Hex2DVertices, HexPositionCalculator >, Hexagon2D< Identity2DTransformations >
{
    
    @Override
    default HexPositionCalculator getPositionCalculator() {
        return HexPositionCalculator.instance;
    }
}
