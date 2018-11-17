/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.parsers.InstanceBasedParameterParser;
import com.firststory.firstoracle.object.Texture;

import java.util.List;
/**
 * @author n1t4chi
 */
public class TextureParser extends InstanceBasedParameterParser< Texture > {
    
    @Override
    public Texture newInstance( String text ) {
        try {
            var strings = ParseUtils.toList( text );
            if( strings.size() == 1 ) {
                return Texture.create( strings.get( 0 ) );
            } else if( strings.size() == 3) {
                return Texture.createCompound(
                    strings.get( 0 ),
                    toInt( strings, 1 ),
                    toInt( strings, 2 )
                );
            } else if( strings.size() == 5) {
                return Texture.create(
                    strings.get( 0 ),
                    toInt( strings, 1 ),
                    toInt( strings, 2 ),
                    toInt( strings, 3 ),
                    toInt( strings, 4 )
                );
            }
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
        throw new ParseFailedException( "Cannot parse " + text + " to texture. " +
            "Acceptable Formats:\n" +
            "\t[texturePath]\n" +
            "\t[texturePath, directions, frames, columns, rows]\n" +
            "\t[compundTexturePathMask, directions, frames]\n"
        );
    }
    
    public int toInt( List< String > strings, int i ) {
        return Integer.parseInt( strings.get( i ) );
    }
    
    @Override
    public int getPriority() {
        return ParseUtils.PRIORITY_FUNDAMENTAL;
    }
    
    @Override
    public Class< Texture > getSetterParameterClass() {
        return Texture.class;
    }
    
    @Override
    public String getSetterName() {
        return ParseUtils.METHOD_SET_TEXTURE;
    }
    
    @Override
    public String getParameterName() {
        return ParseUtils.SCENE_PARAM_TEXTURE;
    }
    
    @Override
    public String getSharedName() {
        return ParseUtils.SHARED_PARAM_TEXTURES;
    }
}
