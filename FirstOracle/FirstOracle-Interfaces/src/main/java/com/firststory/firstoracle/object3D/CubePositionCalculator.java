/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index3D;

/**
 * @author n1t4chi
 */
public class CubePositionCalculator implements Position3DCalculator {
    
    public static final CubePositionCalculator instance = new CubePositionCalculator();
    
    @Override
    public float xIndexToPosition( int x, Index3D shift ) {
        return FirstOracleConstants.transCubeDiscreteToSpace( x, shift.x() );
    }
    
    @Override
    public float yIndexToPosition( int y, Index3D shift ) {
        return FirstOracleConstants.transCubeDiscreteToSpace( y, shift.y() );
    }
    
    @Override
    public float zIndexToPosition( int z, Index3D shift ) {
        return FirstOracleConstants.transCubeDiscreteToSpace( z, shift.z() );
    }
    
    @Override
    public int xPositionToIndex( float x, Index3D shift ) {
        return ( int ) FirstOracleConstants.transCubeSpaceToDiscrete( x, shift.x() );
    }
    
    @Override
    public int yPositionToIndex( float y, Index3D shift ) {
        return ( int ) FirstOracleConstants.transCubeSpaceToDiscrete( y, shift.y() );
    }
    
    @Override
    public int zPositionToIndex( float z, Index3D shift ) {
        return ( int ) FirstOracleConstants.transCubeSpaceToDiscrete( z, shift.z() );
    }
}
