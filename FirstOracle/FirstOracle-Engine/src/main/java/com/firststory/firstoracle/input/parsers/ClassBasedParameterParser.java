/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;

/**
 * @author n1t4chi
 */
public abstract class ClassBasedParameterParser< Type > extends ParameterParser< Type, Class< ? extends Type > > {
    
    @Override
    public Type unbox( Class< ? extends Type > aClass ) {
        try {
            return aClass
                .getDeclaredConstructor()
                .newInstance()
            ;
        } catch ( Exception ex ) {
            throw new ParsedClassNotFoundException( aClass.getName(), getSetterParameterClass(), ex );
        }
    }
}
