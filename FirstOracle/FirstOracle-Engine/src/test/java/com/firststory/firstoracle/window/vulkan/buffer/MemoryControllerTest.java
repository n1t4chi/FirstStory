/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import org.junit.Assert;
import org.junit.Test;

public class MemoryControllerTest {
    
    @Test
    public void singleSimpleBufferTest() {
        int outOfMemoryLength = 15;
        char[] outOfBoundsData = "aeiouaeiouaeiou".toCharArray();
        
        int length = 5;
        char[] data1 = "aeiou".toCharArray();
        char[] data2 = "qwert".toCharArray();
        
        MemoryController controller = new MemoryController( new TestMemory( 10 ) );
        
        assertException( MemoryController.OutOfMemoryException.class,
            () -> controller.createBuffer( outOfMemoryLength )
        );
        DataBuffer buffer = controller.createBuffer( length );
        Assert.assertEquals( length, buffer.length() );
        
        assertException( TestMemory.WriteMemoryOutOfBoundException.class, () -> buffer.write( outOfBoundsData )
        );
        buffer.write( data1 );
        
        assertException( TestMemory.ReadMemoryOutOfBoundException.class,
            () -> ( new DataBuffer( controller, new MemoryLocation( 0, outOfMemoryLength, outOfMemoryLength ) ) ).read()
        );
        Assert.assertArrayEquals( data1, buffer.read() );
        
        buffer.write( data2 );
        Assert.assertArrayEquals( data2, buffer.read() );
    }
    
    @Test
    public void multipleSimpleBufferTest() {
        
        int length = 5;
        char[] data1 = "aeiou".toCharArray();
        char[] data2 = "zxcvb".toCharArray();
        
        MemoryController controller = new MemoryController( new TestMemory( 10 ) );
    
        DataBuffer buffer1 = controller.createBuffer( length );
        DataBuffer buffer2 = controller.createBuffer( length );
        assertException( MemoryController.OutOfMemoryException.class,
            () -> controller.createBuffer( length )
        );
    
        buffer1.write( data1 );
        buffer2.write( data2 );
        
        Assert.assertArrayEquals( data1, buffer1.read() );
        Assert.assertArrayEquals( data2, buffer2.read() );
    }
    
    @Test
    public void simpleReassignmentBufferTest() {
        
        int length = 5;
        char[] data1 = "aeiou".toCharArray();
        char[] data2 = "zxcvb".toCharArray();
        char[] data3 = "qwert".toCharArray();
        char[] data4 = "asdfg".toCharArray();
        
        MemoryController controller = new MemoryController( new TestMemory( 10 ) );
        
        DataBuffer buffer1 = controller.createBuffer( length );
        buffer1.write( data1 );
        Assert.assertArrayEquals( data1, buffer1.read() );
        
        DataBuffer buffer2 = controller.createBuffer( length );
        buffer2.write( data2 );
        Assert.assertArrayEquals( data1, buffer1.read() );
        Assert.assertArrayEquals( data2, buffer2.read() );
        
        buffer2.free();
        assertException( DataBuffer.BufferClosedException.class, buffer2::free );
        
        DataBuffer buffer3 = controller.createBuffer( length );
        buffer3.write( data3 );
        
        Assert.assertArrayEquals( data1, buffer1.read() );
        assertException( DataBuffer.BufferClosedException.class, buffer2::read );
        Assert.assertArrayEquals( data3, buffer3.read() );
        
        buffer1.free();
    
        DataBuffer buffer4 = controller.createBuffer( length );
        buffer4.write( data4 );
    
        assertException( DataBuffer.BufferClosedException.class, buffer1::read );
        assertException( DataBuffer.BufferClosedException.class, buffer2::read );
        Assert.assertArrayEquals( data3, buffer3.read() );
        Assert.assertArrayEquals( data4, buffer4.read() );
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