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
public class CubePositionCalculator implements Position3DCalculator {
    
    
    public static Position3D computeCubePosition( int x, int y, int z, Index3D arrayShift ) {
        return Position3D.pos3(
            FirstOracleConstants.transCubeDiscreteToSpace( x, arrayShift.x() ),
            FirstOracleConstants.transCubeDiscreteToSpace( y, arrayShift.y() ),
            FirstOracleConstants.transCubeDiscreteToSpace( z, arrayShift.z() )
        );
    }
    
    @Override
    public Position3D compute( int x, int y, int z, Index3D shift ) {
        return computeCubePosition(x, y, z, shift );
    }
}
