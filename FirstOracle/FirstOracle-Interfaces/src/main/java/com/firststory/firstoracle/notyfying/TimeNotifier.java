/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.notyfying;

import java.util.Collection;

/**
 * @author n1t4chi
 */
public interface TimeNotifier {
    default void addTimeListener( TimeListener listener ) {
        getTimeListeners().add( listener );
    }
    
    Collection< TimeListener > getTimeListeners();
    
    default void removeTimeListener( TimeListener listener ) {
        getTimeListeners().remove( listener );
    }
    
    default void removeAllTimeListener( TimeListener listener ) {
        getTimeListeners().clear();
    }
    
    default void notifyTimeListener( double newTime ) {
        NotifyingEngine.notify(
            getTimeListeners(), ( listener ) -> listener.notify( newTime, this ) );
    }
}
