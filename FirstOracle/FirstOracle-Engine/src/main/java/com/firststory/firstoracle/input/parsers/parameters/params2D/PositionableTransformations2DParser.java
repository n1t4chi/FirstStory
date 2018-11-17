/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.classes.PositionableTransformation2DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.PositionableTransformationsParser;
import com.firststory.firstoracle.object2D.PositionableObject2DTransformations;

/**
 * @author n1t4chi
 */
public class PositionableTransformations2DParser extends PositionableTransformationsParser< PositionableObject2DTransformations > {
    
    private static final PositionableTransformation2DClassParser parser = new PositionableTransformation2DClassParser();
    
    @Override
    public Class< PositionableObject2DTransformations > getSetterParameterClass() {
        return PositionableObject2DTransformations.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITIONABLE_TRANSFORMATIONS_2D;
    }
    
    @Override
    protected Class< ? extends PositionableObject2DTransformations > getClassForName( String name ) {
        return parser.classForName( name );
    }
}
