/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ParameterParser;

/**
 * @author n1t4chi
 */
public abstract class ScaleParser< ScaleType extends Scale > extends ParameterParser< ScaleType > {
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_SCALE;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_SCALE;
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_COMMON;
    }
}
