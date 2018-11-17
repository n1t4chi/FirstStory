/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.exceptions.SharedObjectKeyNotFoundException;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.MutableComposite;
import com.firststory.firstoracle.input.structure.Node;

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
        
        var newComposite = new MutableComposite( composite.getName() );
        var newMutableContent = newComposite.getContent();
        HashMap< String, Node > nodes = new HashMap<>();
        
        baseNode.getContent().forEach( node -> nodes.put( node.getName(), node ) );
        composite.getContent().forEach( node -> nodes.put( node.getName(), node ) );
        newMutableContent.addAll( nodes.values() );
        
        return newComposite;
    }
    
    private void parseSharedNode( Composite sharedObjectsNode ) {
        var objects = sharedObjectsNode.findComposite( getSharedName() );
        objects.getComposites().forEach( node -> {
            instances.put( node.getName(), node );
        } );
    }
}
