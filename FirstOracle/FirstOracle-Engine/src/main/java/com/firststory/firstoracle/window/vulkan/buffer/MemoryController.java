/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.window.vulkan.buffer.MemoryLocation.BY_TRUE_SIZE;

/**
 * @author n1t4chi
 */
class MemoryController {
    
    private final Memory memory;
    private PriorityQueue< MemoryLocation > freeSpace = new PriorityQueue<>( BY_TRUE_SIZE );
    
    MemoryController( Memory memory ) {
        this.memory = memory;
        MemoryLocation location = newLoc( 0, memory.length(), memory.length() );
        addLocation( location );
    }
    
    private void addLocation( MemoryLocation location ) {
        freeSpace.add( location );
    }
    
    private void removeLocation( MemoryLocation location ) {
        freeSpace.remove( location );
    }
    
    char[] read( MemoryLocation location ) {
        return memory.read( location );
    }
    
    void write( MemoryLocation location, char[] data ) {
        memory.write( location, data );
    }
    
    void free( MemoryLocation location ) {
        
        List< MemoryLocation > adjacentLocations = freeSpace.stream()
            .sorted()
            .filter( location::adjacent )
            .collect( Collectors.toList() )
        ;
        freeSpace.remove( location );
        freeSpace.removeAll( adjacentLocations );
        adjacentLocations.forEach( location::merge );
        freeSpace.add( location );
    }
    
    DataBuffer createBuffer( int length ) {
        MemoryLocation memoryLocation = freeSpace.stream()
            .sorted( MemoryLocation::compareTrueLengthTo )
            .filter( Location -> Location.getLength() >= length )
            .min( MemoryLocation::compareTrueLengthTo )
            .orElseThrow( () -> new OutOfMemoryException( length ) );
        
    
        MemoryLocation newLocation = memoryLocation.split( length );
        removeLocation( memoryLocation );
        if( newLocation != null ) {
            addLocation( memoryLocation );
        } else {
            newLocation = memoryLocation;
        }
        
        return new DataBuffer( this, newLocation );
    }
    
    private MemoryLocation newLoc( int offset, int length, int trueLength ) {
        return new MemoryLocation( offset, length, trueLength );
    }
    
    class OutOfMemoryException extends RuntimeException {
        
        private OutOfMemoryException( int freeMemory, int length ) {
            super( "Cannot create block of size " + length + ". Free memory left: " + freeMemory );
        }
        private OutOfMemoryException( int length ) {
            super( "Cannot create block of size " + length + "." );
        }
    }
}
