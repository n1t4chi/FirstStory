/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import org.joml.Matrix4f;
import org.junit.jupiter.api.*;

import static com.firststory.firstoracle.scene.SceneTestUtils.vec3;

class CameraViewTest {
    
    @Test
    void testPointsOnIdentityMatrix() {
        var view = new CameraView2D( Matrix4f::new );
    
        Assertions.assertEquals( vec3( -1.1f, 1.1f, 0 ), view.getHighLeft().getPoint0() );
        Assertions.assertEquals( vec3( 1.1f, 1.1f, 0 ), view.getRight().getPoint0() );
        Assertions.assertEquals( vec3( 1.1f, -1.1f, 0 ), view.getLowRight().getPoint0() );
        Assertions.assertEquals( vec3( -1.1f, -1.1f, 0 ), view.getLeft().getPoint0() );
    }
    
    
    @Test
    void testPointsOn90DegRotIdMatrix() {
        var view = new CameraView2D( () -> new Matrix4f().rotateZ( ( float ) Math.toRadians( 45 ) ) );
        var root2 = ( float ) Math.pow( 2, 0.5 );
        Assertions.assertEquals( vec3( 0, root2*1.1f, 0 ), view.getHighLeft().getPoint0() );
        Assertions.assertEquals( vec3( root2*1.1f, 0, 0 ), view.getRight().getPoint0() );
        Assertions.assertEquals( vec3( 0, -root2*1.1f, 0 ), view.getLowRight().getPoint0() );
        Assertions.assertEquals( vec3( -root2*1.1f, 0, 0 ), view.getLeft().getPoint0() );
    }
}