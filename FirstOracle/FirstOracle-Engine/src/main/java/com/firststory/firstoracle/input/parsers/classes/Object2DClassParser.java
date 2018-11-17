/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.object2D.PositionableObject2D;

/**
 * @author n1t4chi
 */
public class Object2DClassParser extends ObjectClassParser< PositionableObject2D< ?, ? > > {
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_OBJECT_CLASSES_2D;
    }
    
    @Override
    @SuppressWarnings( {"unchecked",  "rawtypes"} )
    public Class< PositionableObject2D< ?, ? > > getBaseClass() {
        return ( Class< PositionableObject2D< ?, ? > > ) (
            ( Class< ? extends PositionableObject2D > ) PositionableObject2D.class
        );
    }
    
    @Override
    public String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_2D_PACKAGE_NAME;
    }
}
