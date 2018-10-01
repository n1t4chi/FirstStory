/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.UV;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.firststory.firstoracle.FirstOracleConstants.UV_DELTA;
import static com.firststory.firstoracle.FirstOracleConstants.array;
import static com.firststory.firstoracle.data.UV.uv;

/**
 * @author n1t4chi
 */
public class PlaneUvMap extends UvMap {
    
    private static final HashMap< Long, PlaneUvMap > map = new HashMap<>( 25 );
    
    public static PlaneUvMap getPlaneUvMap() {
        return getPlaneUvMap( 1, 1, 1, 1 );
    }
    
    /**
     * Returns UV map specifically designed for cube objects
     *
     * @param directions How many directions will be in texture
     * @param frames     How many frames will be in texture
     * @param columns    How many columns for directions will be in texture
     * @param rows       How many rows for frames will be in texture
     * @return UV map instance
     */
    public static PlaneUvMap getPlaneUvMap( int directions, int frames, int columns, int rows ) {
        var hash = hash( directions, frames, rows, columns );
        return map.computeIfAbsent( hash, k -> new PlaneUvMap( directions, frames, columns, rows ) );
    }
    
    private static long hash( int directions, int frames, int columns, int rows ) {
        return (
            ( ( ( ( ( ( long ) columns << 16 ) + rows ) << 16 ) + directions ) << 16 ) + frames
        );
        
    }
    
    @SuppressWarnings( "unchecked" )
    private static List< UV >[][] getUvMap( int directions, int frames, int columns, int rows ) {
        List< UV >[][] map = array( directions , frames );
        
        for ( var direction = 0; direction < directions; direction++ ) {
            for ( var frame = 0; frame < frames; frame++ ) {
                map[direction][frame] = createUvMapPlane( direction, frame, directions, frames, columns, rows );
            }
        }
        return map;
    }
    
    private static List< UV > createUvMapPlane(
        int direction,
        int frame,
        int directions,
        int frames,
        int columns,
        int rows
    ) {
        if ( frame < 0 || frame >= frames || direction < 0 || direction >= directions ) {
            throw new IllegalArgumentException(
                "Illegal frame/Direction: " + "direction:" + direction + ", frame:" + frame + " for parameters: " +
                "directions:" + directions + ", " + "frames:" + frames + ", " + "columns:" + columns + ", " + "rows:" +
                rows + "." );
        }
        var vertUp = ( frame ) / ( float ) rows + UV_DELTA;
        var vertDown = ( frame + 1 ) / ( float ) rows - UV_DELTA;
    
        var horLeft = ( direction ) / ( float ) columns + UV_DELTA;
        var horRight = ( direction + 1 ) / ( float ) columns - UV_DELTA;
    
        return Arrays.asList(
            uv( horLeft, vertDown ),
            uv( horRight, vertDown ),
            uv( horRight, vertUp ),
            uv( horLeft, vertDown ),
            uv( horRight, vertUp ),
            uv( horLeft, vertUp )
        );
    }
    
    public PlaneUvMap( int directions, int frames, int columns, int rows ) {
        super( getUvMap( directions, frames, columns, rows ) );
    }
    
    public PlaneUvMap( Texture texture ) {
        this( texture.getDirections(), texture.getFrames(), texture.getColumns(), texture.getRows() );
    }
}
