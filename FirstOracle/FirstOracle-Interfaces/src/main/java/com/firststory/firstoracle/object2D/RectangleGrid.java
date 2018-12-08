/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public interface RectangleGrid
    extends Terrain2D< Plane2DVertices, RectanglePositionCalculator >, Rectangle< Identity2DTransformations >
{
    
    @Override
    default RectanglePositionCalculator getPositionCalculator() {
        return RectanglePositionCalculator.instance;
    }
}
