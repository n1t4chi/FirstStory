/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates;

public class DualWindow {
    
    //does not work for now
    public static void main( String[] args ) throws Exception {
        Thread t = new Thread( DualWindow::secondWindow );
        t.start();
        t.join();
        secondWindow();
        //FullApplication3D.main( args );
        t.join();
    }
    
    private static void secondWindow() {
        try{
            OpenGlAndGlfwApplication2D.main( new String[]{} );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
}
