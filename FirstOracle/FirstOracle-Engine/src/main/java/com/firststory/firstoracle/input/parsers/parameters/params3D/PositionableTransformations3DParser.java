/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.classes.PositionableTransformation3DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.PositionableTransformationsParser;
import com.firststory.firstoracle.object3D.PositionableObject3DTransformations;

/**
 * @author n1t4chi
 */
public class PositionableTransformations3DParser extends PositionableTransformationsParser< PositionableObject3DTransformations > {
    
    private static final PositionableTransformation3DClassParser parser = new PositionableTransformation3DClassParser();
    
    @Override
    public Class< PositionableObject3DTransformations > getSetterParameterClass() {
        return PositionableObject3DTransformations.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITIONABLE_TRANSFORMATIONS_3D;
    }
    
    @Override
    protected Class< ? extends PositionableObject3DTransformations > getClassForName( String name ) {
        return parser.classForName( name );
    }
}
