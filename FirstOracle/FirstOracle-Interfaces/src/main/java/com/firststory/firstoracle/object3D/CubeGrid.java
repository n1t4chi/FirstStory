/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.data.Index3D;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public interface CubeGrid extends Terrain3D< CubeVertices >, Cube< Identity3DTransformations > {
    
    Vector3f position = new Vector3f();
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param z          z position in array
     * @param arrayShift shift of array
     * @return same vector with updated positions for current rendering
     */
    @Override
    default Vector3fc computePosition( int x, int y, int z, Index3D arrayShift ) {
        return position.set(
            FirstOracleConstants.transCubeDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transCubeDiscreteToSpace( y, arrayShift.y() ),
            FirstOracleConstants.transCubeDiscreteToSpace( z, arrayShift.z() )
        );
    }
}
