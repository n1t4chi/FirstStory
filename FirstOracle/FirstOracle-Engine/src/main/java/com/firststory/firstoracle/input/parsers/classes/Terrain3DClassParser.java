/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.object3D.Terrain3D;

/**
 * @author n1t4chi
 */
public class Terrain3DClassParser extends TerrainClassParser< Terrain3D< ? > > {
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_TERRAIN_CLASSES_3D;
    }
    
    @Override
    @SuppressWarnings( {"unchecked",  "rawtypes"} )
    Class< Terrain3D< ? > > getBaseClass() {
        return ( Class< Terrain3D< ? > > ) ( ( Class< ? extends Terrain3D > ) Terrain3D.class );
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_3D_PACKAGE_NAME;
    }
}
