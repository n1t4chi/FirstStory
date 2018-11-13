/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files.structure;

import com.firststory.firstoracle.files.Exceptions.NoEntryFoundException;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.firststory.firstoracle.files.ParseUtils.indent;

/**
 * @author n1t4chi
 */
public abstract class Composite extends Node {
    
    public Composite( String name ) {
        super( name );
    }
    
    public abstract List< Node > getContent();
    
    public List< Composite > getComposites() {
        return getContent().stream()
            .filter( Node::isComposite )
            .map( Composite.class::cast )
            .collect( Collectors.toList())
        ;
    }
    
    public List< Leaf > getLeafs() {
        return getContent().stream()
            .filter( Node::isLeaf )
            .map( Leaf.class::cast )
            .collect( Collectors.toList())
        ;
    }
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    public Composite findComposite( String name ) {
        var node = find( name, () -> new ImmutableComposite( name ) );
        return node.isComposite() ? (Composite) node : null;
    }
    
    public String findValue( String name, String defaultValue ) {
        return findLeaf( name, defaultValue).getValue();
    }
    
    public Leaf findLeaf( String name, String defaultValue ) {
        var node = find( name, () -> new Leaf( name, defaultValue ) );
        return node.isComposite() ? null : (Leaf)node;
    }
    
    public Leaf findLeafOrThrow( String name ) {
        var node = findOrThrow( name );
        return node.isComposite() ? null : (Leaf)node;
    }
    
    private Node findOrThrow( String name ) {
        return find( name ).orElseThrow( () -> new NoEntryFoundException( this, name ) );
    }
    
    private Node find( String name, Supplier< Node > supplier ) {
        return find( name ).orElseGet( supplier );
    }
    
    private Optional< Node > find( String name ) {
        return getContent().stream()
            .filter( root -> name.equals( root.getName() ) )
            .findFirst()
        ;
    }
    
    @Override
    public String toString() {
        var values = new StringBuilder();
        getContent().forEach( node -> values.append( indent( "\n" + node ) ) );
        return getName() + " -> {" + values + "\n}";
    }
    
}
