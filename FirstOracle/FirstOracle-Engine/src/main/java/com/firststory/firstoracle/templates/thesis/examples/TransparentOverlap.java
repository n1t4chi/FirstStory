/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.thesis.examples;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.object3D.NonAnimatedPlane3D;
import com.firststory.firstoracle.window.WindowBuilder;

/**
 * @author n1t4chi
 */
public class TransparentOverlap {
    
    public static void main( String[] args ) throws Exception {
        var front = new NonAnimatedPlane3D() {
            @Override
            public Colour getOverlayColour() {
                return Colour.col( 1,0,0,0.5f );
            }
        };
        front.setTexture( FirstOracleConstants.EMPTY_TEXTURE );
        front.setPosition( Position3D.pos3( 0, 0, 0 ) );
        
        var back = new NonAnimatedPlane3D() {
            @Override
            public Colour getOverlayColour() {
                return Colour.col( 0,0,1,0.5f );
            }
        };
        back.setTexture( FirstOracleConstants.EMPTY_TEXTURE );
        back.setPosition( Position3D.pos3( 1, 1, -1 ) );
        
        new Thread( () -> display( back, front,  "Expected", 0, 0 ) ).start();
        Thread.sleep( 1000 );
        new Thread( () -> display( front, back, "Invalid", 0, 0 ) ).start();
        Thread.sleep( 1000 );
        new Thread( () -> display( back, front,  "Expected - side", 30, 10 ) ).start();
        Thread.sleep( 1000 );
        new Thread( () -> display( front, back, "Invalid - side", 30, 10 ) ).start();
    }
    
    public static void display(
        NonAnimatedPlane3D plane1,
        NonAnimatedPlane3D plane2,
        String name,
        float rotX,
        float rotY
    ) {
        var width = 300;
        var height = 300;
        var settings = WindowSettings.builder().setVerticalSync( false )
            .setTitle( name )
            .setResizeable( false )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .build();
        
        var window = WindowBuilder.registrableWindow( settings ).build();
    
    
        var plane3 = new NonAnimatedPlane3D() {
            @Override
            public Colour getOverlayColour() {
                return Colour.col( 0,1,0,1f );
            }
        };
        plane3.setTexture( FirstOracleConstants.EMPTY_TEXTURE );
        plane3.setPosition( Position3D.pos3( 0, 0, -2 ) );
        plane3.setRotation( Rotation3D.rot3( 0, 0, 45f ) );
        
        window.createNewScene3D( 0, FirstOracleConstants.INDEX_ZERO_3I, FirstOracleConstants.INDEX_ZERO_3I );
        window.registerObject3D( 0, plane3 );
        window.registerObject3D( 0, plane1 );
        window.registerObject3D( 0, plane2 );
        window.setBackgroundColour( 0, FirstOracleConstants.WHITE );
        window.setScene3DCamera( 0, new IsometricCamera3D( settings, 3, 0, 0, 0, -30 + rotX,-45+ rotY,0 ) );
        
        window.run();
    }
}
