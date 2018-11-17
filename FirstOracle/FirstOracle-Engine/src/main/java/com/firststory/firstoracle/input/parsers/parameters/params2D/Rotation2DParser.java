/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.data.Rotation2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.RotationParser;

/**
 * @author n1t4chi
 */
public class Rotation2DParser extends RotationParser< Rotation2D > {
    
    @Override
    public Class< Rotation2D > getSetterParameterClass() {
        return Rotation2D.class;
    }
    
    @Override
    public Rotation2D newInstance( String text ) {
        return ParseUtils.toRotation2D( text );
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_ROTATIONS_2D;
    }
}
