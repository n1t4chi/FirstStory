/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.structure.Composite;

/**
 * @author n1t4chi
 */
public class SharedTerrains2DParser extends SharedObjectsParser {
    
    public SharedTerrains2DParser( Composite sharedObjectsNode ) {
        super( sharedObjectsNode );
    }
    
    @Override
    String getSharedName() {
        return ParseUtils.SHARED_OBJECTS_TERRAINS_2D;
    }
}
