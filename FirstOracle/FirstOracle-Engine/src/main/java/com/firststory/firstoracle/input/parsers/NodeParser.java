/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input.parsers;

import com.firststory.firstoracle.input.structure.Node;

/**
 * @author n1t4chi
 */
public interface NodeParser< Type, NodeType extends Node > {
    
    Type parse( NodeType node );
}
