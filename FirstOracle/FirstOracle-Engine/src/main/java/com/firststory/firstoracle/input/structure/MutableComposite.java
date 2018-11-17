/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
public class MutableComposite extends Composite {
    
    private final List< Node > content = new ArrayList<>();
    
    public MutableComposite( String name ) {
        super( name );
    }
    
    @Override
    /**
     * Returns mutable content list
     */
    public List< Node > getContent() {
        return content;
    }
    
    public void addContent( Node content ) {
        this.content.add( content );
    }
}
