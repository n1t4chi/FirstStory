/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface WindowResizedNotifier {
    
    default void notifySizeListeners( int newWidth, int newHeight ) {
        NotyfyingEngine.notify(
            getSizeListeners(), ( listener ) -> listener.notify( newWidth, newHeight, this ) );
    }
    
    Collection< WindowResizedListener > getSizeListeners();
    
    default void addSizeListener( WindowResizedListener listener ) {
        getSizeListeners().add( listener );
    }
    
    default void removeSizeListener( WindowResizedListener listener ) {
        getSizeListeners().remove( listener );
    }
    
    default void removeAllSizeListeners() {
        getSizeListeners().clear();
    }
}
