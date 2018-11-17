/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.PositionableObject2DTransformations;

/**
 * @author n1t4chi
 */
public class PositionableTransformation2DClassParser extends ClassParser< PositionableObject2DTransformations > {
    
    @Override
    Class< PositionableObject2DTransformations > getBaseClass() {
        return PositionableObject2DTransformations.class;
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_2D_PACKAGE_NAME;
    }
}
