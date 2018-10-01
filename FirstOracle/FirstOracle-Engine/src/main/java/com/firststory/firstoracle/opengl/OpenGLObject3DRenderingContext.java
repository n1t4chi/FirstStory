/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.opengl;

import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderData;

/**
 * @author n1t4chi
 */
class OpenGLObject3DRenderingContext implements Object3DRenderingContext {
    private final OpenGlRenderingContext context;
    
    OpenGLObject3DRenderingContext( OpenGlRenderingContext context ) {
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
        innerRender( renderData );
    }
    
    private void innerRender( RenderData renderData ) {
        context.render( renderData );
    }
}
