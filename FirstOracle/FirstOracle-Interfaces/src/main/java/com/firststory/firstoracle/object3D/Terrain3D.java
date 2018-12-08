/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.Terrain;

/**
 * Class representing 3D terrain, contains texture, UV mapping, vertices and objectTransformations.
 * @author n1t4chi
 */
public interface Terrain3D< Vertices extends Vertices3D, PositionCalculatorType extends Position3DCalculator >
    extends
        Object3D< Identity3DTransformations, Vertices >,
        Terrain<
            Position3D,
            Scale3D,
            Rotation3D,
            Identity3DTransformations,
            BoundingBox3D,
            Vertices,
            Index3D,
            PositionCalculatorType
        >
{
    
    PositionCalculatorType getPositionCalculator();
    
    @Override
    default Identity3DTransformations getTransformations() {
        return Identity3DTransformations.getIdentity();
    }
    
    default Position3D indexToPosition( int x, int y, int z, Index3D shift ) {
        return getPositionCalculator().indexToPosition( x, y, z, shift );
    }
}
