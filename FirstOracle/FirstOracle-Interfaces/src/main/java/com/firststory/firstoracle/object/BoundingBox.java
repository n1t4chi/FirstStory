/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object;

import com.firststory.firstoracle.object.data.Position;

/**
 * @author n1t4chi
 */
public interface BoundingBox< Box extends BoundingBox< ?, ?, ? >, Transformations extends ObjectTransformations< ?, ? >, PositionType extends Position > {
    
    Box getTransformedBoundingBox( Transformations transformations, PositionType position );
}
