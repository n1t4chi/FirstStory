/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.structure;

import java.util.Collections;
import java.util.List;

/**
 * @author n1t4chi
 */
public class ImmutableComposite extends Composite {
    
    ImmutableComposite( String name ) {
        super( name );
    }
    
    @Override
    public List< Node > getContent() {
        return Collections.emptyList();
    }
}
