/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.object.data.UV;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.*;
import static com.firststory.firstoracle.object.data.UV.uv;

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
        var hor = 1 / 8f;
        //lower a bit UV map so pixels from other face will not be taken into consideration.
        var vertUp = ( frame ) / ( float ) rows + UV_DELTA;
        var vertDown = ( frame + 1 ) / ( float ) rows - UV_DELTA;
    
        var vertMiddle = ( frame + 0.5f ) / ( float ) rows;
        var vertMidUp = ( frame + 0.5f - SQRT3_DIV2 / 2 ) / rows + UV_DELTA;
        var vertMidDown = ( frame + 0.5f + SQRT3_DIV2 / 2 ) / rows - UV_DELTA;
        
        return Arrays.asList(
            uv( 0.5f * hor, vertMiddle ),
            uv( 0.75f * hor - UV_DELTA, vertUp ),
            uv( 0.25f * hor + UV_DELTA, vertUp ),
            uv( 0.5f * hor, vertMiddle ),
            uv( 0.25f * hor + UV_DELTA, vertUp ),
            uv( 0 * hor + UV_DELTA, vertMiddle ),
            uv( 0.5f * hor, vertMiddle ),

            uv( 0 * hor + UV_DELTA, vertMiddle ),
            uv( 0.25f * hor + UV_DELTA, vertDown ),
            uv( 0.5f * hor, vertMiddle ),

            uv( 0.25f * hor + UV_DELTA, vertDown ),
            uv( 0.75f * hor - UV_DELTA, vertDown ),
            uv( 0.5f * hor, vertMiddle ),

            uv( 0.75f * hor - UV_DELTA, vertDown ),
            uv( 1 * hor - UV_DELTA, vertMiddle ),
            uv( 0.5f * hor, vertMiddle ),

            uv( 1 * hor - UV_DELTA, vertMiddle ),
            uv( 0.75f * hor - UV_DELTA, vertUp )
            
        );
    }
    
    private Hex2DUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }
}
