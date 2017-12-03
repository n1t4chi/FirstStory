/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public abstract class Vertices< BoundingBox > extends VertexAttributes {
    
    private final BoundingBox boundingBox;
    private float[][] verticesByFrame;
    
    public Vertices( float[][] verticesByFrame, BoundingBox boundingBox ) {
        setVertices( verticesByFrame );
        this.boundingBox = boundingBox;
    }
    
    public void setVertices( float[][] verticesByFrame ) {
        close();
        this.verticesByFrame = verticesByFrame;
    }
    
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
    
    public int bind( int frame ) {
        return bindBufferAndGetSize( frame );
    }
    
    @Override
    public String toString() {
        return "Vertices: " + Arrays.deepToString( verticesByFrame );
    }
    
    @Override
    protected float[] getArray( long key ) {
        return verticesByFrame[ ( int ) key ];
    }
    
    protected int getIndex() {
        return 0;
    }
}
