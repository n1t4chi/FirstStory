/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface PositionableObject2D< Transformations extends PositionableObject2DTransformations, Vertices extends Vertices2D >
    extends Object2D< Transformations, Vertices >
{
    
    void setTransformations( Transformations transformations );
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations(), getTransformations().getPosition() );
    }
    
}