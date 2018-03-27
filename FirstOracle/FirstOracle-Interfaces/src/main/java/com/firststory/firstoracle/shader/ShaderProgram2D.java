/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.shader;

import com.firststory.firstoracle.camera2D.Camera2D;
import org.joml.Matrix3fc;
import org.joml.Vector2fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface ShaderProgram2D extends ShaderProgram {

    void bindPosition( Vector2fc vector );

    void bindCamera( Camera2D camera2D );

    void bindCamera( Matrix3fc camera );

    void bindScale( Vector2fc vector );

    void bindRotation( float rotation );

    void bindOverlayColour( Vector4fc vector );

    void bindMaxAlphaChannel( float value );
}
