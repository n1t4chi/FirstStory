/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.structure;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.firststory.firstoracle.input.Exceptions.NoMatchingEndTagException;
import com.firststory.firstoracle.input.Exceptions.NoMatchingStartTagException;
import com.firststory.firstoracle.input.Exceptions.OrphanedEntryNodeException;
import com.firststory.firstoracle.input.Exceptions.ParseFailedException;
import com.firststory.firstoracle.input.ParseUtils;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author n1t4chi
 */
public class Roots {
    
    public static Roots parse( String text ) {
        return new Roots( text );
    }
    
    private final List< Composite > roots = new ArrayList<>();
    
    public Roots() {
    }
    
    private Roots( String text ) {
        try {
            var parser = new JsonFactory().createParser( text );
            Deque< MutableComposite > prevComposites = new ArrayDeque<>();
            MutableComposite currentComposite = null;
            JsonToken token;
            while ( ( token = parser.nextToken() ) != null ) {
                currentComposite = handleToken(
                    parser,
                    prevComposites,
                    currentComposite,
                    token
                );
            }
            if ( !prevComposites.isEmpty() ) {
                throw new NoMatchingEndTagException();
            }
        } catch ( ParseFailedException ex ) {
            throw ex;
        } catch ( Exception ex ) {
            throw new ParseFailedException( ex );
        }
    }
    
    @Override
    public String toString() {
        var values = new StringBuilder();
        roots.forEach( node -> values.append( ParseUtils.indent( "\n" + node ) ) );
        return "root {" + values + "\n}";
    }
    
    public List< Composite > getRoots() {
        return roots;
    }
    
    public Composite find( String name ) {
        return roots.stream()
            .filter( root -> name.equals( root.getName() ) )
            .findFirst()
            .orElseGet( ()-> new ImmutableComposite( name ) )
        ;
    }
    
    private void add( Composite root ) {
        roots.add( root );
    }
    
    private MutableComposite handleToken(
        JsonParser parser,
        Deque< MutableComposite > prevComposites,
        MutableComposite currentComposite,
        JsonToken token
    ) throws IOException {
        var returnedComposite = currentComposite;
//        System.err.println( token.name() + " -> " + parser.getCurrentName() + " -> " + parser.getText() );
        switch ( token ) {
            case START_OBJECT:
                returnedComposite = handleStartObject(
                    parser,
                    prevComposites,
                    currentComposite
                );
                break;
            case END_OBJECT:
                returnedComposite = handleEndObject(
                    parser,
                    prevComposites,
                    currentComposite
                );
                break;
            case VALUE_STRING:
                handleValue(
                    parser,
                    currentComposite
                );
                break;
        }
        return returnedComposite;
    }
    
    private void handleValue(
        JsonParser parser,
        MutableComposite currentComposite
    ) throws IOException {
        if ( currentComposite == null ) {
            throw new OrphanedEntryNodeException();
        }
        currentComposite.addContent( new Leaf(
            parser.getCurrentName(),
            parser.getText()
        ) );
    }
    
    private MutableComposite handleEndObject(
        JsonParser parser,
        Deque< MutableComposite > prevComposites,
        MutableComposite currentComposite
    ) throws IOException {
        if ( parser.getCurrentName() != null ) {
            if ( currentComposite == null ) {
                throw new NoMatchingStartTagException();
            }
            return prevComposites.poll();
        }
        return currentComposite;
    }
    
    private MutableComposite handleStartObject(
        JsonParser parser,
        Deque< MutableComposite > prevComposites,
        MutableComposite currentComposite
    ) throws IOException {
        var name = parser.getCurrentName();
        if ( name != null ) {
            var composite = new MutableComposite( name );
            if ( currentComposite != null ) {
                currentComposite.addContent( composite );
                prevComposites.push( currentComposite );
            }
            if ( prevComposites.isEmpty() ) {
                add( composite );
            }
            return composite;
        }
        return currentComposite;
    }
}
