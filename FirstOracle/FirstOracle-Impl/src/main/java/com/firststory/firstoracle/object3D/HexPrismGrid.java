/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Texture;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/**
 * @author: n1t4chi
 */
public class HexPrismGrid extends HexPrism {

    private static Vector3f position = new Vector3f();

    public HexPrismGrid( Texture texture ) {
        super( texture, IdentityTransformations3D.getIdentity() );
    }

    /**
     * Retruns position in space based on position in array
     *
     * @param x
     * @param y
     * @param z
     * @param arrayShift shift of array
     * @return
     */
    public Vector3fc computePosition( int x, int y, int z, Vector3ic arrayShift ) {
        return position.set(
            FirstOracleConstants.transHexPrismXDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transHexPrismYDiscreteToSpace( y, arrayShift.y() ),
            FirstOracleConstants.transHexPrismZDiscreteToSpace( x,
                z,
                arrayShift.x(),
                arrayShift.z()
            )
        );
    }
}
