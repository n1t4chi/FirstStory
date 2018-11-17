/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.input.structure.Composite;

import java.util.List;

/**
 * @author n1t4chi
 */
public class ObjectPair< PositionType extends Position > {
    private final Composite objectNode;
    private final List< PositionType > positions;
    
    public ObjectPair(
        Composite objectNode,
        List< PositionType > positions
    ) {
        this.objectNode = objectNode;
        this.positions = positions;
    }
    
    public List< PositionType > getPositions() {
        return positions;
    }
    
    public Composite getObjectNode() {
        return objectNode;
    }
}
