/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.parameters;

import com.firststory.firstoracle.input.ParseUtils;
import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.parsers.ParameterParser;
import com.firststory.firstoracle.object.Texture;

/**
 * @author n1t4chi
 */
public class TextureParser extends ParameterParser< Texture > {
    
    @Override
    public Texture newInstance( String text ) {
        try {
            return Texture.create( text );
        } catch ( Exception ex ) {
            throw new ParseFailedException( "Exception while setting up object texture", ex );
        }
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
