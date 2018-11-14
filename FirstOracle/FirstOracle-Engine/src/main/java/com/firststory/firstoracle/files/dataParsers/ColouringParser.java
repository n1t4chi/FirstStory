/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.dataParsers;

import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.files.Exceptions.ParseFailedException;
import com.firststory.firstoracle.files.ParseUtils;
import com.firststory.firstoracle.files.SharedData;
import com.firststory.firstoracle.object.Colouring;
import com.firststory.firstoracle.object.GraphicObject;

import java.util.stream.Collectors;

import static com.firststory.firstoracle.files.ParseUtils.METHOD_SET_COLOURING;
import static com.firststory.firstoracle.files.ParseUtils.toList;

/**
 * @author n1t4chi
 */
public interface ColouringParser {
    
    static void setColouring(
        GraphicObject< ?, ?, ? > object,
        SharedData sharedData,
        String colouringText
    ) {
        if( colouringText == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_COLOURING, Colouring.class )
                .invoke( object, newColouring( sharedData, colouringText )
                )
            ;
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    
    static Colouring newColouring( SharedData sharedData, String colouringText ) {
        return ParseUtils.getNewOrShared(
            colouringText,
            sharedData,
            SharedData::getColouring,
            ColouringParser::newColouring
        );
    }
    
    static Colouring newColouring( String colouringText ) {
        return new Colouring( toList( colouringText ).stream()
            .map( ParseUtils::toVec4 )
            .map( Colour::col )
            .collect( Collectors.toList() )
        );
    }
}
