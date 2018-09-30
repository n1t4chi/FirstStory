/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

/**
 * @author n1t4chi
 */
public class Scale3D extends Scale {
    
    public static Scale3D scale3( float x, float y, float z ) {
        return new Scale3D( x, y, z );
    }
    
    private Scale3D( float x, float y, float z ) {
        super( x, y, z );
    }
    
    @Override
    public String toString() {
        return "Scale3D{ " + x() + ", " + y() + ", " + z() + " }";
    }
}
