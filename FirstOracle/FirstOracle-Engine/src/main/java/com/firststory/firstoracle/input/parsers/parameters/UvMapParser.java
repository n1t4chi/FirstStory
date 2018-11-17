/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.UV;
import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.object.UvMap;

import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class UvMapParser extends ParameterParser< UvMap > {
    
    @Override
    public UvMap newInstance( String text ) {
        return new UvMap( ParseUtils.toList( text ).stream()
            .map( ParseUtils::toList )
            .map( uvText1 -> uvText1.stream()
                .map( ParseUtils::toList )
                .map( uvTetxt2 -> uvTetxt2.stream()
                    .map( ParseUtils::toVec2 )
                    .map( UV::uv )
                    .collect( Collectors.toList() )
                )
                .toArray( FirstOracleConstants::array )
            )
            .toArray( FirstOracleConstants::array2D )
        );
    }
    
    @Override
    public Class< UvMap > getSetterParameterClass() {
        return UvMap.class;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_UV_MAP;
    }
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_UV_MAP;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_UV_MAPS;
    }
}
