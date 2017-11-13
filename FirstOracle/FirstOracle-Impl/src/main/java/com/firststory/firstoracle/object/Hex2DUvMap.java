/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.HashMap;

import static com.firststory.firstoracle.FirstOracleConstants.SQRT3_DIV2;

/**
 * @author n1t4chi
 */
public class Hex2DUvMap extends UvMap {
    private static final HashMap< Long, Hex2DUvMap > instances = new HashMap<>( 5 );

    public static Hex2DUvMap getHex2DUvMap() {
        return getHex2DUvMap( 1,1 );
    }
    /**
     * Returns UV map specifically designed for cube objects
     *
     * @param frames How many frames will be in texture
     * @param rows   How many rows for frames will be in texture
     * @return UV map instance
     */
    public static Hex2DUvMap getHex2DUvMap( int frames, int rows ) {
        long hash = hash( frames, rows );
        Hex2DUvMap instance = instances.computeIfAbsent( hash, k -> new Hex2DUvMap( frames, rows ) );
        return instance;
    }

    private static long hash( int frames, int rows ) {
        return ( ( long ) rows << 32 ) + frames;
    }

    private static float[][][] getUvMap( int frames, int rows ) {
        float[][][] map = new float[1][frames][];

        for ( int frame = 0; frame < frames; frame++ ) {
            map[0][frame] = createUvMapHex2D( frame, frames, rows );
        }
        return map;
    }

    private static float[] createUvMapHex2D( int frame, int frames, int rows ) {
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
            0.5f * hor, vertMiddle, 0.75f * hor - del, vertUp, 0.25f * hor + del, vertUp,
            0.5f * hor, vertMiddle, 0.25f * hor + del, vertUp, 0 * hor + del, vertMiddle,
            0.5f * hor, vertMiddle, 0 * hor + del, vertMiddle, 0.25f * hor + del, vertDown,
            0.5f * hor, vertMiddle, 0.25f * hor + del, vertDown, 0.75f * hor - del, vertDown,
            0.5f * hor, vertMiddle, 0.75f * hor - del, vertDown, 1 * hor - del, vertMiddle,
            0.5f * hor, vertMiddle, 1 * hor - del, vertMiddle, 0.75f * hor - del, vertUp,
        };
    }

    private Hex2DUvMap( int frames, int rows ){
        super( getUvMap( frames, rows ) );
    }
}
