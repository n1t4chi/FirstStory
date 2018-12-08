/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;

/**
 * @author n1t4chi
 */
public class HexPrismPositionCalculator implements Position3DCalculator {
    
    public static final HexPrismPositionCalculator instance = new HexPrismPositionCalculator();
    
    @Override
    public float xIndexToPosition( int x, Index3D shift ) {
        return FirstOracleConstants.transHexPrismXDiscreteToSpace( x, shift.x() );
    }
    
    @Override
    public float yIndexToPosition( int y, Index3D shift ) {
        return FirstOracleConstants.transHexPrismYDiscreteToSpace( y, shift.y() );
    }
    
    @Override
    public float zIndexToPosition( int z, Index3D shift ) {
        return zIndexToPosition( 0, z, shift );
    }
    
    public float zIndexToPosition( int x, int z, Index3D shift ) {
        return FirstOracleConstants.transHexPrismZDiscreteToSpace( x, z, shift.x(), shift.z() );
    }
    
    @Override
    public int xPositionToIndex( float x, Index3D shift ) {
        return ( int ) FirstOracleConstants.transHexPrismXSpaceToDiscrete( x, shift.x() );
    }
    
    @Override
    public int yPositionToIndex( float y, Index3D shift ) {
        return ( int ) FirstOracleConstants.transHexPrismYSpaceToDiscrete( y, shift.y() );
    }
    
    @Override
    public int zPositionToIndex( float z, Index3D shift ) {
        return zPositionToIndex( 0, z, shift );
    }
    
    public int zPositionToIndex( float x, float z, Index3D shift ) {
        return ( int ) FirstOracleConstants.transHexPrismZSpaceToDiscrete( x, z, shift.x(), shift.z() );
    }
    
    @Override
    public Index3D positionToIndex( float x, float y, float z, Index3D shift ) {
        return Index3D.id3(
            xPositionToIndex( x, shift ),
            yPositionToIndex( y, shift ),
            zPositionToIndex( x, z, shift )
        );
    }
    
    @Override
    public Position3D indexToPosition( int x, int y, int z, Index3D shift ) {
        return Position3D.pos3(
            xIndexToPosition( x, shift ),
            yIndexToPosition( y, shift ),
            zIndexToPosition( x, z, shift )
        );
    }
}
