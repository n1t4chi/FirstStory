/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;
import com.firststory.firstoracle.input.parsers.classes.ClassParser;

/**
 * @author n1t4chi
 */
public abstract class ClassBasedParameterParser< Type >
    extends ParameterParser< Type, Class< ? extends Type > >
    implements ClassParser< Type >
{
    @Override
    public Class< ? extends Type > newInstance( String text ) {
        return classForName( text );
    }
    
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
