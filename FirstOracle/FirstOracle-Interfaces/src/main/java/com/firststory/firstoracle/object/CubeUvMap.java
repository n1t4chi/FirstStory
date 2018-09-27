/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.UV_DELTA;
import static com.firststory.firstoracle.FirstOracleConstants.array;
import static com.firststory.firstoracle.object.UV.uv;

/**
 * @author n1t4chi
 */
public class CubeUvMap extends UvMap {

    private static final HashMap< Long, CubeUvMap > instances = new HashMap<>( 5 );

    public static CubeUvMap getCubeUvMap() {
        return getCubeUvMap( 1, 1 );
    }

    /**
     * Returns UV map specifically designed for cube objects
     *
     * @param frames How many frames will be in texture
     * @param rows   How many rows for frames will be in texture
     *
     * @return UV map instance
     */
    public static CubeUvMap getCubeUvMap( int frames, int rows ) {
        var hash = hash( frames, rows );
        return instances.computeIfAbsent( hash, k -> new CubeUvMap( frames, rows ) );
    }

    private static long hash( int frames, int rows ) {
        return ( ( long ) rows << 32 ) + frames;
    }
    
    private static List<UV>[][] getUvMap( int frames, int rows ) {
        List<UV>[][] map = array( 1 , frames );

        for ( var frame = 0; frame < frames; frame++ ) {
            map[ 0 ][ frame ] = createUvMapCube( frame, frames, rows );
        }
        return map;
    }
    
    private static List<UV> createUvMapCube( int frame, int frames, int rows ) {
        if ( frame < 0 || frame >= frames || frames > rows ) {
            throw new IllegalArgumentException(
                "Illegal frame:" + frame + " for frames:" + frames + ", rows:" + rows + "." );
        }
        var hor = 1 / 8f;
        var vertUp = ( frame ) / ( float ) rows + UV_DELTA;
        var vertDown = ( frame + 1 ) / ( float ) rows - UV_DELTA;

        return Arrays.asList( 
            uv( 0 * hor + UV_DELTA, vertDown ),
            uv( 1 * hor - UV_DELTA, vertDown ),
            uv( 1 * hor - UV_DELTA, vertUp ),
            uv( 0 * hor + UV_DELTA, vertDown ),
            uv( 1 * hor - UV_DELTA, vertUp ),
            uv( 0 * hor + UV_DELTA, vertUp ),
            
            //face 1
            uv( 1 * hor + UV_DELTA, vertDown ),
            uv( 2 * hor - UV_DELTA, vertDown ),
            uv( 2 * hor - UV_DELTA, vertUp ),
            uv( 1 * hor + UV_DELTA, vertDown ),
            uv( 2 * hor - UV_DELTA, vertUp ),
            uv( 1 * hor + UV_DELTA, vertUp ),
            
    
            //face 2
            uv( 2 * hor + UV_DELTA, vertDown ),
            uv( 3 * hor - UV_DELTA, vertDown ),
            uv( 3 * hor - UV_DELTA, vertUp ),
            uv( 2 * hor + UV_DELTA, vertDown ),
            uv( 3 * hor - UV_DELTA, vertUp ),
            uv( 2 * hor + UV_DELTA, vertUp ),
            
    
            //face 3
            uv( 3 * hor + UV_DELTA, vertDown ),
            uv( 4 * hor - UV_DELTA, vertDown ),
            uv( 4 * hor - UV_DELTA, vertUp ),
            uv( 3 * hor + UV_DELTA, vertDown ),
            uv( 4 * hor - UV_DELTA, vertUp ),
            uv( 3 * hor + UV_DELTA, vertUp ),
            
    
            //face 4
            uv( 4 * hor + UV_DELTA, vertDown ),
            uv( 5 * hor - UV_DELTA, vertDown ),
            uv( 5 * hor - UV_DELTA, vertUp ),
            uv( 4 * hor + UV_DELTA, vertDown ),
            uv( 5 * hor - UV_DELTA, vertUp ),
            uv( 4 * hor + UV_DELTA, vertUp ),
            
    
            //face 5
            uv( 5 * hor + UV_DELTA, vertDown ),
            uv( 6 * hor - UV_DELTA, vertDown ),
            uv( 6 * hor - UV_DELTA, vertUp ),
            uv( 5 * hor + UV_DELTA, vertDown ),
            uv( 6 * hor - UV_DELTA, vertUp ),
            uv( 5 * hor + UV_DELTA, vertUp )
        );
    }

    private CubeUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }

}
