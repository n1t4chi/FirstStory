/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

public interface FpsNotifier {
    
    default void addFpsListener( FpsListener listener ) {
        getFpsListeners().add( listener );
    }
    
    Collection< FpsListener > getFpsListeners();
    
    default void removeFpsListener( FpsListener listener ) {
        getFpsListeners().remove( listener );
    }
    
    default void removeAllFpsListeners() {
        getFpsListeners().clear();
    }
    
    default void notifyFpsListeners( int newFps ) {
        NotyfyingEngine.notify(
            getFpsListeners(), ( listener ) -> listener.notify( newFps, this ) );
    }
}
