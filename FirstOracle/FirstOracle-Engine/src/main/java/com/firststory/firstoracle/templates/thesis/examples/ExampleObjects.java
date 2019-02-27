/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.thesis.examples;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.controller.*;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.object3D.*;
import com.firststory.firstoracle.window.WindowBuilder;

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author n1t4chi
 */
public class ExampleObjects {
    
    
    
    
    public static void main( String[] args ) throws Exception {
        var tex3D = Texture.create( "resources/First Oracle/textures/3D.png" );
        var tex2D = Texture.create( "resources/First Oracle/textures/2D.png" );
        
        var cube = new StaticCubeGrid();
        cube.setTexture( tex3D );
        var hexPrism = new StaticHexPrismGrid();
        hexPrism.setTexture( tex3D );
        var hex = new StaticHexGrid();
        hex.setTexture( tex2D );
        var rectangle = new StaticRectangleGrid();
        rectangle.setTexture( tex2D );
        Supplier< StaticPlane3D > plane3DSupplier = () -> {
            var plane3D = new StaticPlane3D();
            plane3D.setTexture( tex2D );
            return plane3D;
        };
        Supplier< StaticAutoRotablePlane3D > autoRotablePlane3DSupplier = () -> {
            var plane3D = new StaticAutoRotablePlane3D();
            plane3D.setTexture( tex2D );
            return plane3D;
        };
        
        new Thread( () -> display( "Rectangle", rectangle, null, null ) ).start();
        Thread.sleep( 500 );
        new Thread( () -> display( "Hexagon", hex, null, null ) ).start();
        Thread.sleep( 500 );
        new Thread( () -> display( "Cube", null, cube, null ) ).start();
        Thread.sleep( 500 );
        new Thread( () -> display( "HexPrism", null, hexPrism, null ) ).start();
        Thread.sleep( 500 );
        new Thread( () -> display( "Plane3D", null, null, plane3DSupplier ) ).start();
        Thread.sleep( 500 );
        new Thread( () -> display( "Auto rotable Plane3D", null, null, autoRotablePlane3DSupplier ) ).start();
    }
    
    public static void display(
        String name,
        Terrain2D< ?, ? > terrain2D,
        Terrain3D< ?, ? > terrain3D,
        Supplier< ? extends SimplePlane3D > object3DSupplier
    ) {
        var width = 600;
        var height = 600;
        var settings = WindowSettings.builder().setVerticalSync( false )
            .setTitle( name )
            .setResizeable( false )
            .setWindowMode( WindowMode.BORDERLESS )
            .setWidth( width )
            .setHeight( height )
            .build();
        
        var window = WindowBuilder.registrableWindow( settings ).build();
        
        if( terrain2D != null ) {
            Terrain2D< ?, ? >[][] terrain2DS = new Terrain2D[10][10];
            IntStream.range( 0, 10 ).forEach( x -> IntStream.range( 0, 10 ).forEach( y -> terrain2DS[x][y] = terrain2D ) );
            window.createNewScene2D( 0, Index2D.id2( 10, 10 ), FirstOracleConstants.INDEX_ZERO_2I );
            window.registerMultipleTerrains2D( 0, terrain2DS );
        }
        if( terrain3D != null ) {
            Terrain3D< ?, ? >[][][] terrain3DS = new Terrain3D[10][1][10];
            IntStream.range( 0, 10 ).forEach( x -> IntStream.range( 0, 10 ).forEach( z -> terrain3DS[x][0][z] = terrain3D ) );
            window.createNewScene3D( 0, Index3D.id3( 10,1, 10 ), FirstOracleConstants.INDEX_ZERO_3I );
            window.registerMultipleTerrains3D( 0, terrain3DS );
        }
        if( object3DSupplier != null ) {
            var list = new ArrayList< PositionableObject3D< ?, ? > >();
            IntStream.range( 0, 10 ).forEach( x -> IntStream.range( 0, 10 ).forEach( z -> {
                var object = object3DSupplier.get();
                object.setPosition( Position3D.pos3( 2*x, 0, 2*z ) );
                list.add( object );
            } ) );
            window.createNewScene3D( 0, FirstOracleConstants.INDEX_ZERO_3I, FirstOracleConstants.INDEX_ZERO_3I );
            window.registerMultipleObjects3D( 0, list );
        }
        var cameraController = CameraController.createAndStart( window, settings, CameraKeyMap.getFunctionalKeyLayout(), 10, 15f );
        
        window.setBackgroundColour( 0, FirstOracleConstants.WHITE );
        cameraController.setPos2D( -8, -8 );
        cameraController.setPos3D( 7, 0, 10 );
        cameraController.setCameraSize( 15 );
    
        window.setScene2DCamera( 0, cameraController.getCamera2D() );
        window.setScene3DCamera( 0, cameraController.getCamera3D() );
        
        window.run();
    }
}
