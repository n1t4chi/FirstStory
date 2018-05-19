/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.shader;

import com.firststory.firstoracle.camera2D.Camera2D;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface ShaderProgram2D extends ShaderProgram {

    void bindPosition( Vector2fc vector );

    default void bindCamera( Camera2D camera2D ) {
        bindCamera( camera2D.getMatrixRepresentation() );
    }

    void bindCamera( Matrix4fc camera );

    void bindScale( Vector2fc vector );

    void bindRotation( float rotation );

    void bindOverlayColour( Vector4fc vector );

    void bindMaxAlphaChannel( float value );
}
