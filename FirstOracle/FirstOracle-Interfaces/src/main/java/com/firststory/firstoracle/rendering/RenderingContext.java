/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Colour;

import java.util.List;

/**
 * @author n1t4chi
 */
public interface RenderingContext {
    
    void renderOverlay( Camera2D camera, List< RenderData > renderDatas );
    
    void renderBackground( Camera2D camera, Colour backgroundColour, List< RenderData > renderDatas );
    
    void renderScene3D( Camera3D camera, List< RenderData > renderDatas );
    
    void renderScene2D( Camera2D camera, List< RenderData > renderDatas );
    
}
