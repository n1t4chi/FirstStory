/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.firststory.firstoracle.scene.SceneTestUtils.vec3;

public class Line3DTest {
    
    private static final Line3D CENTER_45DEG= new Line3D( vec3( 0, 0, 0 ), vec3( 1, 1, 1 ) );
    private static final Line3D CENTER_STRAIGHT_UP = new Line3D( vec3( 1, 1, 1 ), vec3( 1, 2, 1 ) );
    private static final Line3D CENTER_CONSTANT_X = new Line3D( vec3( 1, 2, 3 ), vec3( 1, 3, 5 ) );
    
    @Test
    public void getPointAtX() {
        Assertions.assertEquals( vec3( 3, 3, 3 ), CENTER_45DEG.getPointAtX( 3 ) );
        Assertions.assertEquals( vec3( 5, 5, 5 ), CENTER_45DEG.getPointAtX( 5 ) );
    
        Assertions.assertEquals( vec3( 1,1,1 ), CENTER_STRAIGHT_UP.getPointAtX( 1 ) );
        Assertions.assertNull( CENTER_STRAIGHT_UP.getPointAtX( 5 ) );
        
        Assertions.assertEquals( vec3( 1,2,3 ), CENTER_CONSTANT_X.getPointAtX( 1 ) );
        Assertions.assertNull(  CENTER_CONSTANT_X.getPointAtX( 5 ) );
    }
    
    @Test
    public void getPointAtY() {
        Assertions.assertEquals( vec3( 3, 3, 3 ), CENTER_45DEG.getPointAtY( 3 ) );
        Assertions.assertEquals( vec3( 5, 5, 5 ), CENTER_45DEG.getPointAtY( 5 ) );
    
        Assertions.assertEquals( vec3( 1,0,1 ), CENTER_STRAIGHT_UP.getPointAtY( 0 ) );
        Assertions.assertEquals( vec3( 1,5,1 ), CENTER_STRAIGHT_UP.getPointAtY( 5 ) );
    
        Assertions.assertEquals( vec3( 1,0,-1 ), CENTER_CONSTANT_X.getPointAtY( 0 ) );
        Assertions.assertEquals( vec3( 1,5,9 ), CENTER_CONSTANT_X.getPointAtY( 5 ) );
    }
    
    @Test
    public void getPointAtZ() {
        Assertions.assertEquals( vec3( 3, 3, 3 ), CENTER_45DEG.getPointAtZ( 3 ) );
        Assertions.assertEquals( vec3( 5, 5, 5 ), CENTER_45DEG.getPointAtZ( 5 ) );
    
        Assertions.assertEquals( vec3( 1,1,1 ), CENTER_STRAIGHT_UP.getPointAtZ( 1 ) );
        Assertions.assertNull( CENTER_STRAIGHT_UP.getPointAtZ( 5 ) );
    
        Assertions.assertEquals( vec3( 1,0.5f,0 ), CENTER_CONSTANT_X.getPointAtZ( 0 ) );
        Assertions.assertEquals( vec3( 1,4,7 ), CENTER_CONSTANT_X.getPointAtZ( 7 ) );
    }
}