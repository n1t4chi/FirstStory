/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.buffer;

import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.vulkan.buffer.LinearMemoryLocation.BY_TRUE_SIZE;

/**
 * @author n1t4chi
 */
class LinearMemoryController< Memory extends LinearMemory< Data >, Data > {
    
    private final long memoryOffsetAlignment;
    private final Memory memory;
    private final PriorityQueue< LinearMemoryLocation > freeSpace = new PriorityQueue<>( BY_TRUE_SIZE );
    
    LinearMemoryController( Memory memory, long memoryOffsetAlignment ) {
        this.memoryOffsetAlignment = memoryOffsetAlignment;
        this.memory = memory;
        var location = newLoc( 0, memory.length(), memory.length() );
        addLocation( location );
    }
    
    protected Memory getMemory() {
        return memory;
    }
    
    private void addLocation( LinearMemoryLocation location ) {
        freeSpace.add( location );
    }
    
    private void removeLocation( LinearMemoryLocation location ) {
        freeSpace.remove( location );
    }
    
    void write( LinearMemoryLocation location, Data data ) {
        memory.write( location, data );
    }
    
    void free( LinearMemoryLocation location ) {
    
        var adjacentLocations = freeSpace.stream()
            .sorted()
            .filter( location::adjacent )
            .collect( Collectors.toList() )
        ;
        removeLocation( location );
        freeSpace.removeAll( adjacentLocations );
        adjacentLocations.forEach( location::merge );
        addLocation( location );
    }
    
    LinearMemoryLocation allocate( long length ) {
        var memoryLocation = freeSpace.stream()
            .sorted( LinearMemoryLocation::compareTrueLengthTo )
            .filter( Location -> Location.getLength() >= length )
            .min( LinearMemoryLocation::compareTrueLengthTo )
            .orElseThrow( () -> new OutOfMemoryException( length ) );
    
        var newLocation = memoryLocation.split( length, memoryOffsetAlignment );
        removeLocation( memoryLocation );
        if( newLocation != null ) {
            addLocation( memoryLocation );
        } else {
            newLocation = memoryLocation;
        }
        
        return newLocation;
    }
    
    private LinearMemoryLocation newLoc( long offset, long length, long trueLength ) {
        return new LinearMemoryLocation( offset, length, trueLength );
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
