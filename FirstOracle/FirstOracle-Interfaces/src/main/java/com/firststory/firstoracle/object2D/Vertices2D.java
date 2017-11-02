/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Vertices;
import com.firststory.firstoracle.object3D.BoundingBox3D;

/**
 * @author: n1t4chi
 */
public class Vertices2D extends Vertices< BoundingBox2D > {

    public Vertices2D( float[][] verticesByFrame ) {
        super( verticesByFrame, BoundingBox2D.getBoundingBox2D( verticesByFrame ) );
    }

    @Override
    protected int getVertexSize() {
        return 2;
    }
}
