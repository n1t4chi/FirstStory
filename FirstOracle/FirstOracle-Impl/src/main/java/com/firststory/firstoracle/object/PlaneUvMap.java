/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import java.util.HashMap;

/**
 * @author: n1t4chi
 */
public class PlaneUvMap extends UvMap {
    private static HashMap< Long, PlaneUvMap > map = new HashMap<>( 25 );

    public static PlaneUvMap getPlaneUvMap() {
        return getPlaneUvMap(1,1,1,1);
    }
    /**
     * Returns UV map specifically designed for cube objects
     *
     * @param directions How many directions will be in texture
     * @param frames     How many frames will be in texture
     * @param rows       How many rows for frames will be in texture
     * @param columns    How many columns for directions will be in texture
     * @return
     */
    public static PlaneUvMap getPlaneUvMap( int directions, int frames, int rows, int columns ) {
        long hash = hash( directions, frames, rows, columns );
        PlaneUvMap instance = map.get( hash );
        if ( instance == null ) {
            map.put( hash, instance = new PlaneUvMap( directions, frames, rows, columns ) );
        }
        return instance;
    }

    private static long hash( int directions, int frames, int columns, int rows ) {
        return (
            ( ( ( ( ( ( long ) columns << 16 ) + rows ) << 16 ) + directions ) << 16 ) + frames
        );

    }

    private static float[][][] getUvMap( int directions, int frames, int columns, int rows ) {
        float[][][] map = new float[directions][frames][];

        for ( int direction = 0; direction < directions; direction++ ) {
            for ( int frame = 0; frame < frames; frame++ ) {
                map[direction][frame] = createUvMapPlane( direction,
                    frame,
                    directions,
                    frames,
                    columns,
                    rows
                );
            }
        }
        return map;
    }

    private static float[] createUvMapPlane(
        int direction, int frame, int directions, int frames, int columns, int rows
    )
    {
        if ( frame < 0 || frame >= frames || direction < 0 || direction >= directions ) {
            throw new IllegalArgumentException(
                "Illegal frame/Direction: " + "direction:" + direction + ", frame:" + frame +
                " for parameters: " + "directions:" + directions + ", " + "frames:" + frames +
                ", " + "columns:" + columns + ", " + "rows:" + rows + "." );
        }
        float del = 0.01f;
        float vertUp = ( frame ) / ( float ) rows + del;
        float vertDown = ( frame + 1 ) / ( float ) rows - del;

        float horLeft = ( direction ) / ( float ) columns + del;
        float horRight = ( direction + 1 ) / ( float ) columns - del;

        float[] rtrn = {
            horLeft, vertDown, horRight, vertDown, horRight, vertUp, horLeft, vertDown, horRight,
            vertUp, horLeft, vertUp
        };
        return rtrn;
    }

    public PlaneUvMap( int directions, int frames, int rows, int columns ) {
        super( getUvMap( directions, frames, rows, columns ) );
    }
}
