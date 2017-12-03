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
    
    default void notifyQuitListeners( QuitEvent event ) {
        for ( QuitListener listener : getQuitListeners() ) {
            listener.notify( event, this );
        }
    }
    
    Collection< QuitListener > getQuitListeners();
    
    default void addQuitListener( QuitListener listener ) {
        getQuitListeners().add( listener );
    }
    
    default void removeQuitListener( QuitListener listener ) {
        getQuitListeners().remove( listener );
    }
    
    default void removeAllQuitListeners() {
        getQuitListeners().clear();
    }
}
