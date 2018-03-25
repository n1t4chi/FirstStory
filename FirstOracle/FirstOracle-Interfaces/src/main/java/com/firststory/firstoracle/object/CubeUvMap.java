/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.HashMap;

import static com.firststory.firstoracle.FirstOracleConstants.UV_DELTA;

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
        long hash = hash( frames, rows );
        return instances.computeIfAbsent( hash, k -> new CubeUvMap( frames, rows ) );
    }

    private static long hash( int frames, int rows ) {
        return ( ( long ) rows << 32 ) + frames;
    }

    private static float[][][] getUvMap( int frames, int rows ) {
        float[][][] map = new float[ 1 ][ frames ][];

        for ( int frame = 0; frame < frames; frame++ ) {
            map[ 0 ][ frame ] = createUvMapCube( frame, frames, rows );
        }
        return map;
    }
    
    private static float[] createUvMapCube( int frame, int frames, int rows ) {
        if ( frame < 0 || frame >= frames || frames > rows ) {
            throw new IllegalArgumentException(
                "Illegal frame:" + frame + " for frames:" + frames + ", rows:" + rows + "." );
        }
        float hor = 1 / 8f;
        float vertUp = ( frame ) / ( float ) rows + UV_DELTA;
        float vertDown = ( frame + 1 ) / ( float ) rows - UV_DELTA;

        return new float[]{
            //face 0
            0 * hor + UV_DELTA, vertDown, 1 * hor - UV_DELTA, vertDown, 1 * hor - UV_DELTA, vertUp, 0 * hor + UV_DELTA,
            vertDown, 1 * hor - UV_DELTA, vertUp, 0 * hor + UV_DELTA, vertUp,
    
            //face 1
            1 * hor + UV_DELTA, vertDown, 2 * hor - UV_DELTA, vertDown, 2 * hor - UV_DELTA, vertUp, 1 * hor + UV_DELTA,
            vertDown, 2 * hor - UV_DELTA, vertUp, 1 * hor + UV_DELTA, vertUp,
    
            //face 2
            2 * hor + UV_DELTA, vertDown, 3 * hor - UV_DELTA, vertDown, 3 * hor - UV_DELTA, vertUp, 2 * hor + UV_DELTA,
            vertDown, 3 * hor - UV_DELTA, vertUp, 2 * hor + UV_DELTA, vertUp,
    
            //face 3
            3 * hor + UV_DELTA, vertDown, 4 * hor - UV_DELTA, vertDown, 4 * hor - UV_DELTA, vertUp, 3 * hor + UV_DELTA,
            vertDown, 4 * hor - UV_DELTA, vertUp, 3 * hor + UV_DELTA, vertUp,
    
            //face 4
            4 * hor + UV_DELTA, vertDown, 5 * hor - UV_DELTA, vertDown, 5 * hor - UV_DELTA, vertUp, 4 * hor + UV_DELTA,
            vertDown, 5 * hor - UV_DELTA, vertUp, 4 * hor + UV_DELTA, vertUp,
    
            //face 5
            5 * hor + UV_DELTA, vertDown, 6 * hor - UV_DELTA, vertDown, 6 * hor - UV_DELTA, vertUp, 5 * hor + UV_DELTA,
            vertDown, 6 * hor - UV_DELTA, vertUp, 5 * hor + UV_DELTA, vertUp
        };
    }

    private CubeUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }

}
