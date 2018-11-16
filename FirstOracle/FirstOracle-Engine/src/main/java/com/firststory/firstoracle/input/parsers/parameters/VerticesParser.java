/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.object.Vertices;

import java.util.List;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.input.ParseUtils.*;

/**
 * @author n1t4chi
 */
public abstract class VerticesParser<
    VerticesType extends Vertices< ?, ? >,
    PositionType extends Position > extends ParameterParser< VerticesType
> {
    
    @Override
    public String getSetterName() {
        return METHOD_SET_VERTICES;
    }
    
    @Override
    public String getParameterName() {
        return SCENE_PARAM_VERTICES;
    }
    
    @Override
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public VerticesType newInstance( String text ) {
        return ( VerticesType ) newInstance( toList( text ).stream()
            .map( ParseUtils::toList )
            .map( verticesText -> verticesText.stream()
                .map( this::toPosition )
                .collect( Collectors.toList() )
            )
            .toArray( List[]::new )
        );
    }
    
    abstract PositionType toPosition( String text );
    
    abstract VerticesType newInstance( List< PositionType >[] positions );
}
