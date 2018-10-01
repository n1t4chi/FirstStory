/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.RenderData;

/**
 * @author n1t4chi
 */
public class OpenGLObject2DRenderingContext implements Object2DRenderingContext {
    private final OpenGlRenderingContext context;
    
    OpenGLObject2DRenderingContext( OpenGlRenderingContext context ) {
        this.context = context;
    }
    
    @Override
    public boolean shouldUseTextures() {
        return context.shouldUseTextures();
    }
    
    @Override
    public boolean shouldDrawBorder() {
        return context.shouldDrawBorder();
    }
    
    @Override
    public void render( RenderData renderData ) {
        context.render( renderData );
    }
}
