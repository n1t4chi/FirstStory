/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * @author n1t4chi
 */
public class FramelessVertices2D extends Vertices2D {
    
    public FramelessVertices2D( float[] vertices ) {
        super( new float[][]{ vertices } );
    }
    
    public int bind() {
        return super.bind( 0 );
    }
}