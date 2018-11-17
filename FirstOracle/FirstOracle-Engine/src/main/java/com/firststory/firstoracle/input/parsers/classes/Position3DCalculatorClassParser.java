/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object3D.Position3DCalculator;

/**
 * @author n1t4chi
 */
public class Position3DCalculatorClassParser extends ClassParser< Position3DCalculator > {
    
    @Override
    Class< Position3DCalculator > getBaseClass() {
        return Position3DCalculator.class;
    }
    
    @Override
    String getDefaultPackage() {
        return FirstOracleConstants.POSITION_CALCULATOR_3D_PACKAGE_NAME;
    }
}
