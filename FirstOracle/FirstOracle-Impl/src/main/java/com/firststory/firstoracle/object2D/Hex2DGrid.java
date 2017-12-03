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
public class Hex2DGrid extends Hexagon2D< Identity2DTransformations > implements Terrain2D< Hex2DVertices > {
    
    private static final Vector2f position = new Vector2f();
    
    {
        setTransformations( Identity2DTransformations.getIdentity() );
    }
    
    @Override
    public Vector2fc computePosition( int x, int y, Vector2ic arrayShift ) {
        return position.set(
            FirstOracleConstants.transHexXDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transHexYDiscreteToSpace( x,
                y,
                arrayShift.x(),
                arrayShift.y()
            )
        );
    }
}
