/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class Parameter< Type > {
    
    private final Class< Type > typeClass;
    private final String paramterName;
    private final String setterName;
    private final Map< String, Type > sharedInstances = new HashMap<>();
    
    public Parameter(
        Class< Type > typeClass,
        String paramterName,
        String setterName
    ) {
        this.typeClass = typeClass;
        this.paramterName = paramterName;
        this.setterName = setterName;
    }
    
    
}
