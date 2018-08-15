/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.buffer;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.window.vulkan.buffer.LinearMemoryLocation.BY_TRUE_SIZE;

/**
 * @author n1t4chi
 */
class LinearMemoryController< Data > {
    
    private final LinearMemory< Data > memory;
    private PriorityQueue< LinearMemoryLocation > freeSpace = new PriorityQueue<>( BY_TRUE_SIZE );
    
    LinearMemoryController( LinearMemory< Data > memory ) {
        this.memory = memory;
        LinearMemoryLocation location = newLoc( 0, memory.length(), memory.length() );
        addLocation( location );
    }
    
    protected LinearMemory< Data > getMemory() {
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
        
        List< LinearMemoryLocation > adjacentLocations = freeSpace.stream()
            .sorted()
            .filter( location::adjacent )
            .collect( Collectors.toList() )
        ;
        removeLocation( location );
        freeSpace.removeAll( adjacentLocations );
        adjacentLocations.forEach( location::merge );
        addLocation( location );
    }
    
    DataBufferInLinearMemory< Data > createBuffer( int length ) {
        LinearMemoryLocation memoryLocation = freeSpace.stream()
            .sorted( LinearMemoryLocation::compareTrueLengthTo )
            .filter( Location -> Location.getLength() >= length )
            .min( LinearMemoryLocation::compareTrueLengthTo )
            .orElseThrow( () -> new OutOfMemoryException( length ) );
        
    
        LinearMemoryLocation newLocation = memoryLocation.split( length );
        removeLocation( memoryLocation );
        if( newLocation != null ) {
            addLocation( memoryLocation );
        } else {
            newLocation = memoryLocation;
        }
        
        return new DataBufferInLinearMemory<>( this, newLocation );
    }
    
    private LinearMemoryLocation newLoc( int offset, int length, int trueLength ) {
        return new LinearMemoryLocation( offset, length, trueLength );
    }
    
    static class OutOfMemoryException extends RuntimeException {
        
        private OutOfMemoryException( int freeMemory, int length ) {
            super( "Cannot create block of size " + length + ". Free memory left: " + freeMemory );
        }
        private OutOfMemoryException( int length ) {
            super( "Cannot create block of size " + length + "." );
        }
    }
}
