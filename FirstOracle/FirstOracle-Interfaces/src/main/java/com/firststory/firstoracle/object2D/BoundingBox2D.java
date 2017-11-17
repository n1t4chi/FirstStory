/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.BoundingBox;
import org.joml.Vector2fc;

/**
 * @author n1t4chi
 */
public class BoundingBox2D implements BoundingBox< BoundingBox2D, ObjectTransformations2D > {

    public static BoundingBox2D getBoundingBox2D( float[] vertices ) {
        float minX, maxX, minY, maxY;
        minX = minY = Float.MAX_VALUE;
        maxY = maxX = -Float.MAX_VALUE;
        for ( int i = 0; i < vertices.length; i += 3 ) {
            if ( vertices[i] < minX ) { minX = vertices[i]; }
            if ( vertices[i] > maxX ) { maxX = vertices[i]; }
            if ( vertices[i + 1] < minY ) { minY = vertices[i]; }
            if ( vertices[i + 1] > maxY ) { maxY = vertices[i]; }
        }
        return new BoundingBox2D( minX, maxX, minY, maxY );
    }

    public static BoundingBox2D getBoundingBox2D( float[][] verticesArray ) {
        float minX, maxX, minY, maxY, minZ, maxZ;
        minX = minY = Float.MAX_VALUE;
        maxY = maxX = -Float.MAX_VALUE;
        for ( float[] vertices : verticesArray ) {
            for ( int i = 0; i < vertices.length; i += 3 ) {
                if ( vertices[i] < minX ) { minX = vertices[i]; }
                if ( vertices[i] > maxX ) { maxX = vertices[i]; }
                if ( vertices[i + 1] < minY ) { minY = vertices[i]; }
                if ( vertices[i + 1] > maxY ) { maxY = vertices[i]; }
            }
        }
        return new BoundingBox2D( minX, maxX, minY, maxY );
    }

    private final float minX, maxX, minY, maxY;

    public BoundingBox2D( float minX, float maxX, float minY, float maxY ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public BoundingBox2D getTransformedBoundingBox( ObjectTransformations2D transformations ) {
        float minX, maxX, minY, maxY;
        minX = this.minX;
        maxX = this.maxX;
        minY = this.minY;
        maxY = this.maxY;
        Float rotation = transformations.getRotation();
        Vector2fc position = transformations.getPosition();
        Vector2fc scale = transformations.getScale();
        if ( rotation != 0 ) {
            minX *= 1.5;
            maxX *= 1.5;
            minY *= 1.5;
            maxY *= 1.5;
        }
        minX *= scale.x();
        minX += position.x();
        maxX *= scale.x();
        maxX += position.x();
        minY *= scale.y();
        minY += position.y();
        maxY *= scale.y();
        maxY += position.y();
        return new BoundingBox2D( minX, maxX, minY, maxY );
    }

    /**
     * Returns whether the given bounding box intersects with this bounding box
     *
     * @param boundingBox2D
     * @return true if they intersect
     */
    public boolean intersects( BoundingBox2D boundingBox2D ) {
        return minX <= boundingBox2D.maxX && maxX >= boundingBox2D.minX &&
               minY <= boundingBox2D.maxY && maxY >= boundingBox2D.minY;
    }

    @Override
    public String toString() {
        return "BBox2D: [" + minX + "," + maxX + "] [" + minY + "," + maxY + "]";
    }
}
