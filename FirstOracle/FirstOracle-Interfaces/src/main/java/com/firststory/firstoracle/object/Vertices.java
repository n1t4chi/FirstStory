/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.object3D.BoundingBox3D;

/**
 * @author: n1t4chi
 */
public abstract class Vertices< BoundingBox > extends VertexAttributes {

    private BoundingBox boundingBox;

    public Vertices( float[][] verticesByFrame, BoundingBox boundingBox ) {
        super( verticesByFrame );
        this.boundingBox = boundingBox;
    }

    public BoundingBox getBoundingBox() {return boundingBox;}

    @Override
    protected int getIndex() {
        return 0;
    }

}
