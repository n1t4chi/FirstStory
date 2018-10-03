/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.data.Position;

import java.util.Arrays;
import java.util.List;

/**
 * @author n1t4chi
 */
public abstract class Vertices< VertexData extends Position, BoundingBoxType extends BoundingBox< ?, ?, ? > > extends VertexAttribute< VertexData > {
    
    private final BoundingBoxType boundingBox;
    private List< VertexData >[] verticesByFrame;
    
    public Vertices( List< VertexData>[] verticesByFrame, BoundingBoxType boundingBox ) {
        setVertices( verticesByFrame );
        this.boundingBox = boundingBox;
    }
    
    public void setVertices( List< VertexData >[] verticesByFrame ) {
        dispose();
        this.verticesByFrame = verticesByFrame;
    }
    
    public BoundingBoxType getBoundingBox() {
        return boundingBox;
    }
    
    @Override
    public String toString() {
        return "Vertices:{" + Arrays.deepToString( verticesByFrame ) + '}';
    }
    
    private void assertParameters( int[] parameters ) {
        if ( parameters.length != 1 ) {
            throw new IllegalArgumentException( "Illegal parameter length" );
        }
    }
    
    @Override
    protected long getKey( int... parameters ) {
        assertParameters( parameters );
        return parameters[ 0 ];
    }
    
    @Override
    protected List< VertexData > getData( int... parameters ) {
        assertParameters( parameters );
        return verticesByFrame[ parameters[ 0 ] ];
    }
}
