/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.exceptions.SharedObjectKeyNotFoundException;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.MutableComposite;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public abstract class SharedObjectsParser {
    private final Map< String, Composite > instances = new HashMap<>(  );
    
    SharedObjectsParser( Composite sharedObjectsNode ) {
        parseSharedNode( sharedObjectsNode );
    }
    
    abstract String getSharedName();
    
    public Composite parseNode( Composite composite ) {
        var base = composite.findValue( ParseUtils.SHARED_OBJECTS_PARAMETER, null );
        if( base == null ) {
            return composite;
        }
        var baseName = ParseUtils.normalizeSharedKey( base );
        
        var baseNode = instances.get( baseName );
        if( baseNode == null ) {
            throw new SharedObjectKeyNotFoundException( base, getSharedName() );
        }
    
        return new MutableComposite(
            composite.getName(),
            baseNode.getContent(),
            composite.getContent()
        );
    }
    
    private void parseSharedNode( Composite sharedObjectsNode ) {
        var objects = sharedObjectsNode.findComposite( getSharedName() );
        objects.getComposites().forEach( node -> instances.put( node.getName(), node ) );
    }
}
