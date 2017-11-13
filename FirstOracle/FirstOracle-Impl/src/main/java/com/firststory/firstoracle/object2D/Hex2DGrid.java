/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Texture;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public class Hex2DGrid extends Hexagon2D {

    private static final Vector2f position = new Vector2f();

    public Hex2DGrid( Texture texture ) {
        super( texture, IdentityTransformations2D.getIdentity() );
    }

    /**
     * Retruns position in space based on position in array
     *
     * @param x x position in array
     * @param y y position in array
     * @param arrayShift shift of array
     * @return same vector with updated positions for current rendering
     */
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
