/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public class AnimatedMutableObject2D< Transformations extends Object2DTransformations, Vertices extends Vertices2D >
    extends MutableObject2D< Transformations, Vertices >
{
    
    @Override
    public int getCurrentUvMapDirection() {
        return 0;
    }
    
    @Override
    public int getCurrentUvMapFrame() {
        return 0;
    }
    
    @Override
    public int getCurrentVertexFrame() {
        return 0;
    }
}
