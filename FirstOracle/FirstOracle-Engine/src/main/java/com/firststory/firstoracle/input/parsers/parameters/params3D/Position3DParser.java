/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters.params3D;

import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.parameters.PositionParser;

/**
 * @author n1t4chi
 */
public class Position3DParser extends PositionParser< Position3D > {
    
    @Override
    public Class< Position3D > getSetterParameterClass() {
        return Position3D.class;
    }
    
    @Override
    public Position3D newInstance( String text ) {
        return ParseUtils.toPosition3D( text );
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_POSITIONS_3D;
    }
}
