/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import java.util.Collection;

/**
 * @author: n1t4chi
 */
public interface FpsNotifier {
    Collection<FpsObserver> getObservers();
    default void addObserver(FpsObserver observer){
        getObservers().add( observer );
    }
    default void removeObserver(FpsObserver observer){
        getObservers().remove( observer );
    }
    default void removeAllObservers(FpsObserver observer){
        getObservers().clear();
    }
    default void notifyObservers(int newFps){
        for(FpsObserver observer : getObservers()){
            observer.notify( newFps, this );
        }
    }
}
