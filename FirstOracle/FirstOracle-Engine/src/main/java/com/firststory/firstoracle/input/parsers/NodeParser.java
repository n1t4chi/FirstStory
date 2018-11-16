/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.Node;

/**
 * @author n1t4chi
 */
public interface NodeParser< Type, NodeType extends Node > {
    
    Type getSharedInstance( String key );
    
    Type newInstance( NodeType node );
    
    void newSharedInstance( Node childNode );
    
    String getSharedName();
    
    Type parse( NodeType node );
    
    default void parseShared( Composite sharedNode ) {
        sharedNode.findComposite( getSharedName() )
            .getContent()
            .forEach( this::newSharedInstance )
        ;
    }
}
