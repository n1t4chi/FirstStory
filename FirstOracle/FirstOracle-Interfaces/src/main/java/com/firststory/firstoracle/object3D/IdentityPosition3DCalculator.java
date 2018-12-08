/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.data.Index3D;

/**
 * @author n1t4chi
 */
public class IdentityPosition3DCalculator implements Position3DCalculator {
    
    public static final IdentityPosition3DCalculator instance = new IdentityPosition3DCalculator();
    
    @Override
    public float xIndexToPosition( int x, Index3D shift ) {
        return x + shift.x();
    }
    
    @Override
    public float yIndexToPosition( int y, Index3D shift ) {
        return y + shift.y();
    }
    
    @Override
    public float zIndexToPosition( int z, Index3D shift ) {
        return z + shift.z();
    }
    
    @Override
    public int xPositionToIndex( float x, Index3D shift ) {
        return ( int ) (x - shift.x());
    }
    
    @Override
    public int yPositionToIndex( float y, Index3D shift ) {
        return ( int ) (y - shift.y());
    }
    
    @Override
    public int zPositionToIndex( float z, Index3D shift ) {
        return ( int ) (z - shift.z());
    }
}
