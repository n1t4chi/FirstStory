/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import com.firststory.firstoracle.buffer.BufferNotCreatedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinearMemoryControllerTest {
    
    @Test
    public void singleSimpleBufferTest() {
        var outOfMemoryLength = 15;
        var outOfBoundsData = "aeiouaeiouaeiou".toCharArray();
    
        var length = 5;
        var data1 = "aeiou".toCharArray();
        var data2 = "qwert".toCharArray();
    
        var controller = new TestableLinearMemoryController( new TestableLinearMemory( 10 ) );
        
        assertException(
            VulkanLinearMemoryController.OutOfMemoryException.class,
            () -> controller.createBuffer( outOfMemoryLength )
        );
        var buffer = controller.createBuffer( length );
        Assertions.assertEquals( length, buffer.length() );
        
        assertException(
            TestableLinearMemory.WriteMemoryOutOfBoundException.class,
            () -> buffer.load( outOfBoundsData )
        );
        buffer.load( data1 );
        
        assertException( TestableLinearMemory.ReadMemoryOutOfBoundException.class,
            () -> new TestableDataBufferInLinearMemory( controller,
                new VulkanLinearMemoryLocation( 0, outOfMemoryLength, outOfMemoryLength )
            ).readBuffer()
        );
        Assertions.assertArrayEquals( data1, buffer.readBuffer() );
        
        buffer.load( data2 );
        Assertions.assertArrayEquals( data2, buffer.readBuffer() );
    }
    
    @Test
    public void multipleSimpleBufferTest() {
    
        var length = 5;
        var data1 = "aeiou".toCharArray();
        var data2 = "zxcvb".toCharArray();
    
        var controller = new TestableLinearMemoryController( new TestableLinearMemory( 10 ) );
    
        var buffer1 = controller.createBuffer( length );
        var buffer2 = controller.createBuffer( length );
        assertException( VulkanLinearMemoryController.OutOfMemoryException.class, () -> controller.createBuffer( length ) );
        
        buffer1.load( data1 );
        buffer2.load( data2 );
        
        Assertions.assertArrayEquals( data1, buffer1.readBuffer() );
        Assertions.assertArrayEquals( data2, buffer2.readBuffer() );
    }
    
    @Test
    public void simpleReassignmentBufferTest() {
    
        var length = 5;
        var data1 = "aeiou".toCharArray();
        var data2 = "zxcvb".toCharArray();
        var data3 = "qwert".toCharArray();
        var data4 = "asdfg".toCharArray();
    
        var controller = new TestableLinearMemoryController( new TestableLinearMemory( 10 ) );
    
        var buffer1 = controller.createBuffer( length );
        buffer1.load( data1 );
        Assertions.assertArrayEquals( data1, buffer1.readBuffer() );
    
        var buffer2 = controller.createBuffer( length );
        buffer2.load( data2 );
        Assertions.assertArrayEquals( data1, buffer1.readBuffer() );
        Assertions.assertArrayEquals( data2, buffer2.readBuffer() );
        
        buffer2.delete();
        assertException( BufferNotCreatedException.class, buffer2::delete );
    
        var buffer3 = controller.createBuffer( length );
        buffer3.load( data3 );
        
        Assertions.assertArrayEquals( data1, buffer1.readBuffer() );
        assertException( BufferNotCreatedException.class, buffer2::readBuffer );
        Assertions.assertArrayEquals( data3, buffer3.readBuffer() );
        
        buffer1.delete();
    
        var buffer4 = controller.createBuffer( length );
        buffer4.load( data4 );
        
        
        
        assertException( BufferNotCreatedException.class, buffer1::readBuffer );
        assertException( BufferNotCreatedException.class, buffer2::readBuffer );
        Assertions.assertArrayEquals( data3, buffer3.readBuffer() );
        Assertions.assertArrayEquals( data4, buffer4.readBuffer() );
    }
    
    private void assertException( Class< ? extends Exception > aClass, Failable test ) {
        try {
            test.execute();
            Assertions.fail();
        } catch ( Exception ex ) {
            try {
                Assertions.assertEquals( aClass, ex.getClass() );
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