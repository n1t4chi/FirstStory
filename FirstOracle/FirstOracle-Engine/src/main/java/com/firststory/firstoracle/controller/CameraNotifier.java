/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

import java.util.Collection;

/**
 * @author n1t4chi
 */
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
        for ( var listener : getCameraListeners() ) {
            listener.notify( event, this );
        }
    }
}
