/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

import java.util.Collection;

public interface CameraNotifier {
    
    default void addCameraListener( CameraListener listener ) {
        getCameraListeners().add( listener );
    }
    
    Collection< CameraListener > getCameraListeners();
    
    default void removeCameraListener( CameraListener listener ) {
        getCameraListeners().remove( listener );
    }
    
    default void removeAllCameraListeners() {
        getCameraListeners().clear();
    }
    
    default void notifyCameraListeners( CameraEvent event ) {
        for ( CameraListener listener : getCameraListeners() ) {
            listener.notify( event, this );
        }
    }
}
