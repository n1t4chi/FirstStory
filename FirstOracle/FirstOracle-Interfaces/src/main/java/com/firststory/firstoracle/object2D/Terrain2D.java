/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.object.Terrain;

/**
 * Class representing 2D terrain, contains texture, UV mapping, vertices and objectTransformations.
 * @author n1t4chi
 */
public interface Terrain2D< Vertices extends Vertices2D >
    extends Object2D< Identity2DTransformations, Vertices >,
    Terrain< Position2D, Scale2D, Rotation2D, Identity2DTransformations, BoundingBox2D, Vertices, Index2D >
{
    
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
     * @return position in space
     */
    Position2D computePosition( int x, int y, Index2D arrayShift );
    
    @Override
    default Position2D computePosition(
        Index2D position,
        Index2D arrayShift
    ) {
        return computePosition( position.x(), position.y(), arrayShift );
    }
}
