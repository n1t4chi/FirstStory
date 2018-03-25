/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
public abstract class Vertices< BoundingBox > extends VertexAttribute {
    
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
    
    public int bind( VertexAttributeLoader loader, int frame ) {
        loader.bindBuffer( this, frame );
        return getVertexLength( frame );
    }
    
    @Override
    public String toString() {
        return "Vertices: " + Arrays.deepToString( verticesByFrame );
    }
    
    @Override
    public float[] getArray( long key ) {
        return verticesByFrame[ ( int ) key ];
    }
    
    public int getIndex() {
        return 0;
    }
}
