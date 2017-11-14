/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.camera2D;

import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Math.pow;
import static java.lang.Math.toRadians;
import static org.junit.Assert.*;

/**
 * @author n1t4chi
 */
public class MovableCamera2DTest {

    Vector3f vector;
    Matrix3f camera;

    @Test
    public void test() {
        //identity
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f();
        assertVector( 3f, 2f );

        //scale
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().scale( 1, 1, 1 );
        assertVector( 3f, 2f );
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().scale( 3, 4, 1 );
        assertVector( 9f, 8f );

        //rotation
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().rotateZ( ( float ) toRadians( 180 ) );
        assertVector( -3f, -2f );
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().rotateZ( ( float ) toRadians( 90 ) );
        assertVector( -2f, 3f );
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().rotateZ( ( float ) toRadians( -90 ) );
        assertVector( 2f, -3f );
        vector = new Vector3f( 1, 1, 1 );
        camera = new Matrix3f().rotateZ( ( float ) toRadians( 45 ) );
        assertVector( 0, ( float ) pow(2,0.5) );
        vector = new Vector3f( 1, 1, 1 );
        camera = new Matrix3f().rotateZ( ( float ) toRadians( -45 ) );
        assertVector( ( float ) pow(2,0.5),0 );

        //translation
        vector = new Vector3f( 3, 2, 1 );
        camera = translate(new Matrix3f(), 0,0);
        assertVector( 3, 2 );

        vector = new Vector3f( 3, 2, 1 );
        camera = translate(new Matrix3f(), 1,3);
        assertVector( 4, 5 );

        vector = new Vector3f( 3, 2, 1 );
        camera = translate(new Matrix3f(), -3,-2);
        assertVector( 0, 0 );


        //scale then rotate
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().scale(2).rotateZ( ( float ) toRadians( 180 ) );
        assertVector( -6f, -4f );
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().scale(2).rotateZ( ( float ) toRadians( 90 ) );
        assertVector( -4f, 6f );
        vector = new Vector3f( 3, 2, 1 );
        camera = new Matrix3f().scale(2).rotateZ( ( float ) toRadians( -90 ) );
        assertVector( 4f, -6f );
        vector = new Vector3f( 1, 1, 1 );
        camera = new Matrix3f().scale(2).rotateZ( ( float ) toRadians( 45 ) );
        assertVector( 0, 2f*( float ) pow(2,0.5) );
        vector = new Vector3f( 1, 1, 1 );
        camera = new Matrix3f().scale(2).rotateZ( ( float ) toRadians( -45 ) );
        assertVector( 2f*( float ) pow(2,0.5),0 );


        //scale then rotate then translate
        vector = new Vector3f( 3, 2, 1 );
        camera = translate( new Matrix3f().scale(2).rotateZ( ( float ) toRadians( 180 ) ) , 10, 10);
        assertVector( 10-6f, 10-4f );
        vector = new Vector3f( 3, 2, 1 );
        camera = translate( new Matrix3f().scale(2).rotateZ( ( float ) toRadians( 90 ) ) , 10, 10);
        assertVector( 10-4f, 10+6f );
        vector = new Vector3f( 3, 2, 1 );
        camera = translate( new Matrix3f().scale(2).rotateZ( ( float ) toRadians( -90 ) ) , 10, 10);
        assertVector( 10+4f, 10-6f );
        vector = new Vector3f( 1, 1, 1 );
        camera = translate( new Matrix3f().scale(2).rotateZ( ( float ) toRadians( 45 ) ) , 10, 10);
        assertVector( 10+0, 10+(2f*( float ) pow(2,0.5)) );
        vector = new Vector3f( 1, 1, 1 );
        camera = translate( new Matrix3f().scale(2).rotateZ( ( float ) toRadians( -45 ) ) , 10, 10);
        assertVector( 10+(2f*( float ) pow(2,0.5)),10+0 );

    }

    private Matrix3f translate(Matrix3f camera, float x, float y){
        camera.m20 = x;
        camera.m21 = y;
        camera.m22 = 1;
        return camera;
    }

    private void assertVector( float expectedX, float expectedY ) {
        camera.transform( vector );
        assertEquals( "X",expectedX, vector.x, 0.001 );
        assertEquals( "Y",expectedY, vector.y, 0.001 );
    }
}