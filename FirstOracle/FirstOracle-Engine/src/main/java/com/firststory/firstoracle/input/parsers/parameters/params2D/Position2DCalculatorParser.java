/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.PositionCalculatorParser;
import com.firststory.firstoracle.object2D.Position2DCalculator;

/**
 * @author n1t4chi
 */
public class Position2DCalculatorParser extends PositionCalculatorParser< Position2DCalculator > {
    
    @Override
    public Class< Position2DCalculator > getBaseClass() {
        return Position2DCalculator.class;
    }
    
    @Override
    public String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_2D_PACKAGE_NAME;
    }
    
    @Override
    public Class< Position2DCalculator > getSetterParameterClass() {
        return Position2DCalculator.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITION_CALCULATORS_2D;
    }
}
