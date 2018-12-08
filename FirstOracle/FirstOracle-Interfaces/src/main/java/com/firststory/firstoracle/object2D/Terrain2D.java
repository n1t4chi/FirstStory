/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.Terrain;

/**
 * Class representing 2D terrain, contains texture, UV mapping, vertices and objectTransformations.
 * @author n1t4chi
 */
public interface Terrain2D< Vertices extends Vertices2D, PositionCalculatorType extends Position2DCalculator >
    extends
        Object2D< Identity2DTransformations, Vertices >,
        Terrain<
            Position2D,
            Scale2D,
            Rotation2D,
            Identity2DTransformations,
            BoundingBox2D,
            Vertices,
            Index2D,
            PositionCalculatorType
        >
{
    
    @Override
    default Identity2DTransformations getTransformations() {
        return Identity2DTransformations.getIdentity();
    }
    
    default Position2D indexToPosition( int x, int y, Index2D shift ) {
        return getPositionCalculator().indexToPosition( x, y, shift );
    }
}
