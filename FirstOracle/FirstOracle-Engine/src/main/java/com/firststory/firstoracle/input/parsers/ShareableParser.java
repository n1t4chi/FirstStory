/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.input.structure.Node;

import static com.firststory.firstoracle.input.ParseUtils.SHARED_NAME_PREFIX;

/**
 * @author n1t4chi
 */
public interface ShareableParser< Type > extends NodeParser< Type, Leaf > {
    
    Type newInstance( String text );
    
    String getSharedName();
    
    Type getSharedInstance( String name );
    
    void addSharedInstance(
        String name,
        Type instance
    );
    
    default void parseShared( Composite sharedNode ) {
        sharedNode.findComposite( getSharedName() )
            .getContent()
            .forEach( this::newSharedInstance )
        ;
    }
    
    default void newSharedInstance( Node node ) {
        if( !node.isComposite() ) {
            var leaf = ( Leaf ) node;
            addSharedInstance(
                leaf.getName(),
                newInstance( leaf.getValue() )
            );
        }
    }
    
    default boolean isShared( String text ) {
        return text != null && text.startsWith( SHARED_NAME_PREFIX );
    }
    
    default String normalizeSharedKey( String key ) {
        return key.substring( 1 );
    }
}
