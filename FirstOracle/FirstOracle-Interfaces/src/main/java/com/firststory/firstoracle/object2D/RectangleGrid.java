/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.data.Index2D;
import com.firststory.firstoracle.object.data.Position2D;

/**
 * @author n1t4chi
 */
public interface RectangleGrid
    extends Terrain2D< Plane2DVertices >, Rectangle< Identity2DTransformations >
{
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param arrayShift shift of array
     * @return same vector with updated positions for current rendering
     */
    @Override
    default Position2D computePosition( int x, int y, Index2D arrayShift ) {
        return Position2D.pos2(
            FirstOracleConstants.transPlaneDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y, arrayShift.y() )
        );
    }
}
