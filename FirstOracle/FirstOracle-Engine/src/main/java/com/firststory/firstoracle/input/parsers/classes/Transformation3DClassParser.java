/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.Object3DTransformations;

/**
 * @author n1t4chi
 */
public class Transformation3DClassParser extends ClassParser< Object3DTransformations > {
    
    @Override
    Class< Object3DTransformations > getBaseClass() {
        return Object3DTransformations.class;
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_3D_PACKAGE_NAME;
    }
}
