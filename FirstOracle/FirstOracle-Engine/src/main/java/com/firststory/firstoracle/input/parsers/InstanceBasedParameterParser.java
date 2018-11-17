/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

/**
 * @author n1t4chi
 */
public abstract class InstanceBasedParameterParser< Type > extends ParameterParser<Type, Type> {
    
    @Override
    public Type unbox( Type type ) {
        return type;
    }
}
