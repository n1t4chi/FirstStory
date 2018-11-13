/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.dataParsers;

import com.firststory.firstoracle.data.UV;
import com.firststory.firstoracle.files.Exceptions.ParseFailedException;
import com.firststory.firstoracle.files.ParseUtils;
import com.firststory.firstoracle.files.SharedData;
import com.firststory.firstoracle.object.PositionableObject;
import com.firststory.firstoracle.object.UvMap;

import java.util.List;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.files.ParseUtils.METHOD_SET_UV_MAP;
import static com.firststory.firstoracle.files.ParseUtils.toList;

/**
 * @author n1t4chi
 */
public interface UvMapParser {
    
    static void setUvMap(
        PositionableObject< ?, ?, ? > object,
        SharedData sharedData,
        String uvText
    ) {
        if( uvText == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_UV_MAP, UvMap.class )
                .invoke( object, newUvMap( sharedData, uvText ) )
            ;
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    static UvMap newUvMap( SharedData sharedData, String uvText ) {
        return ParseUtils.getNewOrShared(
            uvText,
            sharedData,
            SharedData::getUvMap,
            UvMapParser::newUvMap
        );
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    static UvMap newUvMap( String uvText ) {
        try {
            return new UvMap( toList( uvText ).stream()
                .map( ParseUtils::toList )
                .map( uvText1 -> ( List< UV >[]) uvText1.stream()
                    .map( ParseUtils::toList )
                    .map( uvTetxt2 -> uvTetxt2.stream()
                        .map( ParseUtils::toVec2 )
                        .map( UV::uv )
                        .collect( Collectors.toList() )
                    )
                    .toArray( List[]::new )
                )
                .toArray( List[][]::new )
            );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
}
