/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.input.parsers.object.SharedObjects2DParser;
import com.firststory.firstoracle.input.parsers.object.SharedObjects3DParser;
import com.firststory.firstoracle.input.parsers.object.SharedTerrains2DParser;
import com.firststory.firstoracle.input.parsers.object.SharedTerrains3DParser;
import com.firststory.firstoracle.input.structure.Composite;

/**
 * @author n1t4chi
 */
public class SharedObjects {
    private final SharedObjects2DParser sharedObjects2DParser;
    private final SharedTerrains2DParser sharedTerrains2DParser;
    private final SharedObjects3DParser sharedObjects3DParser;
    private final SharedTerrains3DParser sharedTerrains3DParser;
    
    SharedObjects( Composite sharedObjectsNode ) {
        sharedObjects2DParser = new SharedObjects2DParser( sharedObjectsNode );
        sharedTerrains2DParser = new SharedTerrains2DParser( sharedObjectsNode );
        sharedObjects3DParser = new SharedObjects3DParser( sharedObjectsNode );
        sharedTerrains3DParser = new SharedTerrains3DParser( sharedObjectsNode );
    }
    
    public SharedObjects2DParser getSharedObjects2DParser() {
        return sharedObjects2DParser;
    }
    
    public SharedTerrains2DParser getSharedTerrains2DParser() {
        return sharedTerrains2DParser;
    }
    
    public SharedObjects3DParser getSharedObjects3DParser() {
        return sharedObjects3DParser;
    }
    
    public SharedTerrains3DParser getSharedTerrains3DParser() {
        return sharedTerrains3DParser;
    }
}
