/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers.classes;

import com.firststory.firstoracle.input.exceptions.SharedDataKeyNotFoundException;
import com.firststory.firstoracle.input.parsers.ShareableParser;
import com.firststory.firstoracle.input.structure.Leaf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public abstract class ShareableClassParser< Type >
    extends ClassParser< Type >
    implements ShareableParser< Class< ? extends Type>, Class< ? extends Type> >
{
    private final Map< String, Class< ? extends Type> > sharedInstances = new HashMap<>();
    
    @Override
    public Class< ? extends Type> getSharedInstance( String name ) {
        return sharedInstances.get( normalizeSharedKey( name ) );
    }
    
    @Override
    public void addSharedInstance( String name, Class< ? extends Type> instance ) {
        sharedInstances.put( name, instance );
    }
    
    @Override
    public Class< ? extends Type > newInstance( String text ) {
        return classForName( text );
    }
    
    public Class< ? extends Type > parse( Leaf node ) {
        return parse( node.getValue() );
    }
    
    public Class< ? extends Type > parse( String text ) {
        if( isShared( text ) ) {
            var instance = getSharedInstance( text );
            if( instance == null ) {
                throw new SharedDataKeyNotFoundException( text, getSharedName() );
            }
            return instance;
        }
        return classForName( text );
    }
}
