/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.data.Scale2D;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author n1t4chi
 */
public class BoundingBox2DTest {
    
    @Test
    public void testBBO() {
        var bb = new BoundingBox2D( -1, 1, -1, 1 );
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
        float angle,
        int minX,
        int maxX,
        int minY,
        int maxY
    ) {
        var scale = Scale2D.scale2( sx, sy );
        var pos = Position2D.pos2( x, y );
        var rot = Rotation2D.rot2( angle );
        var assertBB = bb.getTransformedBoundingBox( new PositionableObject2DTransformations() {
            @Override
            public Scale2D getScale() {
                return scale;
            }
    
            @Override
            public Rotation2D getRotation() {
                return rot;
            }
    
            @Override
            public Position2D getPosition() {
                return pos;
            }
        } );
        Assert.assertEquals( minX, assertBB.getMinX(), 0.001 );
        Assert.assertEquals( maxX, assertBB.getMaxX(), 0.001 );
        Assert.assertEquals( minY, assertBB.getMinY(), 0.001 );
        Assert.assertEquals( maxY, assertBB.getMaxY(), 0.001 );
    }
}