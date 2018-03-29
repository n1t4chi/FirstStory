/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public interface Hex2DGrid
    extends Terrain2D< Hex2DVertices >, Hexagon2D< Identity2DTransformations >
{
    
    Vector2f position = new Vector2f();
    
    @Override
    default Vector2fc computePosition( int x, int y, Vector2ic arrayShift ) {
        return position.set(
            FirstOracleConstants.transHexXDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transHexYDiscreteToSpace( x, y, arrayShift.x(), arrayShift.y() )
        );
    }
}
