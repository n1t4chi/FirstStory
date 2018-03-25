/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public class DefaultDirectionController implements DirectionController {
    
    private final int directions;
    
    public DefaultDirectionController( int directions ) {
        this.directions = directions;
    }
    
    @Override
    public int getCurrentDirection( double currentCameraRotation ) {
        currentCameraRotation += ( 360.0 / directions / 2.0 );
        currentCameraRotation %= 360;
        currentCameraRotation *= directions;
        currentCameraRotation /= 360.0;
        return ( int ) currentCameraRotation;
    }
}
