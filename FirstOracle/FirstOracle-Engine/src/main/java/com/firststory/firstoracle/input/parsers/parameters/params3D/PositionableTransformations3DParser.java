/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.PositionableTransformationsParser;
import com.firststory.firstoracle.object3D.PositionableObject3DTransformations;

/**
 * @author n1t4chi
 */
public class PositionableTransformations3DParser extends PositionableTransformationsParser< PositionableObject3DTransformations > {
    
    @Override
    public Class< PositionableObject3DTransformations > getBaseClass() {
        return PositionableObject3DTransformations.class;
    }
    
    @Override
    public String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_3D_PACKAGE_NAME;
    }
    
    @Override
    public Class< PositionableObject3DTransformations > getSetterParameterClass() {
        return PositionableObject3DTransformations.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITIONABLE_TRANSFORMATIONS_3D;
    }
}
