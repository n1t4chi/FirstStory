/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;
import com.firststory.firstoracle.input.parsers.ParameterParser;

/**
 * @author n1t4chi
 */
public abstract class PositionCalculatorParser< PositionCalculator > extends ParameterParser< PositionCalculator > {
    
    protected abstract Class< ? extends PositionCalculator > getClassForName( String name );
    
    @Override
    public PositionCalculator newInstance( String text ) {
        try {
            return getClassForName( text )
                .getDeclaredConstructor()
                .newInstance()
            ;
        } catch ( Exception ex ) {
            throw new ParsedClassNotFoundException( text, getSetterParameterClass(), ex );
        }
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_POSITION_CALCULATOR;
    }
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_POSITION_CALC;
    }
}
