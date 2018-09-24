/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates;

/**
 * @author n1t4chi
 */
public class DualWindow {
    
    public static void main( String[] args ) throws Exception {
        Thread t = new Thread( DualWindow::secondWindow );
        t.start();
        firstWindow();
        t.join();
    }
    
    private static void firstWindow() {
        try{
            GlfwApplication3D.main( new String[]{} );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    private static void secondWindow() {
        try{
            GlfwApplication2D.main( new String[]{} );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
}
