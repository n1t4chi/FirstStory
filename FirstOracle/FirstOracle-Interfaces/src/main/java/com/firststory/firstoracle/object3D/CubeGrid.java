/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.data.Position3D;

/**
 * @author n1t4chi
 */
public interface CubeGrid extends Terrain3D< CubeVertices >, Cube< Identity3DTransformations > {
    
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
    default Position3D computePosition( int x, int y, int z, Index3D arrayShift ) {
        return Position3D.pos3(
            FirstOracleConstants.transCubeDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transCubeDiscreteToSpace( y, arrayShift.y() ),
            FirstOracleConstants.transCubeDiscreteToSpace( z, arrayShift.z() )
        );
    }
}
