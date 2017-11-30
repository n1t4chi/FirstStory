/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

public interface FpsNotifier {
    
    default void addFpsObserver( FpsListener observer ) {
        getFpsObservers().add( observer );
    }
    
    Collection< FpsListener > getFpsObservers();
    
    default void removeFpsObserver( FpsListener observer ) {
        getFpsObservers().remove( observer );
    }
    
    default void removeAllFpsObservers() {
        getFpsObservers().clear();
    }
    
    default void notifyFpsListeners( int newFps ) {
        for ( FpsListener observer : getFpsObservers() ) {
            observer.notify( newFps, this );
        }
    }
}
