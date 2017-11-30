/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.HashMap;

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
        float del = 0.01f;
        float vertUp = ( frame ) / ( float ) rows + del;
        float vertDown = ( frame + 1 ) / ( float ) rows - del;

        return new float[]{
            //face 0
            0 * hor + del, vertDown,
            1 * hor - del, vertDown,
            1 * hor - del, vertUp,
            0 * hor + del, vertDown, 1 * hor - del, vertUp, 0 * hor + del, vertUp,

            //face 1
            1 * hor + del, vertDown,
            2 * hor - del, vertDown,
            2 * hor - del, vertUp,
            1 * hor + del, vertDown, 2 * hor - del, vertUp, 1 * hor + del, vertUp,

            //face 2
            2 * hor + del, vertDown,
            3 * hor - del, vertDown,
            3 * hor - del, vertUp,
            2 * hor + del, vertDown, 3 * hor - del, vertUp, 2 * hor + del, vertUp,

            //face 3
            3 * hor + del, vertDown,
            4 * hor - del, vertDown,
            4 * hor - del, vertUp,
            3 * hor + del, vertDown, 4 * hor - del, vertUp, 3 * hor + del, vertUp,

            //face 4
            4 * hor + del, vertDown,
            5 * hor - del, vertDown,
            5 * hor - del, vertUp,
            4 * hor + del, vertDown, 5 * hor - del, vertUp, 4 * hor + del, vertUp,

            //face 5
            5 * hor + del, vertDown,
            6 * hor - del, vertDown,
            6 * hor - del, vertUp,
            5 * hor + del, vertDown, 6 * hor - del, vertUp, 5 * hor + del, vertUp
        };
    }

    private CubeUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }

}
