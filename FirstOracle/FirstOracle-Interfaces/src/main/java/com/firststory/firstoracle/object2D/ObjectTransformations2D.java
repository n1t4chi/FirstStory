/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.ObjectTransformations;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * @author: n1t4chi
 */
public interface ObjectTransformations2D extends ObjectTransformations< Vector2fc, Float, Vector2fc > {

    Vector2fc ZERO = new Vector2f( 0, 0 );
    Vector2fc ONE = new Vector2f( 1, 1 );

    @Override
    default Vector2fc getScale() { return ONE; }

    @Override
    default Float getRotation() { return 0f; }

    @Override
    default Vector2fc getPosition() { return ZERO; }

}
