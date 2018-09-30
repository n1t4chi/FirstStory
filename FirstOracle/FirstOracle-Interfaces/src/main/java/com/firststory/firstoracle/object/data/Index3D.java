/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

/**
 * @author n1t4chi
 */
public class Index3D extends Index {
    
    public static Index3D id3( int x, int y, int z ) {
        return new Index3D( x, y, z );
    }
    
    private Index3D( int x, int y, int z ) {
        super( x, y, z );
    }
    
    @Override
    public String toString() {
        return "Index3D{ " + x() + ", " + y() + ", " + z() + " }";
    }
}
