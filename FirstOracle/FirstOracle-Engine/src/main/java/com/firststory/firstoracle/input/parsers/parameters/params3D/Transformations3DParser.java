/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.classes.Transformation3DClassParser;
import com.firststory.firstoracle.input.parsers.parameters.TransformationsParser;
import com.firststory.firstoracle.object3D.Object3DTransformations;

/**
 * @author n1t4chi
 */
public class Transformations3DParser extends TransformationsParser< Object3DTransformations > {
    
    private static final Transformation3DClassParser parser = new Transformation3DClassParser();
    
    @Override
    public Class< Object3DTransformations > getSetterParameterClass() {
        return Object3DTransformations.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_TRANSFORMATIONS_3D;
    }
    
    @Override
    public Class< ? extends Object3DTransformations > newInstance( String name ) {
        return parser.classForName( name );
    }
}
