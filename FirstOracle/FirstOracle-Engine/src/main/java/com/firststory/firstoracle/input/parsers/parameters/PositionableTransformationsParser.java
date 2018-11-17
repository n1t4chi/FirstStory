/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ClassBasedParameterParser;
import com.firststory.firstoracle.object.PositionableObjectTransformations;

/**
 * @author n1t4chi
 */
public abstract class PositionableTransformationsParser< Transformations extends PositionableObjectTransformations< ?, ?, ? > >
    extends ClassBasedParameterParser< Transformations >
{
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_TRANSFORMATIONS;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_TRANSFORMATIONS;
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_FUNDAMENTAL;
    }
}
