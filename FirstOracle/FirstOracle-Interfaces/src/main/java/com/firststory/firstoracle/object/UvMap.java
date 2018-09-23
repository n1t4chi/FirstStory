/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.Arrays;
import java.util.List;

/**
 * @author n1t4chi
 */
public class UvMap extends VertexAttribute< UV > {
    
    private final List< UV >[][] uvMapByDirectionAndFrame;
    
    public UvMap( List< UV >[][] uvMapByDirectionAndFrame ) {
        this.uvMapByDirectionAndFrame = uvMapByDirectionAndFrame;
    }
    
    @Override
    protected List< UV > getData( int... parameters ) {
        assertParameters( parameters );
        return uvMapByDirectionAndFrame[ parameters[0] ][ parameters[1] ];
    }
    
    @Override
    protected long getKey( int... parameters ) {
        assertParameters( parameters );
        return ( ( long ) parameters[ 0 ] << 32 ) + parameters[ 1 ];
    }
    
    private void assertParameters( int[] parameters ) {
        if ( parameters.length != 2 ) {
            throw new IllegalArgumentException( "Illegal parameter length" );
        }
    }
    
    @Override
    public String toString() {
        return "UvMap:{" + Arrays.deepToString( uvMapByDirectionAndFrame ) + '}';
    }
}
