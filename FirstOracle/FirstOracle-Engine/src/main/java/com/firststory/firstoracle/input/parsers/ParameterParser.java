/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.exceptions.SharedDataKeyNotFoundException;
import com.firststory.firstoracle.input.structure.Leaf;
import com.firststory.firstoracle.input.structure.Node;

import java.util.HashMap;
import java.util.Map;

import static com.firststory.firstoracle.input.ParseUtils.SHARED_NAME_PREFIX;

/**
 * @author n1t4chi
 */
public abstract class ParameterParser< Type > implements NodeParser< Type, Leaf  > {
    
    private final Map< String, Type > sharedInstances = new HashMap<>();
    
    public abstract Class< Type > getTypeClass();
    
    public abstract String getSetterName();
    
    public abstract Type newInstance( String text );
    
    public abstract String getParameterName();
    
    @Override
    public Type getSharedInstance( String name ) {
        return sharedInstances.get( name );
    }
    
    @Override
    public Type newInstance( Leaf node ) {
        return newInstance( node.getValue() );
    }
    
    public void newSharedInstance( Node node ) {
        if( !node.isComposite() ) {
            var leaf = ( Leaf ) node;
            sharedInstances.put(
                leaf.getName(),
                newInstance( leaf.getValue() )
            );
        }
    }
    
    public void apply(
        Object object,
        Leaf leaf
    ) {
        if( leaf.getValue() != null && getParameterName().equalsIgnoreCase( leaf.getName() ) ) {
            try {
                applyUnsafe( object, leaf );
            } catch ( Exception ex ) {
                throw new ParseFailedException( "Exception while setting up object texture", ex );
            }
        }
    }
    
    
    @Override
    public Type parse( Leaf node ) {
        if( isShared( node ) ) {
            var key = normalizeSharedKey( node.getValue() );
            var instance = getSharedInstance( key );
            if( instance == null ) {
                throw new SharedDataKeyNotFoundException( key, getSharedName() );
            }
            return instance;
        }
        return newInstance( node );
    }
    
    private boolean isShared( Leaf node ) {
        return node.getValue().startsWith( SHARED_NAME_PREFIX );
    }
    
    private String normalizeSharedKey( String key ) {
        return key.substring( 1 );
    }
    
    private void applyUnsafe(
        Object object,
        Leaf leaf
    ) throws Exception {
        object.getClass()
            .getMethod( getSetterName(), getTypeClass() )
            .invoke( object, parse( leaf ) )
        ;
    }
    
}
