/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.object;

import java.util.List;

/**
 * @author n1t4chi
 */
public class Colouring extends VertexAttribute< VertexColour > {
    
    private final List< VertexColour > colour;
    
    public Colouring( List< VertexColour > colour ) {
        this.colour = colour;
    }
    
    @Override
    protected List< VertexColour > getData( int... parameters ) {
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
