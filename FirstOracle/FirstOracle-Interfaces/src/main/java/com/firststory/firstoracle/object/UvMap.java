/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public class UvMap extends VertexAttribute {
    
    private final float[][][] uvMapByDirectionAndFrame;
    
    public UvMap( float[][][] uvMapByDirectionAndFrame ) {
        this.uvMapByDirectionAndFrame = uvMapByDirectionAndFrame;
    }
    
    public void bind( VertexAttributeLoader loader, int direction, int frame ) {
        loader.bindBuffer( this, ( ( long ) direction << 32 ) + frame );
    }
    
    public int getIndex() {
        return 1;
    }
    
    public int getVertexSize() {
        return 2;
    }
    
    @Override
    public float[] getArray( long key ) {
        return uvMapByDirectionAndFrame[( int ) ( key >> 32 )][( int ) ( key )];
    }
}
