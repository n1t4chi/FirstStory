/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.object;

import com.firststory.firstoracle.data.Index;
import com.firststory.firstoracle.object.Terrain;

import java.util.List;

/**
 * @author n1t4chi
 */
public class TerrainPair< TerrainType extends Terrain< ?,?,?,IndexType,? >, IndexType extends Index > {
    private final TerrainType terrain;
    private final List< IndexType > indices;
    
    public TerrainPair(
        TerrainType terrain,
        List< IndexType > indices
        ) {
        this.terrain = terrain;
        this.indices = indices;
    }
    
    public List< IndexType > getIndices() {
        return indices;
    }
    
    public TerrainType getTerrain() {
        return terrain;
    }
}
