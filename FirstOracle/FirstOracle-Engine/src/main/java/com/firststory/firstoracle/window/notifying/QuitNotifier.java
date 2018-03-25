/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
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
        getQuitListeners().parallelStream()
            .map(listener -> listener.notify( event, this ))
            .forEach( threads -> threads.forEach( t -> {
                try{ t.wait(); } catch(Exception ignored ){}
            } ) );
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
