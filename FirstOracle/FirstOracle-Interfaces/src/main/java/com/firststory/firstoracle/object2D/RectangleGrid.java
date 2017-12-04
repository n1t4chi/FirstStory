/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.Terrain2DRenderer;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public interface RectangleGrid
    extends Terrain2D< Plane2DVertices >, Rectangle< Identity2DTransformations, Terrain2DRenderer >
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
    default Vector2fc computePosition( int x, int y, Vector2ic arrayShift ) {
        return position.set(
            FirstOracleConstants.transPlaneDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y, arrayShift.y() )
        );
    }
}
