/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.SharedData;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.object.GraphicObject;
import com.firststory.firstoracle.object.Texture;

import static com.firststory.firstoracle.input.ParseUtils.METHOD_SET_TEXTURE;

/**
 * @author n1t4chi
 */
public interface TextureParser {
    
    static void setTexture(
        GraphicObject< ?, ?, ? > object,
        SharedData sharedData,
        String textureName
    ) {
        if( textureName == null ) {
            return;
        }
        try {
            object.getClass()
                .getMethod( METHOD_SET_TEXTURE, Texture.class )
                .invoke( object, newTexture( sharedData, textureName ) );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
    static Texture newTexture( SharedData sharedData, String textureName ) {
        return ParseUtils.getNewOrShared(
            textureName,
            sharedData,
            SharedData::getTexture,
            TextureParser::newTexture
        );
    }
    
    static Texture newTexture( String textureName ) {
        try {
            return Texture.create( textureName );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
    }
}
