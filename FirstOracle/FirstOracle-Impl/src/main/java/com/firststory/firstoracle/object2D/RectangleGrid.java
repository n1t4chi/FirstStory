/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public class RectangleGrid extends Rectangle< Identity2DTransformations > implements Terrain2D< Plane2DVertices > {
    private static final Vector2f position = new Vector2f();
    
    {
        setTransformations( Identity2DTransformations.getIdentity() );
    }
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param arrayShift shift of array
     *
     * @return same vector with updated positions for current rendering
     */
    @Override
    public Vector2fc computePosition( int x, int y, Vector2ic arrayShift ) {
        return position.set(
            FirstOracleConstants.transPlaneDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y, arrayShift.y() )
        );
    }
}
