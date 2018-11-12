/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * @author n1t4chi
 */
public class Scale3D extends Scale {
    
    public static Scale3D scale3( Vector3fc vec ) {
        return scale3( vec.x(), vec.y(), vec.z() );
    }
    
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
    
    public Vector3fc toVec3f() {
        return new Vector3f( x(), y(), z() );
    }
}
