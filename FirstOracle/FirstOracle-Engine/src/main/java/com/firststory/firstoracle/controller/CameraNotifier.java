/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

import java.util.Collection;

public interface CameraNotifier {
    
    default void addCameraObserver( CameraListener observer ) {
        getCameraObservers().add( observer );
    }
    
    Collection< CameraListener > getCameraObservers();
    
    default void removeCameraObserver( CameraListener observer ) {
        getCameraObservers().remove( observer );
    }
    
    default void removeAllCameraObservers() {
        getCameraObservers().clear();
    }
    
    default void notifyCameraListeners( CameraEvent event ) {
        for ( CameraListener observer : getCameraObservers() ) {
            observer.notify( event, this );
        }
    }
}
