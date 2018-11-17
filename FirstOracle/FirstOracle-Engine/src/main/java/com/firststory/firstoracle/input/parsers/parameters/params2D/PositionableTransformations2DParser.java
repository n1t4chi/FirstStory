/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.PositionableTransformationsParser;
import com.firststory.firstoracle.object2D.PositionableObject2DTransformations;

/**
 * @author n1t4chi
 */
public class PositionableTransformations2DParser extends PositionableTransformationsParser< PositionableObject2DTransformations > {
    
    @Override
    public Class< PositionableObject2DTransformations > getBaseClass() {
        return PositionableObject2DTransformations.class;
    }
    
    @Override
    public String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_2D_PACKAGE_NAME;
    }
    
    @Override
    public Class< PositionableObject2DTransformations > getSetterParameterClass() {
        return PositionableObject2DTransformations.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITIONABLE_TRANSFORMATIONS_2D;
    }
}
