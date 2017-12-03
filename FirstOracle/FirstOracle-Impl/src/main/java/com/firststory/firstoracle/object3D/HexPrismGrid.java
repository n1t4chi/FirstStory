/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/**
 * @author n1t4chi
 */
public class HexPrismGrid extends HexPrism< Identity3DTransformations > implements Terrain3D< HexPrismVertices > {
    
    private static final Vector3f position = new Vector3f();
    
    {
        setTransformations( Identity3DTransformations.getIdentity() );
    }
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param z          z position in array
     * @param arrayShift shift of array
     *
     * @return same vector with updated positions for current rendering
     */
    @Override
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
