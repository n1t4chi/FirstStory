/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.PositionableObject3DTransformations;

/**
 * @author n1t4chi
 */
public class PositionableTransformation3DClassParser extends ClassParser< PositionableObject3DTransformations > {
    
    @Override
    Class< PositionableObject3DTransformations > getBaseClass() {
        return PositionableObject3DTransformations.class;
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_3D_PACKAGE_NAME;
    }
}
