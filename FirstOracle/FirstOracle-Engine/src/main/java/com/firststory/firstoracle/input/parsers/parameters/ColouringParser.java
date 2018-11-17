/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.InstanceBasedParameterParser;
import com.firststory.firstoracle.object.Colouring;

import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class ColouringParser extends InstanceBasedParameterParser< Colouring > {
    
    @Override
    public Colouring newInstance( String text ) {
        return new Colouring( ParseUtils.toList( text ).stream()
            .map( ParseUtils::toVec4 )
            .map( Colour::col )
            .collect( Collectors.toList() )
        );
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_FUNDAMENTAL;
    }
    
    @Override
    public Class< Colouring > getSetterParameterClass() {
        return Colouring.class;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_COLOURING;
    }
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_COLOURING;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_COLOURINGS;
    }
}
