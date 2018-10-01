/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.Colour;

import java.util.List;

/**
 * @author n1t4chi
 */
public class Colouring extends VertexAttribute< Colour > {
    
    private final List< Colour > colour;
    
    public Colouring( List< Colour > colour ) {
        this.colour = colour;
    }
    
    @Override
    protected List< Colour > getData( int... parameters ) {
        return colour;
    }
    
    @Override
    protected long getKey( int... parameters ) {
        return 0;
    }
    
    @Override
    public String toString() {
        return "Colouring:{" + colour + '}';
    }
}
