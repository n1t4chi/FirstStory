/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.input.Key;
import com.firststory.firstoracle.scene.RenderableScene;
import com.firststory.firstoracle.window.Window;

/**
 * @author n1t4chi
 */
public interface ControllableScene extends RenderableScene {
    
    void initialise( Window window );
    
    default void click( double x, double y ) {}
    
    default void scroll( double dx ) {}
    
    default void keyPress( Key key ) {}
    
    default void windowSize( int width, int height ) {}
    
    default void currentTime( double newTimeSnapshot ) {}
    
    default void keyRelease( Key key ) {}
}
