/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ParameterParser;

/**
 * @author n1t4chi
 */
public abstract class PositionParser< PositionType extends Position > extends ParameterParser< PositionType > {
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_POSITION;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_POSITION;
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_COMMON;
    }
}
