/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

/**
 * @author n1t4chi
 */
public interface BoundingBox< Box extends BoundingBox, Transformations extends ObjectTransformations > {
    
    Box getTransformedBoundingBox( Transformations transformations );
}
