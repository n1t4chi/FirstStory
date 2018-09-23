/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Vertex2D;
import com.firststory.firstoracle.object.Vertices;

import java.util.List;

/**
 * @author n1t4chi
 */
public class Vertices2D extends Vertices< Vertex2D, BoundingBox2D > {
    
    public Vertices2D( List< Vertex2D >[] verticesByFrame ) {
        super( verticesByFrame, BoundingBox2D.getBoundingBox2D( verticesByFrame ) );
    }
}
