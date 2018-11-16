/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.object3D.Vertices3D;

import java.util.List;

import static com.firststory.firstoracle.input.ParseUtils.SHARED_PARAM_VERTICES_3D;

/**
 * @author n1t4chi
 */
public class Vertices3DParser extends VerticesParser< Vertices3D, Position3D > {
    
    @Override
    public Class< Vertices3D > getTypeClass() {
        return Vertices3D.class;
    }
    
    @Override
    Position3D toPosition( String text ) {
        return Position3D.pos3( ParseUtils.toVec3( text ) );
    }
    
    @Override
    Vertices3D newInstance( List< Position3D >[] positions ) {
        return new Vertices3D( positions );
    }
    
    @Override
    public String getSharedName() {
        return SHARED_PARAM_VERTICES_3D;
    }
}
