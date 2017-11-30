/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface TimeNotifier {
    default void addTimeObserver( TimeListener observer ) {
        getTimeObservers().add( observer );
    }
    
    Collection< TimeListener > getTimeObservers();
    
    default void removeTimeObserver( TimeListener observer ) {
        getTimeObservers().remove( observer );
    }
    
    default void removeAllTimeObservers( TimeListener observer ) {
        getTimeObservers().clear();
    }
    
    default void notifyTimeObservers( double newTime ) {
        for ( TimeListener observer : getTimeObservers() ) {
            observer.notify( newTime, this );
        }
    }
}
