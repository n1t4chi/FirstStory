/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.object.data.Colour;

/**
 * @author n1t4chi
 */
public interface RenderingContext {
    
    void setBackgroundColour( Colour backgroundColour );
    
    void render2D( Render< Object2DRenderingContext > context );
    
    void render3D( Render< Object3DRenderingContext > context );
    
    void useRendering2D( Camera2D camera, boolean useDepth );
    
    void useRendering3D( Camera3D camera, boolean useDepth );
    
    interface Render< T extends ObjectRenderingContext< ?, ?, ?, ?, ?, ? > > {
        void render( T renderer );
    }
}
