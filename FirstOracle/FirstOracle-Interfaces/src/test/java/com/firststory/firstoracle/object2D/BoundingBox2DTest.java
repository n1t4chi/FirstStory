/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author n1t4chi
 */
public class BoundingBox2DTest {
    
    @Test
    public void testBBO() {
        BoundingBox2D bb = new BoundingBox2D( -1, 1, -1, 1 );
        assertBBO( bb, 0, 0, 1, 1, 0, -1, 1, -1, 1 );
        assertBBO( bb, 10, 10, 1, 1, 0, 9, 11, 9, 11 );
        assertBBO( bb, 0, 0, 10, 10, 0, -10, 10, -10, 10 );
        assertBBO( bb, 10, 10, 10, 5, 0, 0, 20, 5, 15 );
        assertBBO( bb, 10, 10, 10, 5, 180, 0, 20, 5, 15 );
        assertBBO( bb, 10, 10, 10, 5, 90, 5, 15, 0, 20 );
        assertBBO( bb, 10, 10, 10, 5, -90, 5, 15, 0, 20 );
        
    }
    
    private void assertBBO(
        BoundingBox2D bb,
        int x,
        int y,
        int sx,
        int sy,
        float rot,
        int minX,
        int maxX,
        int minY,
        int maxY
    ) {
        Vector2fc scale = new Vector2f( sx, sy );
        Vector2fc pos = new Vector2f( x, y );
        BoundingBox2D assertBB = bb.getTransformedBoundingBox( new Object2DTransformations() {
            @Override
            public Vector2fc getScale() {
                return scale;
            }
    
            @Override
            public Float getRotation() {
                return rot;
            }
    
            @Override
            public Vector2fc getPosition() {
                return pos;
            }
        } );
        Assert.assertEquals( minX, assertBB.getMinX(), 0.001 );
        Assert.assertEquals( maxX, assertBB.getMaxX(), 0.001 );
        Assert.assertEquals( minY, assertBB.getMinY(), 0.001 );
        Assert.assertEquals( maxY, assertBB.getMaxY(), 0.001 );
    }
}