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
    private LineType type;
    
    public static LineData lineLoop( float width, Vector4fc colour ) {
        return new LineData( width, colour, LineType.LINE_LOOP );
    }
    
    public static LineData lineLoop( float width, float red, float green, float blue, float alpha ) {
        return new LineData( width, red, green, blue, alpha, LineType.LINE_LOOP );
    }
    
    public static LineData lines( float width, Vector4fc colour ) {
        return new LineData( width, colour, LineType.LINES );
    }
    
    public static LineData lines( float width, float red, float green, float blue, float alpha ) {
        return new LineData( width, red, green, blue, alpha, LineType.LINES );
    }
    
    private LineData( float width, Vector4fc colour, LineType type ) {
        this.width = width;
        this.colour = colour;
        this.type = type;
    }
    
    private LineData( float width, float red, float green, float blue, float alpha, LineType type ) {
        this( width, new Vector4f( red, green, blue, alpha ), type );
    }
    
    public float getWidth() {
        return width;
    }
    
    public Vector4fc getColour() {
        return colour;
    }
    
    public LineType getType() {
        return type;
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
