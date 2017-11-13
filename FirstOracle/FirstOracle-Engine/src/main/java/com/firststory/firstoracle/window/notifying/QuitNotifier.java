/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface QuitNotifier {

    default void notifyQuitListeners() {
        notifyQuitListeners( new QuitEvent() );
    }

    Collection< QuitListener > getQuitListeners();

    default void addQuitObserver( QuitListener observer ) {
        getQuitListeners().add( observer );
    }

    default void removeQuitObserver( QuitListener observer ) {
        getQuitListeners().remove( observer );
    }

    default void removeAllQuitListeners() {
        getQuitListeners().clear();
    }

    default void notifyQuitListeners( QuitEvent event ) {
        for ( QuitListener observer : getQuitListeners() ) {
            observer.notify( event, this );
        }
    }
}
