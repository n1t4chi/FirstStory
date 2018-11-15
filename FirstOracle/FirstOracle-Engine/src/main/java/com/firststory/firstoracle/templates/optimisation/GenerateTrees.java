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
        var shift = 15;
        for( var i=0; i< 400 ; i++ ) {
            var x = 2*( r.nextInt(50- shift ) + shift );
            var z = 2*( r.nextInt(50 ) );
            var index = Index2D.id2( x, z );
            if( indices.contains( index ) ) {
                i--;
                continue;
            }
            indices.add( index );
            System.out.println(
                "\"tree" + i + "\": {" +
                    "\"class\": \"$obj3d\", \"rotation\": \"$rot3d\", \"scale\": \"$scaleObj3d\", \"texture\": \"$tree3d\"," +
                    "\"position\": \"" + x + ",13," + z +"\"" +
                    "},"
            );
        }
    }
}
