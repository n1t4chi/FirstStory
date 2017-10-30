/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author: n1t4chi
 */
public class Vertices extends VertexAttributes {
    
    private final BoundingBox boundingBox;
    
    public Vertices( float[][] verticesByFrame ) {
        super( verticesByFrame );
        float minX, maxX, minY, maxY, minZ, maxZ;
        minX = minY = minZ = Float.MAX_VALUE;
        maxZ = maxY = maxX = -Float.MAX_VALUE;
        for ( float[] vertices : verticesByFrame ) {
            for ( int i = 0; i < vertices.length; i += 3 ) {
                if ( vertices[i] < minX ) { minX = vertices[i]; }
                if ( vertices[i] > maxX ) { maxX = vertices[i]; }
                if ( vertices[i + 1] < minY ) { minY = vertices[i]; }
                if ( vertices[i + 1] > maxY ) { maxY = vertices[i]; }
                if ( vertices[i + 2] < minZ ) { minZ = vertices[i]; }
                if ( vertices[i + 2] > maxZ ) { maxZ = vertices[i]; }
            }
        }
        boundingBox = new BoundingBox( minX,maxX,minY,maxY,minZ,maxZ );
    }
    
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
    
    @Override
    protected int getVertexSize() {
        return 3;
    }
    
    @Override
    protected int getIndex() {
        return 0;
    }
    
}
