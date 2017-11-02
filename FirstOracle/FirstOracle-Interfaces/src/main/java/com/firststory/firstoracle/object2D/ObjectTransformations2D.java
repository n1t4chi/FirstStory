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
public interface ObjectTransformations2D extends ObjectTransformations<Vector2fc> {

    ObjectTransformations2D IDENTITY_TRANSFORMATION = new IdentityTransformations2D();
    Vector2fc ZERO = new Vector2f( 0, 0 );
    Vector2fc ONE = new Vector2f( 1, 1 );

    static ObjectTransformations2D getIdentity() {return IDENTITY_TRANSFORMATION;}
    @Override
    default Vector2fc getScale() { return ONE; }

    @Override
    default Vector2fc getRotation() { return ZERO; }

    @Override
    default Vector2fc getPosition() { return ZERO; }

    class IdentityTransformations2D implements ObjectTransformations2D {}
}
