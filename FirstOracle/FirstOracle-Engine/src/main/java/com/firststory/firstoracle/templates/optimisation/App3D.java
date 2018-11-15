/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.templates.IOUtilities;

import java.io.IOException;

/**
 * @author n1t4chi
 */
public class App3D {
    
    public static void main( String[] args ) throws IOException {
        Utils.getApp3D( IOUtilities.readTextResource( "resources/First Oracle/scene.json" ) ).run();
    }
}
