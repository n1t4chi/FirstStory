/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.HashMap;

import static com.firststory.firstoracle.FirstOracleConstants.SQRT3_DIV2;

/**
 * @author n1t4chi
 */
public class HexPrismUvMap extends UvMap {
    private static final HashMap< Long, HexPrismUvMap > instances = new HashMap<>( 5 );

    public static HexPrismUvMap getHexPrismUvMap() {
        return getHexPrismUvMap( 1, 1 );
    }

    /**
     * Returns UV map specifically designed for cube objects
     *
     * @param frames How many frames will be in texture
     * @param rows   How many rows for frames will be in texture
     *
     * @return UV map instance
     */
    public static HexPrismUvMap getHexPrismUvMap( int frames, int rows ) {
        long hash = hash( frames, rows );
        return instances.computeIfAbsent( hash,
            k -> new HexPrismUvMap( frames, rows )
        );
    }

    private static long hash( int frames, int rows ) {
        return ( ( long ) rows << 32 ) + frames;
    }

    private static float[][][] getUvMap( int frames, int rows ) {
        float[][][] map = new float[ 1 ][ frames ][];

        for ( int frame = 0; frame < frames; frame++ ) {
            map[ 0 ][ frame ] = createUvMapHexPrism( frame, frames, rows );
        }
        return map;
    }

    private static float[] createUvMapHexPrism( int frame, int frames, int rows ) {
        if ( frame < 0 || frame >= frames || frames > rows ) {
            throw new IllegalArgumentException(
                "Illegal frame:" + frame + " for frames:" + frames + ", rows:" + rows + "." );
        }
        float hor = 1 / 8f;
        //lower a bit UV map so pixels from other face will not be taken into consideration.
        float del = 0.01f;
        float vertUp = ( frame ) / ( float ) rows + del;
        float vertDown = ( frame + 1 ) / ( float ) rows - del;

        float vertMiddle = ( frame + 0.5f ) / ( float ) rows;
        float vertMidUp = ( frame + 0.5f - SQRT3_DIV2 / 2 ) / rows + del;
        float vertMidDown = ( frame + 0.5f + SQRT3_DIV2 / 2 ) / rows - del;

        return new float[]{
            //face 0
            0 * hor + del, vertDown, 1 * hor - del, vertDown, 1 * hor - del, vertUp,
            0 * hor + del, vertDown, 1 * hor - del, vertUp, 0 * hor + del, vertUp,

            //face 1
            1 * hor + del, vertDown, 2 * hor - del, vertDown, 2 * hor - del, vertUp,
            1 * hor + del, vertDown, 2 * hor - del, vertUp, 1 * hor + del, vertUp,

            //face 2
            2 * hor + del, vertDown, 3 * hor - del, vertDown, 3 * hor - del, vertUp,
            2 * hor + del, vertDown, 3 * hor - del, vertUp, 2 * hor + del, vertUp,

            //face 3
            3 * hor + del, vertDown, 4 * hor - del, vertDown, 4 * hor - del, vertUp,
            3 * hor + del, vertDown, 4 * hor - del, vertUp, 3 * hor + del, vertUp,

            //face 4
            4 * hor + del, vertDown, 5 * hor - del, vertDown, 5 * hor - del, vertUp,
            4 * hor + del, vertDown, 5 * hor - del, vertUp, 4 * hor + del, vertUp,

            //face 5
            5 * hor + del, vertDown, 6 * hor - del, vertDown, 6 * hor - del, vertUp,
            5 * hor + del, vertDown, 6 * hor - del, vertUp, 5 * hor + del, vertUp,

            //face 6
            6.5f * hor, vertMiddle, 6.75f * hor - del, vertUp, 6.25f * hor + del, vertUp,
            6.5f * hor, vertMiddle, 6.25f * hor + del, vertUp, 6 * hor + del, vertMiddle,
            6.5f * hor, vertMiddle, 6 * hor + del, vertMiddle, 6.25f * hor + del, vertDown,
            6.5f * hor, vertMiddle, 6.25f * hor + del, vertDown, 6.75f * hor - del, vertDown,
            6.5f * hor, vertMiddle, 6.75f * hor - del, vertDown, 7 * hor - del, vertMiddle,
            6.5f * hor, vertMiddle, 7 * hor - del, vertMiddle, 6.75f * hor - del, vertUp,

            //face 7
            7.5f * hor, vertMiddle, 7.75f * hor - del, vertUp, 7.25f * hor + del, vertUp,
            7.5f * hor, vertMiddle, 7.25f * hor + del, vertUp, 7 * hor + del, vertMiddle,
            7.5f * hor, vertMiddle, 7 * hor + del, vertMiddle, 7.25f * hor + del, vertDown,
            7.5f * hor, vertMiddle, 7.25f * hor + del, vertDown, 7.75f * hor - del, vertDown,
            7.5f * hor, vertMiddle, 7.75f * hor - del, vertDown, 8 * hor - del, vertMiddle,
            7.5f * hor, vertMiddle, 8 * hor - del, vertMiddle, 7.75f * hor - del, vertUp
        };
    }
    
    private HexPrismUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }
}
