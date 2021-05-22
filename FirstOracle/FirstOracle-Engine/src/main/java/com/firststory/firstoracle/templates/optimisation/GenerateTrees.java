/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.data.Index2D;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author n1t4chi
 */
public class GenerateTrees {
    
    public static void main( String[] args ) {
        Random r = ThreadLocalRandom.current();
        Set< Index2D > indices = new HashSet<>();
        var shift = 0 ;
        for( var i=0; i< 100 ; i++ ) {
            var x = 2*( r.nextInt(40- shift ) + shift );
            var z = 2*( r.nextInt(40 ) );
            var index = Index2D.id2( x, z );
            if( indices.contains( index ) ) {
                i--;
                continue;
            }
            indices.add( index );
        }
        System.err.print( "\"position\": [" );
        var i = 0;
        for( var index : indices ) {
            if( i%10 == 0 ) {
                System.err.print( "\",\n\"" );
            }
            System.err.print( "{" + index.x() + "," + index.y() + "}," );
            i++;
        }
        System.err.println( "\"\n]" );
    }
}
