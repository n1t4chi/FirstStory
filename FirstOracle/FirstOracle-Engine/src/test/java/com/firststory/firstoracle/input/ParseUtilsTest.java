/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseUtilsTest {
    
    @Test
    void toList() {
        assertEquals( List.of(), ParseUtils.toList( "" ) );
        assertEquals( List.of(), ParseUtils.toList( "[]" ) );
        assertEquals( List.of( "a" ), ParseUtils.toList( "[a]" ) );
        assertEquals( List.of( "a" ), ParseUtils.toList( "a" ) );
        assertEquals( List.of( "a", "b" ), ParseUtils.toList( "[ a ,b ]" ) );
        assertEquals( List.of( "a", "b" ), ParseUtils.toList( " a ,b " ) );
        assertEquals( List.of( "a", "b","c", "d", "e", "f" ), ParseUtils.toList( " a , b , c , d , e , f " ) );
    }
}