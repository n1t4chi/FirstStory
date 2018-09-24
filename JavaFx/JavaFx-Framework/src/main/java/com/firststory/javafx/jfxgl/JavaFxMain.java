/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.javafx.jfxgl;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.Runner;
import cuchaz.jfxgl.JFXGLLauncher;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class JavaFxMain {
    private static Logger logger = FirstOracleConstants.getLogger( JavaFxMain.class );
    
    public static void main( String[] args ) {
        logger.fine( "Starting JFXGL main with arguments: " + Arrays.toString( args ) );
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( JavaFxMain.class, args );
    }
    public static void jfxglmain( String[] args ) {
        Runner.main( args );
    }
    
    
}
