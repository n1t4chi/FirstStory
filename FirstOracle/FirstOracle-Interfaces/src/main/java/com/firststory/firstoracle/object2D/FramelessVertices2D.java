/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.VertexAttributeLoader;

/**
 * @author n1t4chi
 */
public class FramelessVertices2D extends Vertices2D {
    
    public FramelessVertices2D( float[] vertices ) {
        super( new float[][]{ vertices } );
    }
    
    public int bind( VertexAttributeLoader loader ) {
        return super.bind( loader, 0 );
    }
}