/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import org.joml.Vector3fc;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.firststory.firstoracle.FirstOracleConstants.MID_DELTA;
import static com.firststory.firstoracle.scene.SceneTestUtils.vec3;

class PlaneTest {
    
    @Test
    void returnsValidMinMaxValues() {
        var plane = new Plane(
            vec3( -3f, 2.5f, 0 ),
            vec3( 3.5f, 1.5f, 0 ),
            vec3( 1.5f, -5f, 0 ),
            vec3( -4.5f, -2.5f, 0 ),
            Vector3fc::x,
            Vector3fc::y
        );
        Assertions.assertEquals( 4, plane.maxDim1_Ceil(), MID_DELTA );
        Assertions.assertEquals( -5, plane.minDim1_Floor(), MID_DELTA );
        Assertions.assertEquals( 3, plane.maxDim2_Ceil(), MID_DELTA );
        Assertions.assertEquals( -5, plane.minDim2_Floor(), MID_DELTA );
    }
    
    @Test
    void returnsNullIfOutside() {
        var plane = new Plane(
            vec3( -1, 1, 0 ),
            vec3( 1, 1, 0 ),
            vec3( 1, -1, 0 ),
            vec3( -1, -1, 0 ),
            Vector3fc::x,
            Vector3fc::y
        );
    
        var out1 = plane.getBoundsAtDim1( -2 );
        Assertions.assertNull( out1 );
    
        var out2 = plane.getBoundsAtDim1( 2 );
        Assertions.assertNull( out2 );
        
    }
    
    @ParameterizedTest(name = "bounds @x=\"{0}\" should be ( {1}, {2} )")
    @CsvSource( {
        "4, 1, 2",
        "-5, -3, -2",
        "1.5, -5, 2",
        "-3, -4, 3",
        "0, -5, 3",
        "0.25, -5, 2",
    } )
    void returnsValidBoundariesOnXs1( float x, int expectedMin, int expectedMax ) {
        var plane = new Plane(
            vec3( -3f, 2.5f, 0 ),
            vec3( 3.5f, 1.5f, 0 ),
            vec3( 1.5f, -5f, 0 ),
            vec3( -4.5f, -2.5f, 0 ),
            Vector3fc::x,
            Vector3fc::y
        );
        Assertions.assertEquals( new Bounds( expectedMin, expectedMax ) , plane.getBoundsAtDim1( x ) );
    }
    
    @ParameterizedTest(name = "bounds @x=\"{0}\" should be ( {1}, {2} )")
    @CsvSource( {
        "1, -1, 1",
        "-1, -1, 1",
        "0.5, -1, 1",
        "1, -1, 1",
        "0.5, -1, 1",
    } )
    void returnsValidBoundariesOnXs2( float x, int expectedMin, int expectedMax ) {
        var plane = new Plane(
            vec3( -1, 1, 0 ),
            vec3( 1, 1, 0 ),
            vec3( 1, -1, 0 ),
            vec3( -1, -1, 0 ),
            Vector3fc::x,
            Vector3fc::y
        );
        Assertions.assertEquals( new Bounds( expectedMin, expectedMax ) , plane.getBoundsAtDim1( x ) );
    }
}