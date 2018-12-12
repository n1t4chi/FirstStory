/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.thesis.examples;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object3D.StaticCube;
import com.firststory.firstoracle.window.WindowBuilder;
import org.joml.Matrix4f;

import java.io.IOException;

import static java.lang.Float.POSITIVE_INFINITY;

/**
 * @author n1t4chi
 */
public class PerspectiveComparison {
    
    public static void main( String[] args ) throws Exception {
        var perspective = new Matrix4f()
            .setPerspective( 90, 1, 1, POSITIVE_INFINITY )
            .lookAtPerspective(
                -3f, 3f, 3f,
                0, 0, 0,
                0, 1, 0,
                new Matrix4f()
            )
            .scale( 2 )
        ;
        
        var ortho = new Matrix4f()
            .ortho( -3,3,-3,3,-3,3 )
        ;
        var isometric = new Matrix4f()
            .ortho( -3,3,-3,3,-3,3 )
            .rotateX( ( float ) Math.toRadians( 30 ) )
            .rotateY( ( float ) Math.toRadians( 45 ) )
        ;
    
        new Thread( () -> display( perspective, "perspective" ) ).start();
        Thread.sleep( 2000 );
        new Thread( () -> display( ortho, "orthogonal" ) ).start();
        Thread.sleep( 2000 );
        new Thread( () -> display( isometric, "isometric" ) ).start();
    }
    
    public static void display( Matrix4f matrix4f, String name ) {
        System.setProperty( "DrawBorder", "true" );
        var width = 700;
        var height = 700;
        var settings = WindowSettings.builder().setVerticalSync( false )
            .setTitle( name )
            .setResizeable( false )
            .setWindowMode( WindowMode.WINDOWED )
            .setWidth( width )
            .setHeight( height )
            .build();
        
        var window = WindowBuilder.registrableWindow( settings ).build();
        
        var cube = new StaticCube();
        Texture texture2D = null;
        try {
            texture2D = Texture.create( "resources/First Oracle/texture3Dc.png" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        cube.setTexture( texture2D );
        
        window.createNewScene3D( 0, FirstOracleConstants.INDEX_ZERO_3I, FirstOracleConstants.INDEX_ZERO_3I );
        window.setBackgroundColour( 0, FirstOracleConstants.WHITE );
        var camera = new DummyCamera3D( matrix4f );
        window.setScene3DCamera( 0, camera );
        window.registerObject3D( 0, cube );
        
        window.run();
    }
    
}
