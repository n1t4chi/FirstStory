/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.window.*;

/**
 * @author n1t4chi
 */
public class Main {
    
    public static void main( String[] args ) {
        var settings = WindowSettings.builder().setResizeable( true ).build();
        var provider = new SceneController();
        var window = WindowBuilder.simpleWindow( settings, provider ).build();
        provider.initialise( window );
        window.run();
    }
    
}
