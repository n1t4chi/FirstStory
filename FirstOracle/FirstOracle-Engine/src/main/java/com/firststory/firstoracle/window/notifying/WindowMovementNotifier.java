/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.notifying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface WindowMovementNotifier {
    
    default void notifyMovementListeners( int newX, int newY ) {
        NotyfyingEngine.notify(
            getMovementListeners(), ( listener ) -> listener.notify( newX, newY, this ) );
    }
    
    Collection< WindowMovementListener > getMovementListeners();
    
    default void addMovementListener( WindowMovementListener listener ) {
        getMovementListeners().add( listener );
    }
    
    default void removeMovementListener( WindowMovementListener listener ) {
        getMovementListeners().remove( listener );
    }
    
    default void removeAllMovementListeners() {
        getMovementListeners().clear();
    }
}
