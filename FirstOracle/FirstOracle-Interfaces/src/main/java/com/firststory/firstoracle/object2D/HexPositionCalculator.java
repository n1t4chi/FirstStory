/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.*;

/**
 * @author n1t4chi
 */
public class HexPositionCalculator implements Position2DCalculator {
    
    public static final HexPositionCalculator instance = new HexPositionCalculator();
    
    @Override
    public float xIndexToPosition( int x, Index2D shift ) {
        return FirstOracleConstants.transHexXDiscreteToSpace( x, shift.x() );
    }
    
    @Override
    public float yIndexToPosition( int y, Index2D shift ) {
        return yIndexToPosition( 0, y, shift );
    }
    
    public float yIndexToPosition( int x, int y, Index2D shift ) {
        return FirstOracleConstants.transHexYDiscreteToSpace( x, y, shift.x(), shift.y() );
    }
    
    @Override
    public int xPositionToIndex( float x, Index2D shift ) {
        return ( int ) FirstOracleConstants.transHexXSpaceToDiscrete( x, shift.x() );
    }
    
    @Override
    public int yPositionToIndex( float y, Index2D shift ) {
        return yPositionToIndex( 0, y, shift );
    }
    
    public int yPositionToIndex( float x, float y, Index2D shift ) {
        return ( int ) FirstOracleConstants.transHexYSpaceToDiscrete( x, y, shift.x(), shift.y() );
    }
    
    @Override
    public Position2D indexToPosition( int x, int y, Index2D shift ) {
        return Position2D.pos2(
            xIndexToPosition( x, shift ),
            yIndexToPosition( x, y, shift )
        );
    }
    
    @Override
    public Index2D positionToIndex( float x, float y, Index2D shift ) {
        return Index2D.id2(
            xPositionToIndex( x, shift ),
            yPositionToIndex( x, y, shift )
        );
    }
}
