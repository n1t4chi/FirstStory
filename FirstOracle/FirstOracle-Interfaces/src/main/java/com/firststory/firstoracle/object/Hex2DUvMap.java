/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.UV;

import java.util.*;

import static com.firststory.firstoracle.FirstOracleConstants.*;
import static com.firststory.firstoracle.data.UV.uv;

/**
 * @author n1t4chi
 */
public class Hex2DUvMap extends UvMap {
    private static final HashMap< Long, Hex2DUvMap > instances = new HashMap<>( 5 );
    
    public static Hex2DUvMap getHex2DUvMap() {
        return getHex2DUvMap( 1, 1 );
    }
    
    /**
     * Returns UV map specifically designed for cube objects
     *
     * @param frames How many frames will be in texture
     * @param rows   How many rows for frames will be in texture
     *
     * @return UV map instance
     */
    public static Hex2DUvMap getHex2DUvMap( int frames, int rows ) {
        var hash = hash( frames, rows );
        return instances.computeIfAbsent( hash, k -> new Hex2DUvMap( frames, rows ) );
    }
    
    private static long hash( int frames, int rows ) {
        return ( ( long ) rows << 32 ) + frames;
    }
    
    private static List< UV >[][] getUvMap( int frames, int rows ) {
        List< UV >[][] map = array( 1 , frames );
        
        for ( var frame = 0; frame < frames; frame++ ) {
            map[ 0 ][ frame ] = createUvMapHex2D( frame, frames, rows );
        }
        return map;
    }
    
    private static List< UV > createUvMapHex2D( int frame, int frames, int rows ) {
        if ( frame < 0 || frame >= frames || frames > rows ) {
            throw new IllegalArgumentException(
                "Illegal frame:" + frame + " for frames:" + frames + ", rows:" + rows + "." );
        }
        //lower a bit UV map so pixels from other face will not be taken into consideration.
        var vertUp = ( frame ) / ( float ) rows + UV_DELTA;
        var vertDown = ( frame + 1 ) / ( float ) rows - UV_DELTA;
    
        var vertMiddle = ( frame + 0.5f ) / ( float ) rows;
        //     4
        //  3     5
        //  2     6
        //     1
        return Arrays.asList(
            //1
            uv( 0.5f, vertMiddle ),
            uv( 0.75f - UV_DELTA, vertDown ),
            uv( 0.25f + UV_DELTA, vertDown ),
    
            //2
            uv( 0.5f, vertMiddle ),
            uv( 0.25f + UV_DELTA, vertDown ),
            uv( 0 + UV_DELTA, vertMiddle ),

            //3
            uv( 0.5f, vertMiddle ),
            uv( 0 + UV_DELTA, vertMiddle ),
            uv( 0.25f + UV_DELTA, vertUp ),
            
            //4
            uv( 0.5f, vertMiddle ),
            uv( 0.25f + UV_DELTA, vertUp ),
            uv( 0.75f - UV_DELTA, vertUp ),
            
            //5
            uv( 0.5f, vertMiddle ),
            uv( 0.75f - UV_DELTA, vertUp ),
            uv( 1 - UV_DELTA, vertMiddle ),
            
            //6
            uv( 0.5f, vertMiddle ),
            uv( 1 - UV_DELTA, vertMiddle ),
            uv( 0.75f - UV_DELTA, vertDown ),
            
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 ),
            uv( 0, 0 ), uv( 0, 0 ), uv( 0, 0 )
        );
    }
    
    private Hex2DUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }
}
