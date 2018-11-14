/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.object.Terrain;

/**
 * Class representing 3D terrain, contains texture, UV mapping, vertices and objectTransformations.
 * @author n1t4chi
 */
public interface Terrain3D< Vertices extends Vertices3D > extends
    Object3D< Identity3DTransformations, Vertices >,
    Terrain< Identity3DTransformations, BoundingBox3D, Vertices, Index3D, Position3D >
{
    
    @Override
    default Identity3DTransformations getTransformations() {
        return Identity3DTransformations.getIdentity();
    }
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param z          z position in array
     * @param arrayShift shift of array in space
     * @return position in space
     */
    Position3D computePosition( int x, int y, int z, Index3D arrayShift );
    
    @Override
    default Position3D computePosition( Index3D position, Index3D arrayShift ) {
        return computePosition( position.x(), position.y(), position.z(), arrayShift );
    }
}
