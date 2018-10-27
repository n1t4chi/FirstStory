/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.buffer;

import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanLinearMemoryLocation.BY_POSITION;
import static com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanLinearMemoryLocation.BY_TRUE_SIZE;

/**
 * @author n1t4chi
 */
class VulkanLinearMemoryController< Memory extends VulkanLinearMemory< Data >, Data > {
    
    private final long memoryOffsetAlignment;
    private final Memory memory;
    private final PriorityQueue< VulkanLinearMemoryLocation > freeSpace = new PriorityQueue<>( BY_TRUE_SIZE );
    
    VulkanLinearMemoryController( Memory memory, long memoryOffsetAlignment ) {
        this.memoryOffsetAlignment = memoryOffsetAlignment;
        this.memory = memory;
        var location = newLoc( 0, memory.length(), memory.length() );
        freeSpace.add( location );
    }
    
    protected Memory getMemory() {
        return memory;
    }
    
    void write( VulkanLinearMemoryLocation location, Data data ) {
        memory.write( location, data );
    }
    
    void free( VulkanLinearMemoryLocation location ) {
        synchronized ( freeSpace ) {
            var adjacentLocations = freeSpace.stream()
                .sorted( BY_POSITION )
                .filter( location::adjacent )
                .collect( Collectors.toList() )
            ;
            freeSpace.remove( location );
            freeSpace.removeAll( adjacentLocations );
            adjacentLocations.forEach( location::merge );
            freeSpace.add( location );
        }
    }
    
    VulkanLinearMemoryLocation allocate( long length ) {
        synchronized ( freeSpace ) {
            var memoryLocation = freeSpace.stream()
                .sorted( VulkanLinearMemoryLocation::compareTrueLengthTo )
                .filter( Location -> Location.getLength() >= length )
                .min( VulkanLinearMemoryLocation::compareTrueLengthTo )
                .orElseThrow( () -> new OutOfMemoryException( length ) );
    
            var newLocation = memoryLocation.split( length, memoryOffsetAlignment );
            freeSpace.remove( memoryLocation );
            if ( newLocation != null ) {
                freeSpace.add( memoryLocation );
            } else {
                newLocation = memoryLocation;
            }
            return newLocation;
        }
        
    }
    
    private VulkanLinearMemoryLocation newLoc( long offset, long length, long trueLength ) {
        return new VulkanLinearMemoryLocation( offset, length, trueLength );
    }
    
    static class OutOfMemoryException extends RuntimeException {
        
        private OutOfMemoryException( long freeMemory, long length ) {
            super( "Cannot create block of size " + length + ". Free memory left: " + freeMemory );
        }
        private OutOfMemoryException( long length ) {
            super( "Cannot create block of size " + length + "." );
        }
    }
}
