/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;

/**
 * @author n1t4chi
 */
public abstract class ClassBasedParameterParser< Type > extends ParameterParser<Type> {
    
    protected abstract Class< ? extends Type > getClassForName( String name );
    
    @Override
    public Type newInstance( String text ) {
        try {
            return getClassForName( text )
                .getDeclaredConstructor()
                .newInstance()
                ;
        } catch ( Exception ex ) {
            throw new ParsedClassNotFoundException( text, getSetterParameterClass(), ex );
        }
    }
}
