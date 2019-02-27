/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import java.util.Objects;

import static com.firststory.firstoracle.data.Colour.col;

/**
 * @author n1t4chi
 */
public class LineData {
    
    private final float width;
    private final Colour colour;
    private final LineType type;
    
    public static LineData lineLoop( float width, Colour colour ) {
        return new LineData( width, colour, LineType.LINE_LOOP );
    }
    
    public static LineData lineLoop( float width, float red, float green, float blue, float alpha ) {
        return new LineData( width, red, green, blue, alpha, LineType.LINE_LOOP );
    }
    
    public static LineData lines( float width, Colour colour ) {
        return new LineData( width, colour, LineType.LINES );
    }
    
    public static LineData lines( float width, float red, float green, float blue, float alpha ) {
        return new LineData( width, red, green, blue, alpha, LineType.LINES );
    }
    
    private LineData( float width, Colour colour, LineType type ) {
        this.width = width;
        this.colour = colour;
        this.type = type;
    }
    
    private LineData( float width, float red, float green, float blue, float alpha, LineType type ) {
        this( width, col( red, green, blue, alpha ), type );
    }
    
    public float getWidth() {
        return width;
    }
    
    public Colour getColour() {
        return colour;
    }
    
    public LineType getType() {
        return type;
    }
    
    @Override
    public int hashCode() {
        var result = ( width != +0.0f ? Float.floatToIntBits( width ) : 0 );
        result = 31 * result + ( colour != null ? colour.hashCode() : 0 );
        return result;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }
    
        var lineData = ( LineData ) o;
        
        if ( Float.compare( lineData.width, width ) != 0 ) { return false; }
        return Objects.equals( colour, lineData.colour );
    }
}
