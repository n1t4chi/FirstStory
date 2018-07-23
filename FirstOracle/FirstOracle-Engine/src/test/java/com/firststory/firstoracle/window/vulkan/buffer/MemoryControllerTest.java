/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import org.junit.Assert;
import org.junit.Test;

public class MemoryControllerTest {
    
    @Test
    public void singleBufferTest() {
        int outOfMemoryLength = 15;
        char[] outOfBoundsData = "aeiouaeiouaeiou".toCharArray();
        
        int length = 5;
        char[] data = "aeiou".toCharArray();
        
        MemoryController controller = new MemoryController( 10 );
        
        assertException( MemoryController.OutOfMemoryException.class,
            () -> controller.createBuffer( outOfMemoryLength )
        );
        DataBuffer buffer = controller.createBuffer( length );
        Assert.assertEquals( length, buffer.length() );
        
        assertException( Memory.WriteMemoryOutOfBoundException.class, () -> buffer.write( outOfBoundsData )
        );
        buffer.write( data );
        
        assertException( Memory.ReadMemoryOutOfBoundException.class,
            () -> ( new DataBuffer( controller.getMemory(), new MemoryLocation( 0, outOfMemoryLength ) ) ).read()
        );
        Assert.assertArrayEquals( data, buffer.read() );
    }
    
    private void assertException( Class< ? extends Exception > aClass, Failable test ) {
        try {
            test.execute();
        } catch ( Exception ex ) {
            try {
                Assert.assertEquals( aClass, ex.getClass() );
            } catch ( Throwable ex2 ) {
                ex2.addSuppressed( ex );
                throw ex2;
            }
        }
    }
    
    private interface Failable {
        
        void execute() throws Exception;
    }
}