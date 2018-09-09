/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public class LineData {
    
    private final float width;
    private final Vector4fc colour;
    
    public LineData( float width, Vector4fc colour ) {
        this.width = width;
        this.colour = colour;
    }
    
    public LineData( float width, float red, float green, float blue, float alpha ) {
        this.width = width;
        this.colour = new Vector4f( red, green, blue, alpha );
    }
    
    public float getWidth() {
        return width;
    }
    
    public Vector4fc getColour() {
        return colour;
    }
    
    @Override
    public int hashCode() {
        int result = ( width != +0.0f ? Float.floatToIntBits( width ) : 0 );
        result = 31 * result + ( colour != null ? colour.hashCode() : 0 );
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
        
        LineData lineData = ( LineData ) o;
        
        if ( Float.compare( lineData.width, width ) != 0 ) { return false; }
        return colour != null ? colour.equals( lineData.colour ) : lineData.colour == null;
    }
}
