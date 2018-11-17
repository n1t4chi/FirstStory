/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.object.Vertices;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public abstract class VerticesParser<
    VerticesType extends Vertices< ?, ? >,
    PositionType extends Position > extends ParameterParser< VerticesType
> {
    
    @Override
    public VerticesType newInstance( String text ) {
        return newInstance( ParseUtils.toList( text ).stream()
            .map( ParseUtils::toList )
            .map( verticesText -> verticesText.stream()
                .map( this::toPosition )
                .collect( Collectors.toList() )
            )
            .toArray( FirstOracleConstants::array )
        );
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_VERTICES;
    }
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_VERTICES;
    }
    
    protected abstract PositionType toPosition( String text );
    
    protected abstract VerticesType newInstance( List< PositionType >[] positions );
}
