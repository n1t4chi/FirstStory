/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

/**
 * @author n1t4chi
 */
public class FramelessVertices3D extends Vertices3D {

    public FramelessVertices3D( float[] vertices ) {
        super( new float[][]{vertices} );
    }

    public int bind() {
        return super.bind(0);
    }
}
