/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.shader;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import org.joml.*;

/**
 * @author n1t4chi
 */
public interface UniformLocation {
    
    void bind( Vector2fc vector );
    
    void bind( Vector3fc vector );
    
    void bind( Vector4fc vector );
    
    void bind( float value );
    
    void bind( Camera3D camera3D );
    
    void bind( Matrix4fc camera );
    
    void bind( Camera2D camera2D );
    
    void bind( Matrix3fc camera );
}
