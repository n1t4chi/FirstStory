/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.shader;

/**
 * @author n1t4chi
 */
public class CannotCreateUniformLocationException extends ShaderException {
    
    public CannotCreateUniformLocationException( String s ) {
        super( "Cannot create uniform location: " + s );
    }
}
