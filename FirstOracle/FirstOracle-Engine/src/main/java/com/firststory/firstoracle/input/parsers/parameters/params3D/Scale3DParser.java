/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.data.Scale3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.ScaleParser;

/**
 * @author n1t4chi
 */
public class Scale3DParser extends ScaleParser< Scale3D > {
    
    @Override
    public Class< Scale3D > getSetterParameterClass() {
        return Scale3D.class;
    }
    
    @Override
    public Scale3D newInstance( String text ) {
        return ParseUtils.toScale3D( text );
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_SCALES_3D;
    }
}
