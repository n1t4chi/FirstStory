/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import org.junit.Assert;
import org.junit.Test;

public class LinearMemoryControllerTest {
    
    @Test
    public void singleSimpleBufferTest() {
        int outOfMemoryLength = 15;
        char[] outOfBoundsData = "aeiouaeiouaeiou".toCharArray();
        
        int length = 5;
        char[] data1 = "aeiou".toCharArray();
        char[] data2 = "qwert".toCharArray();
        
        TestableLinearMemoryController controller = new TestableLinearMemoryController( new TestableLinearMemory( 10 ) );
        
        assertException(
            LinearMemoryController.OutOfMemoryException.class,
            () -> controller.createBuffer( outOfMemoryLength )
        );
        TestableDataBufferInLinearMemory buffer = controller.createBuffer( length );
        Assert.assertEquals( length, buffer.length() );
        
        assertException(
            TestableLinearMemory.WriteMemoryOutOfBoundException.class,
            () -> buffer.load( outOfBoundsData )
        );
        buffer.load( data1 );
        
        assertException( TestableLinearMemory.ReadMemoryOutOfBoundException.class,
            () -> new TestableDataBufferInLinearMemory( controller,
                new LinearMemoryLocation( 0, outOfMemoryLength, outOfMemoryLength )
            ).readBuffer()
        );
        Assert.assertArrayEquals( data1, buffer.readBuffer() );
        
        buffer.load( data2 );
        Assert.assertArrayEquals( data2, buffer.readBuffer() );
    }
    
    @Test
    public void multipleSimpleBufferTest() {
        
        int length = 5;
        char[] data1 = "aeiou".toCharArray();
        char[] data2 = "zxcvb".toCharArray();
        
        TestableLinearMemoryController controller = new TestableLinearMemoryController( new TestableLinearMemory( 10 ) );
        
        TestableDataBufferInLinearMemory buffer1 = controller.createBuffer( length );
        TestableDataBufferInLinearMemory buffer2 = controller.createBuffer( length );
        assertException( LinearMemoryController.OutOfMemoryException.class, () -> controller.createBuffer( length ) );
        
        buffer1.load( data1 );
        buffer2.load( data2 );
        
        Assert.assertArrayEquals( data1, buffer1.readBuffer() );
        Assert.assertArrayEquals( data2, buffer2.readBuffer() );
    }
    
    @Test
    public void simpleReassignmentBufferTest() {
        
        int length = 5;
        char[] data1 = "aeiou".toCharArray();
        char[] data2 = "zxcvb".toCharArray();
        char[] data3 = "qwert".toCharArray();
        char[] data4 = "asdfg".toCharArray();
        
        TestableLinearMemoryController controller = new TestableLinearMemoryController( new TestableLinearMemory( 10 ) );
        
        TestableDataBufferInLinearMemory buffer1 = controller.createBuffer( length );
        buffer1.load( data1 );
        Assert.assertArrayEquals( data1, buffer1.readBuffer() );
        
        TestableDataBufferInLinearMemory buffer2 = controller.createBuffer( length );
        buffer2.load( data2 );
        Assert.assertArrayEquals( data1, buffer1.readBuffer() );
        Assert.assertArrayEquals( data2, buffer2.readBuffer() );
        
        buffer2.delete();
        assertException( BufferNotCreatedException.class, buffer2::delete );
        
        TestableDataBufferInLinearMemory buffer3 = controller.createBuffer( length );
        buffer3.load( data3 );
        
        Assert.assertArrayEquals( data1, buffer1.readBuffer() );
        assertException( BufferNotCreatedException.class, buffer2::readBuffer );
        Assert.assertArrayEquals( data3, buffer3.readBuffer() );
        
        buffer1.delete();
        
        TestableDataBufferInLinearMemory buffer4 = controller.createBuffer( length );
        buffer4.load( data4 );
        
        
        
        assertException( BufferNotCreatedException.class, buffer1::readBuffer );
        assertException( BufferNotCreatedException.class, buffer2::readBuffer );
        Assert.assertArrayEquals( data3, buffer3.readBuffer() );
        Assert.assertArrayEquals( data4, buffer4.readBuffer() );
    }
    
    private void assertException( Class< ? extends Exception > aClass, Failable test ) {
        try {
            test.execute();
            Assert.fail();
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