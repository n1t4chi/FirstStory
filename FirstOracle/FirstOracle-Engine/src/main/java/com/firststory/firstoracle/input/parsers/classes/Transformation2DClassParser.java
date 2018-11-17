/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Object2DTransformations;

/**
 * @author n1t4chi
 */
public class Transformation2DClassParser extends ClassParser< Object2DTransformations > {
    
    @Override
    Class< Object2DTransformations > getBaseClass() {
        return Object2DTransformations.class;
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_2D_PACKAGE_NAME;
    }
}
