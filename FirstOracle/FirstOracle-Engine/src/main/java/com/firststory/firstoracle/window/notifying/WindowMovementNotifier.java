/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface WindowMovementNotifier {
    
    default void notifyMovementObservers( int newX, int newY ) {
        for ( WindowMovementListener observer : getMovementObservers() ) {
            observer.notify( newX, newY, this );
        }
    }
    
    Collection< WindowMovementListener > getMovementObservers();
    
    default void addMovementObserver( WindowMovementListener observer ) {
        getMovementObservers().add( observer );
    }
    
    default void removeMovementObserver( WindowMovementListener observer ) {
        getMovementObservers().remove( observer );
    }
    
    default void removeAllMovementObservers() {
        getMovementObservers().clear();
    }
}
