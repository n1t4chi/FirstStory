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
 * @author n1t4chi
 */
public class CubeGrid extends Cube {
    private static final Vector3f position = new Vector3f(  );

    public CubeGrid( Texture texture ) {
        super( texture, IdentityTransformations3D.getIdentity() );
    }

    /**
     * Retruns position in space based on position in array
     * @param x x position in array
     * @param y y position in array
     * @param z z position in array
     * @param arrayShift shift of array
     * @return same vector with updated positions for current rendering
     */
    public Vector3fc computePosition(int x, int y, int z, Vector3ic arrayShift){
        return position.set(
            FirstOracleConstants.transCubeDiscreteToSpace( x , arrayShift.x() ),
            FirstOracleConstants.transCubeDiscreteToSpace( y , arrayShift.y() ),
            FirstOracleConstants.transCubeDiscreteToSpace( z , arrayShift.z() )
        );
    }
}
