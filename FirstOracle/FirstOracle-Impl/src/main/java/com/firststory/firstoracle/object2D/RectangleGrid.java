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
 * @author: n1t4chi
 */
public class RectangleGrid extends Rectangle {
    private static Vector2f position = new Vector2f(  );

    public RectangleGrid( Texture texture ) {
        super( texture, IdentityTransformations2D.getIdentity() );
    }

    /**
     * Retruns position in space based on position in array
     * @param x
     * @param y
     * @param arrayShift shift of array
     * @return
     */
    public Vector2fc computePosition(int x, int y, Vector2ic arrayShift){
        return position.set(
            FirstOracleConstants.transPlaneDiscreteToSpace( x , arrayShift.x() ),
            FirstOracleConstants.transPlaneDiscreteToSpace( y , arrayShift.y() )
        );
    }
}
