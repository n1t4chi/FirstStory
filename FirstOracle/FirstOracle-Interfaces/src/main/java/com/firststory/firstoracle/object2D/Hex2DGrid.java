/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;

/**
 * @author n1t4chi
 */
public interface Hex2DGrid
    extends Terrain2D< Hex2DVertices >, Hexagon2D< Identity2DTransformations >
{
    
    @Override
    default Position2D computePosition( int x, int y, Index2D arrayShift ) {
        return HexPositionCalculator.computeHexPosition( x, y, arrayShift );
    }
}
