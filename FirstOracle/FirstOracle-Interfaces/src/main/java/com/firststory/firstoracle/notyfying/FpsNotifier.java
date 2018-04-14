/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.notyfying;

import java.util.Collection;

public /**
 * @author n1t4chi
 */
interface FpsNotifier {
    
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
