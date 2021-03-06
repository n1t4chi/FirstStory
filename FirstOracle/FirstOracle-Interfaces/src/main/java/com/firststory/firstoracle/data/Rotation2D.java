/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

/**
 * @author n1t4chi
 */
public class Rotation2D extends Rotation {
    
    public static Rotation2D rot2( float angle ) {
        return new Rotation2D( angle );
    }
    
    private Rotation2D( float angle ) {
        super( 0, 0, angle );
    }
    
    @Override
    public String toString() {
        return "Rotation2D{ " + oz() + " }";
    }
    
    public Float toVec1f() {
        return oz();
    }
}
