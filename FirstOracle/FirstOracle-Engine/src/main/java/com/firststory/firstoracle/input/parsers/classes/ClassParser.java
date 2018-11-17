/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.input.SceneParser;
import com.firststory.firstoracle.input.exceptions.ParsedClassNotFoundException;
import com.firststory.firstoracle.input.parsers.NodeParser;
import com.firststory.firstoracle.input.structure.Leaf;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public abstract class ClassParser< Type > implements NodeParser< Class< ? extends Type >, Leaf > {
    
    private static final Logger logger = FirstOracleConstants.getLogger( ClassParser.class );
    
    abstract Class< Type > getBaseClass();
    
    abstract String getDefaultPackage();
    
    @Override
    public Class< ? extends Type > parse( Leaf node ) {
        var className = node.getValue();
        return classForName( className );
    }
    
    public Class< ? extends Type > classForName( String className ) {
        try {
            return SceneParser.class.getClassLoader()
                .loadClass( className )
                .asSubclass( getBaseClass() )
            ;
        } catch ( Exception e1 ) {
            logger.log( Level.WARNING, "Exception while extracting object class " + className, e1 );
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
