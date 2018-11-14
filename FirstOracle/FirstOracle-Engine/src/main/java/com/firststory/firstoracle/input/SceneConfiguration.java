/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.input.structure.Composite;

import static com.firststory.firstoracle.input.ParseUtils.*;

/**
 * @author n1t4chi
 */
public class SceneConfiguration {
    
    private Index2D terrainShift2D = FirstOracleConstants.INDEX_ZERO_2I;
    private Index3D terrainShift3D = FirstOracleConstants.INDEX_ZERO_3I;
    
    private Index2D terrainSize2D = null;
    private Index3D terrainSize3D = null;
    
    public SceneConfiguration( Composite configNode ) {
        setTerrainShift2D( configNode.findValue( CONFIGURATION_TERRAIN_SHIFT_2D, null ) );
        setTerrainShift3D( configNode.findValue( CONFIGURATION_TERRAIN_SHIFT_3D, null ) );
        setTerrainSize2D( configNode.findValue( CONFIGURATION_TERRAIN_SIZE_2D, null ) );
        setTerrainSize3D( configNode.findValue( CONFIGURATION_TERRAIN_SIZE_3D, null ) );
    }
    
    private void setTerrainShift2D( String text ) {
        if( text != null ) {
            this.terrainShift2D = ParseUtils.toIndex2D( text );
        }
    }
    
    private void setTerrainShift3D( String text ) {
        if( text != null ) {
            this.terrainShift3D = ParseUtils.toIndex3D( text );
        }
    }
    
    private void setTerrainSize2D( String text ) {
        if( text != null ) {
            this.terrainSize2D = ParseUtils.toIndex2D( text );
        }
    }
    
    private void setTerrainSize3D( String text ) {
        if( text != null ) {
            this.terrainSize3D = ParseUtils.toIndex3D( text );
        }
    }
    
    public Index2D getTerrainShift2D() {
        return terrainShift2D;
    }
    
    public Index3D getTerrainShift3D() {
        return terrainShift3D;
    }
    
    public Index2D getTerrainSize2D() {
        return terrainSize2D;
    }
    
    public Index3D getTerrainSize3D() {
        return terrainSize3D;
    }
}
