/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.Vertex3D;
import com.firststory.firstoracle.object.Vertices;

import java.util.List;

/**
 * @author n1t4chi
 */
public class Vertices3D extends Vertices< Vertex3D, BoundingBox3D > {
    
    public Vertices3D( List<Vertex3D>[] verticesByFrame ) {
        super( verticesByFrame, BoundingBox3D.getBoundingBox3D( verticesByFrame ) );
    }
}
