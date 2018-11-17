/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.object3D.PositionableObject3D;

/**
 * @author n1t4chi
 */
public class Object3DClassParser extends ObjectClassParser< PositionableObject3D< ?, ? > > {
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_OBJECT_CLASSES_3D;
    }
    
    @Override
    @SuppressWarnings( {"unchecked",  "rawtypes"} )
    Class< PositionableObject3D< ?, ? > > getBaseClass() {
        return ( Class< PositionableObject3D< ?, ? > > ) (
            ( Class< ? extends PositionableObject3D > ) PositionableObject3D.class
        );
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_3D_PACKAGE_NAME;
    }
}
