/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params2D;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.PositionParser;

/**
 * @author n1t4chi
 */
public class Position2DParser extends PositionParser< Position2D > {
    
    @Override
    public Class< Position2D > getSetterParameterClass() {
        return Position2D.class;
    }
    
    @Override
    public Position2D newInstance( String text ) {
        return ParseUtils.toPosition2D( text );
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITIONS_2D;
    }
}
