/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ClassBasedParameterParser;
import com.firststory.firstoracle.object.DirectionController;

/**
 * @author n1t4chi
 */
public class DirectionControllerParser
    extends ClassBasedParameterParser< DirectionController >
{
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_CONTROLLER_DIRECITON;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_CONTROLLER_DIRECITON;
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_COMMON;
    }
    
    @Override
    public Class< DirectionController > getSetterParameterClass() {
        return DirectionController.class;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_CONTROLLER_DIRECITON;
    }
    
    @Override
    public Class< DirectionController > getBaseClass() {
        return DirectionController.class;
    }
    
    @Override
    public String getDefaultPackage() {
        return FirstOracleConstants.OBJECT_PACKAGE_NAME;
    }
}
