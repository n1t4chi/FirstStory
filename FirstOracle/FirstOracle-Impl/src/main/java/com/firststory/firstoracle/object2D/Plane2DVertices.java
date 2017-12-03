/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import java.util.HashMap;

/**
 * @author n1t4chi
 */
public class Plane2DVertices extends FramelessVertices2D {
    
    private static final Plane2DVertices Plane2DVertices = new Plane2DVertices();
    private static final HashMap< Touple, Plane2DVertices > instances = new HashMap<>( 2 );
    
    /**
     * Returns default [-1,1]x[-1,1] instance of Plane2DVertices.
     *
     * @return Plane2DVertices instance
     */
    public static Plane2DVertices getPlane2DVertices() {
        return Plane2DVertices;
    }
    
    /**
     * Returns instance of Plane2DVertices.
     *
     * @return Plane2DVertices instance
     */
    public static Plane2DVertices getPlane2DVertices( float minX, float maxX, float minY, float maxY ) {
        Touple touple = new Touple( minX, maxX, minY, maxY );
        if ( instances.containsKey( touple ) ) {
            return instances.get( touple );
        } else {
            Plane2DVertices instance = new Plane2DVertices( minX, maxX, minY, maxY );
            instances.put( touple, instance );
            return instance;
        }
    }
    
    private static float[] createPlane2DVerticesArray() {
        return createPlane2DVerticesArray( -1, 1, -1, 1 );
    }
    
    private static float[] createPlane2DVerticesArray( float minX, float maxX, float minY, float maxY ) {
        float[] pointData = {
        /*0*/ minX, minY,
        /*1*/ maxX, minY,
        /*2*/ maxX, maxY,
        /*3*/ minX, maxY
        };
        
        short[] points = {
            0, 1, 2, 0, 2, 3
        };
        
        float[] rtrn = new float[points.length * 2];
        
        for ( int j = 0; j < points.length; j++ ) {
            rtrn[j * 2] = pointData[2 * points[j]];
            rtrn[j * 2 + 1] = pointData[2 * points[j] + 1];
        }
        return rtrn;
    }
    
    private Plane2DVertices( float minX, float maxX, float minY, float maxY ) {
        super( createPlane2DVerticesArray( minX, maxX, minY, maxY ) );
    }
    
    private Plane2DVertices() {
        super( createPlane2DVerticesArray() );
    }
    
    private static class Touple {
        
        private float minX, maxX, minY, maxY;
        
        private Touple( float minX, float maxX, float minY, float maxY ) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
        
        @Override
        public int hashCode() {
            int result = ( minX != +0.0f ? Float.floatToIntBits( minX ) : 0 );
            result = 31 * result + ( maxX != +0.0f ? Float.floatToIntBits( maxX ) : 0 );
            result = 31 * result + ( minY != +0.0f ? Float.floatToIntBits( minY ) : 0 );
            result = 31 * result + ( maxY != +0.0f ? Float.floatToIntBits( maxY ) : 0 );
            return result;
        }
        
        @Override
        public boolean equals( Object o ) {
            if ( this == o ) { return true; }
            if ( o == null || getClass() != o.getClass() ) { return false; }
            
            final Touple touple = ( Touple ) o;
            
            return Float.compare( touple.minX, minX ) == 0 && Float.compare( touple.maxX, maxX ) == 0 &&
                   Float.compare( touple.minY, minY ) == 0 && Float.compare( touple.maxY, maxY ) == 0;
        }
    }
}
