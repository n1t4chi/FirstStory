/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.data.Position2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.firststory.firstoracle.data.Position2D.pos2;

/**
 * @author n1t4chi
 */
public class Plane2DVertices extends FramelessVertices2D {
    
    private static final Plane2DVertices Plane2DVertices = new Plane2DVertices();
    private static final Plane2DVertices AbsolutePlane2DVertices = new Plane2DVertices(0, 1, 0, 1);
    private static final HashMap< Tuple, Plane2DVertices > instances = new HashMap<>( 2 );
    
    /**
     * Returns default [-1,1]x[-1,1] instance of Plane2DVertices.
     *
     * @return Plane2DVertices instance
     */
    public static Plane2DVertices getPlane2DVertices() {
        return Plane2DVertices;
    }
    
    public static Plane2DVertices getAbsolutePlane2DVertices() {
        return AbsolutePlane2DVertices;
    }
    
    public static Plane2DVertices getPlane2DVertices( float minX, float maxX, float minY, float maxY ) {
        var tuple = new Tuple( minX, maxX, minY, maxY );
        return instances.computeIfAbsent( tuple, tuple1 -> {
            var instance = new Plane2DVertices( minX, maxX, minY, maxY );
            instances.put( tuple, instance );
            return instance;
        } );
    }
    
    private static List< Position2D > createPlane2DVerticesArray() {
        return createPlane2DVerticesArray( -1, 1, -1, 1 );
    }
    
    private static List< Position2D > createPlane2DVerticesArray( float minX, float maxX, float minY, float maxY ) {
        Position2D[] pointData = {
        /*0*/ pos2( minX, minY ),
        /*1*/ pos2( maxX, minY ),
        /*2*/ pos2( maxX, maxY ),
        /*3*/ pos2( minX, maxY )
        };
        
        short[] points = {
            0, 1, 2, 0, 2, 3
        };
    
        List< Position2D > list = new ArrayList<>( points.length );
    
        for ( var point : points ) {
            list.add( pointData[ point ] );
        }
        return list;
    }
    
    private Plane2DVertices( float minX, float maxX, float minY, float maxY ) {
        super( createPlane2DVerticesArray( minX, maxX, minY, maxY ) );
    }
    
    private Plane2DVertices() {
        super( createPlane2DVerticesArray() );
    }
    
    private static class Tuple {
    
        private final float minX;
        private final float maxX;
        private final float minY;
        private final float maxY;
        
        private Tuple( float minX, float maxX, float minY, float maxY ) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
        
        @Override
        public int hashCode() {
            var result = ( minX != +0.0f ? Float.floatToIntBits( minX ) : 0 );
            result = 31 * result + ( maxX != +0.0f ? Float.floatToIntBits( maxX ) : 0 );
            result = 31 * result + ( minY != +0.0f ? Float.floatToIntBits( minY ) : 0 );
            result = 31 * result + ( maxY != +0.0f ? Float.floatToIntBits( maxY ) : 0 );
            return result;
        }
        
        @Override
        public boolean equals( Object o ) {
            if ( this == o ) { return true; }
            if ( o == null || getClass() != o.getClass() ) { return false; }
    
            var tuple = ( Tuple ) o;
            
            return Float.compare( tuple.minX, minX ) == 0 && Float.compare( tuple.maxX, maxX ) == 0 &&
                   Float.compare( tuple.minY, minY ) == 0 && Float.compare( tuple.maxY, maxY ) == 0;
        }
    }
}
