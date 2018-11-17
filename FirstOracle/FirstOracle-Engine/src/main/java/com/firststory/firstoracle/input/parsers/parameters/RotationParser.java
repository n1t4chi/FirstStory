/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.InstanceBasedParameterParser;

/**
 * @author n1t4chi
 */
public abstract class RotationParser< RotationType extends Rotation > extends InstanceBasedParameterParser< RotationType > {
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_ROTATION;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_ROTATION;
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_COMMON;
    }
}
