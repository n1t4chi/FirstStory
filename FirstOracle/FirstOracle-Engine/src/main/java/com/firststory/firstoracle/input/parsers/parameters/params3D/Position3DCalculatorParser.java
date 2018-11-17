/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.classes.Position3DCalculatorClassParser;
import com.firststory.firstoracle.input.parsers.parameters.PositionCalculatorParser;
import com.firststory.firstoracle.object3D.Position3DCalculator;

/**
 * @author n1t4chi
 */
public class Position3DCalculatorParser extends PositionCalculatorParser< Position3DCalculator > {
    
    private static Position3DCalculatorClassParser classParser = new Position3DCalculatorClassParser();
    
    @Override
    public Class< Position3DCalculator > getSetterParameterClass() {
        return Position3DCalculator.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITION_CALCULATORS_3D;
    }
    
    @Override
    public Class< ? extends Position3DCalculator > newInstance( String name ) {
        return classParser.classForName( name );
    }
}
