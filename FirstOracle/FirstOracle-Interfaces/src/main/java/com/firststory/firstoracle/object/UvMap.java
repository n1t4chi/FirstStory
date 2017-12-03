/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public class UvMap extends VertexAttributes {
    
    private final float[][][] uvMapByDirectionAndFrame;
    
    public UvMap( float[][][] uvMapByDirectionAndFrame ) {
        this.uvMapByDirectionAndFrame = uvMapByDirectionAndFrame;
    }
    
    public void bind( int direction, int frame ) {
        bindBufferAndGetSize( ( ( long ) direction << 32 ) + frame );
    }
    
    protected int getIndex() {
        return 1;
    }
    
    protected int getVertexSize() {
        return 2;
    }
    
    @Override
    protected float[] getArray( long key ) {
        return uvMapByDirectionAndFrame[( int ) ( key >> 32 )][( int ) ( key )];
    }
}
