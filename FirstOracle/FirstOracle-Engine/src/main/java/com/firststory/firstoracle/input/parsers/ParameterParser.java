/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.exceptions.ParseFailedException;
import com.firststory.firstoracle.input.exceptions.SharedDataKeyNotFoundException;
import com.firststory.firstoracle.input.structure.Composite;
import com.firststory.firstoracle.input.structure.Leaf;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public abstract class ParameterParser< Type, ShareableContainer > implements ShareableParser< Type, ShareableContainer >, Comparable< ParameterParser<?, ?> > {
    
    private final Map< String, ShareableContainer > sharedInstances = new HashMap<>();
    
    public abstract int getPriority();
    
    public abstract Class< Type > getSetterParameterClass();
    
    public abstract String getParameterName();
    
    public abstract String getSetterName();
    
    public abstract Type unbox( ShareableContainer container );
    
    @Override
    public int compareTo( @NotNull ParameterParser< ?, ? > o ) {
        return Integer.compare( getPriority(), o.getPriority() );
    }
    
    @Override
    public ShareableContainer getSharedInstance( String name ) {
        return sharedInstances.get( normalizeSharedKey( name ) );
    }
    
    @Override
    public void addSharedInstance( String name, ShareableContainer instance ) {
        sharedInstances.put( name, instance );
    }
    
    public void tryToApply( Object object, Composite objectComposite ) {
        apply( object, objectComposite.findLeaf( getParameterName(), null ) );
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
        if( isShared( node.getValue() ) ) {
            var instance = getSharedInstance( node.getValue() );
            if( instance == null ) {
                throw new SharedDataKeyNotFoundException( node.getValue(), getSharedName() );
            }
            return unbox( instance );
        }
        return unbox( newInstance( node.getValue() ) );
    }
    
    private void applyUnsafe(
        Object object,
        Leaf leaf
    ) throws Exception {
        var aClass = object.getClass();
        var setterName = getSetterName();
        var parameterClass = getSetterParameterClass();
        var parse = parse( leaf );
        var method = getMethod( aClass, setterName, parameterClass );
        method.invoke( object, parse );
    }
    
    private Method getMethod(
        Class< ? > aClass,
        String setterName,
        Class< Type > parameterClass
    ) throws NoSuchMethodException {
        var methods = aClass.getMethods();
        for ( var method : methods ) {
            if( setterName.equals( method.getName() ) ) {
                try {
                    var parameters = method.getParameters();
                    if ( parameters.length == 1 ) {
                        var parameter = parameters[ 0 ];
                        parameterClass.asSubclass( parameter.getType() );
                        return method;
                    }
                } catch ( ClassCastException ignored ) {}
            }
        }
        throw new NoSuchMethodException(
            "No method " + setterName +
            " with related parameter class " + parameterClass +
            " was found for class " + aClass  );
    }
    
}
