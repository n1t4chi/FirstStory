/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.shader;

import com.firststory.firstoracle.camera3D.Camera3D;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

/**
 * @author n1t4chi
 */
public interface ShaderProgram3D extends ShaderProgram {

    void bindPosition( Vector3fc vector );

    void bindCamera( Camera3D camera3D );

    void bindCamera( Matrix4fc camera );

    void bindScale( Vector3fc vector );

    void bindRotation( Vector3fc vector );

    void bindOverlayColour( Vector4fc vector );

    void bindMaxAlphaChannel( float value );
}
