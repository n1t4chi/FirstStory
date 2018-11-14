/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n1t4chi
 */
class MutableComposite extends Composite {
    
    private final List< Node > content = new ArrayList<>();
    
    MutableComposite( String name ) {
        super( name );
    }
    
    @Override
    public List< Node > getContent() {
        return content;
    }
    
    void addContent( Node content ) {
        this.content.add( content );
    }
}
