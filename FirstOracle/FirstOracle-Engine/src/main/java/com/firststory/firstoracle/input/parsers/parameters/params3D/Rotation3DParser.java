/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.data.Rotation3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.RotationParser;

/**
 * @author n1t4chi
 */
public class Rotation3DParser extends RotationParser< Rotation3D > {
    
    @Override
    public Class< Rotation3D > getSetterParameterClass() {
        return Rotation3D.class;
    }
    
    @Override
    public Rotation3D newInstance( String text ) {
        return ParseUtils.toRotation3D( text );
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_ROTATIONS_3D;
    }
}
