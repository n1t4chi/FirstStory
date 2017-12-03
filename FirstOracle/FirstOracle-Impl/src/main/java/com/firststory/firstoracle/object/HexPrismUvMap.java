/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.HashMap;

import static com.firststory.firstoracle.FirstOracleConstants.SQRT3_DIV2;
import static com.firststory.firstoracle.FirstOracleConstants.UV_DELTA;

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
        float vertUp = ( frame ) / ( float ) rows + UV_DELTA;
        float vertDown = ( frame + 1 ) / ( float ) rows - UV_DELTA;

        float vertMiddle = ( frame + 0.5f ) / ( float ) rows;
        float vertMidUp = ( frame + 0.5f - SQRT3_DIV2 / 2 ) / rows + UV_DELTA;
        float vertMidDown = ( frame + 0.5f + SQRT3_DIV2 / 2 ) / rows - UV_DELTA;

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
            vertDown, 6 * hor - UV_DELTA, vertUp, 5 * hor + UV_DELTA, vertUp,
    
            //face 6
            6.5f * hor, vertMiddle, 6.75f * hor - UV_DELTA, vertUp, 6.25f * hor + UV_DELTA, vertUp, 6.5f * hor,
            vertMiddle, 6.25f * hor + UV_DELTA, vertUp, 6 * hor + UV_DELTA, vertMiddle, 6.5f * hor, vertMiddle,
            6 * hor + UV_DELTA, vertMiddle, 6.25f * hor + UV_DELTA, vertDown, 6.5f * hor, vertMiddle,
            6.25f * hor + UV_DELTA, vertDown, 6.75f * hor - UV_DELTA, vertDown, 6.5f * hor, vertMiddle,
            6.75f * hor - UV_DELTA, vertDown, 7 * hor - UV_DELTA, vertMiddle, 6.5f * hor, vertMiddle,
            7 * hor - UV_DELTA, vertMiddle, 6.75f * hor - UV_DELTA, vertUp,
    
            //face 7
            7.5f * hor, vertMiddle, 7.75f * hor - UV_DELTA, vertUp, 7.25f * hor + UV_DELTA, vertUp, 7.5f * hor,
            vertMiddle, 7.25f * hor + UV_DELTA, vertUp, 7 * hor + UV_DELTA, vertMiddle, 7.5f * hor, vertMiddle,
            7 * hor + UV_DELTA, vertMiddle, 7.25f * hor + UV_DELTA, vertDown, 7.5f * hor, vertMiddle,
            7.25f * hor + UV_DELTA, vertDown, 7.75f * hor - UV_DELTA, vertDown, 7.5f * hor, vertMiddle,
            7.75f * hor - UV_DELTA, vertDown, 8 * hor - UV_DELTA, vertMiddle, 7.5f * hor, vertMiddle,
            8 * hor - UV_DELTA, vertMiddle, 7.75f * hor - UV_DELTA, vertUp
        };
    }
    
    private HexPrismUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }
}
