/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public interface Terrain2D< Vertices extends Vertices2D > extends Object2D< Identity2DTransformations, Vertices > {
    
    @Override
    default Identity2DTransformations getTransformations() {
        return Identity2DTransformations.getIdentity();
    }
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param arrayShift shift of array in space
     */
    Vector2fc computePosition( int x, int y, Vector2ic arrayShift );
}
