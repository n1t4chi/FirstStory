/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.classes.Transformation2DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.TransformationsParser;
import com.firststory.firstoracle.object2D.Object2DTransformations;

/**
 * @author n1t4chi
 */
public class Transformations2DParser extends TransformationsParser< Object2DTransformations > {
    
    private static final Transformation2DClassParser parser = new Transformation2DClassParser();
    
    @Override
    public Class< Object2DTransformations > getSetterParameterClass() {
        return Object2DTransformations.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_TRANSFORMATIONS_2D;
    }
    
    @Override
    protected Class< ? extends Object2DTransformations > getClassForName( String name ) {
        return parser.classForName( name );
    }
}
