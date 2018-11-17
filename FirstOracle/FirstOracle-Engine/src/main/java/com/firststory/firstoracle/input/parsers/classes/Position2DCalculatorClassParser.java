/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object2D.Position2DCalculator;

/**
 * @author n1t4chi
 */
public class Position2DCalculatorClassParser extends ClassParser< Position2DCalculator > {
    
    @Override
    Class< Position2DCalculator > getBaseClass() {
        return Position2DCalculator.class;
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.POSITION_CALCULATOR_2D_PACKAGE_NAME;
    }
}
