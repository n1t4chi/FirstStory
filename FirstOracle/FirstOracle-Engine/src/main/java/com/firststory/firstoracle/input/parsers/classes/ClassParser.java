/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.SceneParser;
import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;

import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public interface ClassParser< Type > {
    
    Logger logger = FirstOracleConstants.getLogger( ClassParser.class );
    
    Class< Type > getBaseClass();
    
    String getDefaultPackage();
    
    default Class< ? extends Type > classForName( String className ) {
        try {
            return SceneParser.class.getClassLoader()
                .loadClass( className )
                .asSubclass( getBaseClass() )
            ;
        } catch ( Exception e1 ) {
            try {
                return ClassParser.class.getClassLoader()
                    .loadClass( getDefaultPackage() + "." + className )
                    .asSubclass( getBaseClass() )
                    ;
            } catch ( Exception e2 ) {
                e2.addSuppressed( e1 );
                throw new ParsedClassNotFoundException( className, getBaseClass(), e2 );
            }
        }
    }
}
