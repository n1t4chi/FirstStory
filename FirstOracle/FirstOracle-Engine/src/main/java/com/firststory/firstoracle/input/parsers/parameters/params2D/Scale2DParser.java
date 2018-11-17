/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.data.Scale2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.ScaleParser;

/**
 * @author n1t4chi
 */
public class Scale2DParser extends ScaleParser< Scale2D > {
    
    @Override
    public Class< Scale2D > getSetterParameterClass() {
        return Scale2D.class;
    }
    
    @Override
    public Scale2D newInstance( String text ) {
        return ParseUtils.toScale2D( text );
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_SCALES_2D;
    }
}
