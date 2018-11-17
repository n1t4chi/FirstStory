/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.object2D.Terrain2D;

/**
 * @author n1t4chi
 */
public class Terrain2DClassParser extends TerrainClassParser< Terrain2D< ? > > {
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_TERRAIN_CLASSES_2D;
    }
    
    @Override
    @SuppressWarnings( {"unchecked",  "rawtypes"} )
    public Class< Terrain2D< ? > > getBaseClass() {
        return ( Class< Terrain2D< ? > > ) ( ( Class< ? extends Terrain2D > ) Terrain2D.class );
    }
    
    @Override
    public String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_2D_PACKAGE_NAME;
    }
}
