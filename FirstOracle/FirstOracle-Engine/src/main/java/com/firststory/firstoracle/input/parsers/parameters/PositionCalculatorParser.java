/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ClassBasedParameterParser;

/**
 * @author n1t4chi
 */
public abstract class PositionCalculatorParser< PositionCalculator > extends ClassBasedParameterParser< PositionCalculator > {
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_POSITION_CALCULATOR;
    }
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_POSITION_CALC;
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_COMMON;
    }
}
