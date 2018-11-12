/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.object.VertexAttributeLoader;

/**
 * @author n1t4chi
 */
public class MockAttributeLoader implements VertexAttributeLoader< MockBuffer> {
    
    @Override
    public MockBuffer provideBuffer( float[] array ) {
        return new MockBuffer( array );
    }
}
