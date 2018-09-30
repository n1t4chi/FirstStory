/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.data.Index2D;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public interface RectangleGrid
    extends Terrain2D< Plane2DVertices >, Rectangle< Identity2DTransformations >
{
    
    Vector2f position = new Vector2f();
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param arrayShift shift of array
     * @return same vector with updated positions for current rendering
     */
    @Override
    default Vector2fc computePosition( int x, int y, Index2D arrayShift ) {
        return position.set(
            FirstOracleConstants.transPlaneDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y, arrayShift.y() )
        );
    }
}
