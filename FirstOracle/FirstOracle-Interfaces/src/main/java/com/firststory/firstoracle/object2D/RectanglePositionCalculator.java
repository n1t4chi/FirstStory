/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index2D;

/**
 * @author n1t4chi
 */
public class RectanglePositionCalculator implements Position2DCalculator {
    
    public static final RectanglePositionCalculator instance = new RectanglePositionCalculator();
    
    @Override
    public float xIndexToPosition( int x, Index2D shift ) {
        return FirstOracleConstants.transPlaneDiscreteToSpace( x, shift.x() );
    }
    
    @Override
    public float yIndexToPosition( int y, Index2D shift ) {
        return FirstOracleConstants.transPlaneDiscreteToSpace( y, shift.y() );
    }
    
    @Override
    public int xPositionToIndex( float x, Index2D shift ) {
        return ( int ) FirstOracleConstants.transPlaneSpaceToDiscrete( x, shift.x() );
    }
    
    @Override
    public int yPositionToIndex( float y, Index2D shift ) {
        return ( int ) FirstOracleConstants.transPlaneSpaceToDiscrete( y, shift.y() );
    }
}
