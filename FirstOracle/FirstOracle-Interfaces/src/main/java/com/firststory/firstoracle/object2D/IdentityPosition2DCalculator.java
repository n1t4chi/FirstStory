/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Index2D;

/**
 * @author n1t4chi
 */
public class IdentityPosition2DCalculator implements Position2DCalculator {
    
    public static final IdentityPosition2DCalculator instance = new IdentityPosition2DCalculator();
    
    @Override
    public float xIndexToPosition( int x, Index2D shift ) {
        return x + shift.x();
    }
    
    @Override
    public float yIndexToPosition( int y, Index2D shift ) {
        return y + shift.y();
    }
    
    @Override
    public int xPositionToIndex( float x, Index2D shift ) {
        return ( int ) ( x - shift.x() );
    }
    
    @Override
    public int yPositionToIndex( float y, Index2D shift ) {
        return ( int ) ( y - shift.y() );
    }
}
