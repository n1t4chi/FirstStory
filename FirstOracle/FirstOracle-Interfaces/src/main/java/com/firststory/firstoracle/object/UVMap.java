/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author: n1t4chi
 */
public class UVMap extends VertexAttributes {
    
    public UVMap( float[][] verticesByFrame ) {
        super( verticesByFrame );
    }
    
    @Override
    protected int getVertexSize() {
        return 2;
    }
    
    @Override
    protected int getIndex() {
        return 1;
    }
}
