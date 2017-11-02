/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.VertexAttributes;
import com.firststory.firstoracle.object.Vertices;

/**
 * @author: n1t4chi
 */
public class Vertices3D extends Vertices< BoundingBox3D > {

    public Vertices3D( float[][] verticesByFrame ) {
        super( verticesByFrame, BoundingBox3D.getBoundingBox3D( verticesByFrame ) );
    }

    @Override
    protected int getVertexSize() {
        return 3;
    }
}
