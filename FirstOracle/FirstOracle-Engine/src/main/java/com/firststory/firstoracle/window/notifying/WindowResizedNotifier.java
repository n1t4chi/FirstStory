/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author: n1t4chi
 */
public interface WindowResizedNotifier {

    default void notifySizeListeners( int newWidth, int newHeight ) {
        for ( WindowResizedListener observer : getSizeObservers() ) {
            observer.notify( newWidth, newHeight, this );
        }
    }

    Collection< WindowResizedListener > getSizeObservers();

    default void addSizeObserver( WindowResizedListener observer ) {
        getSizeObservers().add( observer );
    }

    default void removeSizeObserver( WindowResizedListener observer ) {
        getSizeObservers().remove( observer );
    }

    default void removeAllSizeObservers() {
        getSizeObservers().clear();
    }
}
