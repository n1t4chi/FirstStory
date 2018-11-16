/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Position2D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.object2D.Vertices2D;

import java.util.List;

import static com.firststory.firstoracle.input.ParseUtils.SHARED_PARAM_VERTICES_2D;

/**
 * @author n1t4chi
 */
public class Vertices2DParser extends VerticesParser< Vertices2D, Position2D > {
    
    @Override
    public Class< Vertices2D > getTypeClass() {
        return Vertices2D.class;
    }
    
    @Override
    Position2D toPosition( String text ) {
        return Position2D.pos2( ParseUtils.toVec2( text ) );
    }
    
    @Override
    Vertices2D newInstance( List< Position2D >[] positions ) {
        return new Vertices2D( positions );
    }
    
    @Override
    public String getSharedName() {
        return SHARED_PARAM_VERTICES_2D;
    }
}
