/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.*;
import static com.firststory.firstoracle.object.UV.uv;

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
        var hash = hash( frames, rows );
        return instances.computeIfAbsent( hash,
            k -> new HexPrismUvMap( frames, rows )
        );
    }

    private static long hash( int frames, int rows ) {
        return ( ( long ) rows << 32 ) + frames;
    }
    
    @SuppressWarnings( "unchecked" )
    private static List< UV >[][] getUvMap( int frames, int rows ) {
        List< UV >[][] map = array( 1 , frames );

        for ( var frame = 0; frame < frames; frame++ ) {
            map[ 0 ][ frame ] = createUvMapHexPrism( frame, frames, rows );
        }
        return map;
    }

    private static List< UV > createUvMapHexPrism( int frame, int frames, int rows ) {
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
            //face 0
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
            uv( 5 * hor + UV_DELTA, vertUp ),


            //face 6
            uv( 6.5f * hor, vertMiddle ),
            uv( 6.75f * hor - UV_DELTA, vertUp ),
            uv( 6.25f * hor + UV_DELTA, vertUp ),
            uv( 6.5f * hor, vertMiddle ),
            uv( 6.25f * hor + UV_DELTA, vertUp ),
            uv( 6 * hor + UV_DELTA, vertMiddle ),
            uv( 6.5f * hor, vertMiddle ),

            uv( 6 * hor + UV_DELTA, vertMiddle ),
            uv( 6.25f * hor + UV_DELTA, vertDown ),
            uv( 6.5f * hor, vertMiddle ),

            uv( 6.25f * hor + UV_DELTA, vertDown ),
            uv( 6.75f * hor - UV_DELTA, vertDown ),
            uv( 6.5f * hor, vertMiddle ),

            uv( 6.75f * hor - UV_DELTA, vertDown ),
            uv( 7 * hor - UV_DELTA, vertMiddle ),
            uv( 6.5f * hor, vertMiddle ),

            uv( 7 * hor - UV_DELTA, vertMiddle ),
            uv( 6.75f * hor - UV_DELTA, vertUp ),


            //face 7
            uv( 7.5f * hor, vertMiddle ),
            uv( 7.75f * hor - UV_DELTA, vertUp ),
            uv( 7.25f * hor + UV_DELTA, vertUp ),
            uv( 7.5f * hor, vertMiddle ),
            uv( 7.25f * hor + UV_DELTA, vertUp ),
            uv( 7 * hor + UV_DELTA, vertMiddle ),
            uv( 7.5f * hor, vertMiddle ),

            uv( 7 * hor + UV_DELTA, vertMiddle ),
            uv( 7.25f * hor + UV_DELTA, vertDown ),
            uv( 7.5f * hor, vertMiddle ),

            uv( 7.25f * hor + UV_DELTA, vertDown ),
            uv( 7.75f * hor - UV_DELTA, vertDown ),
            uv( 7.5f * hor, vertMiddle ),

            uv( 7.75f * hor - UV_DELTA, vertDown ),
            uv( 8 * hor - UV_DELTA, vertMiddle ),
            uv( 7.5f * hor, vertMiddle ),

            uv( 8 * hor - UV_DELTA, vertMiddle ),
            uv( 7.75f * hor - UV_DELTA, vertUp )
        );
    }
    
    private HexPrismUvMap( int frames, int rows ) {
        super( getUvMap( frames, rows ) );
    }
}
