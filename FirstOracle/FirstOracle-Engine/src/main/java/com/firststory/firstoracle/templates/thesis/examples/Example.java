/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.thesis.examples;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.data.*;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.*;
import com.firststory.firstoracle.window.WindowBuilder;

/**
 * @author n1t4chi
 */
public class Example {
    public static void main( String[] args ) throws Exception {
        var settings = WindowSettings.builder()
            .setTitle( "Example Application" )
            .setWidth( 500 )
            .setHeight( 500 )
            .build()
        ;
        var window = WindowBuilder.registrableWindow( settings ).build();
        window.createNewScene2D(
            0,
            FirstOracleConstants.INDEX_ONE_2I,
            FirstOracleConstants.INDEX_ZERO_2I
        );
        
        var rectangleGrid = new StaticRectangleGrid();
        var textureRect = Texture.create(
            "resources\\First Oracle\\textures\\grass2D.png"
        );
        rectangleGrid.setTexture( textureRect );
        window.registerTerrain2D( 0, rectangleGrid, 0, 0 );
        
        var hex = new StaticHexagon();
        var textureHex = Texture.create(
            "resources\\First Oracle\\textures\\2D.png"
        );
        hex.setTexture( textureHex );
        window.registerObject2D( 0, hex );
    
        hex.setPosition( Position2D.pos2( 2, 2 ) );
        var controller = CameraController.createAndStart(
            window,
            settings
        );
        window.setScene2DCamera( 0, controller.getCamera2D() );
        window.setBackgroundColour( 0, Colour.col( 0.5f, 0.5f, 1, 1 ) );
        
        window.run();
    }
}
