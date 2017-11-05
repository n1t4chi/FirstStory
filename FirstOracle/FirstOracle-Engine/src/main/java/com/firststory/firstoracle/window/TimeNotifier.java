/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import java.util.Collection;

/**
 * @author: n1t4chi
 */
public interface TimeNotifier {
    Collection<TimeObserver> getObservers();
    default void addObserver(TimeObserver observer){
        getObservers().add( observer );
    }
    default void removeObserver(TimeObserver observer){
        getObservers().remove( observer );
    }
    default void removeAllObservers(TimeObserver observer){
        getObservers().clear();
    }
    default void notifyObservers(double newTime){
        for(TimeObserver observer : getObservers()){
            observer.notify( newTime, this );
        }
    }
}
