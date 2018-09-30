/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object.data;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public class Scale2D extends Scale {
    
    public static Scale2D scale2( float x, float y ) {
        return new Scale2D( x, y );
    }
    
    private Scale2D( float x, float y ) {
        super( x, y, 0 );
    }
    
    @Override
    public String toString() {
        return "Scale2D{ " + x() + ", " + y() + " }";
    }
    
    public Vector2fc toVec2f() {
        return new Vector2f( x(), y() );
    }
}
