/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.structure;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class MutableComposite extends Composite {
    
    private final Map< String, Node > content = new LinkedHashMap<>();
    
    public MutableComposite( String name ) {
        super( name );
    }
    
    public MutableComposite(
        String name,
        Collection< Node > baseContent,
        Collection< Node > overwriteContent
    ) {
        super( name );
        baseContent.forEach( node -> content.put( node.getName(), node ) );
        overwriteContent.forEach( node -> content.put( node.getName(), node ) );
    }
    
    /**
     * Returns mutable content list
     */
    @Override
    public Collection< Node > getContent() {
        return content.values();
    }
    
    public void addContent( Node content ) {
        this.content.put( content.getName(), content );
    }
}
