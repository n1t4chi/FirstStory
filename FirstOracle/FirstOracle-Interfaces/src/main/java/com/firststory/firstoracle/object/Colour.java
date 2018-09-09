/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public class Colour extends VertexAttribute {
    
    private final float[] colour;
    
    public Colour( float[] colour ) {
        this.colour = colour;
    }
    
    public void bind( VertexAttributeLoader loader ) {
        loader.bindBuffer( this, 0 );
    }
    
    public int getIndex() {
        return 1;
    }
    
    public int getVertexSize() {
        return 2;
    }
    
    @Override
    public float[] getArray( long key ) {
        return colour;
    }
}
