/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderData;

/**
 * @author n1t4chi
 */
public class VulkanObject3DRenderingContext implements Object3DRenderingContext {
    
    private final VulkanRenderingContext context;
    
    VulkanObject3DRenderingContext( VulkanRenderingContext context ) {
        
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
        context.draw( renderData );
    }
}
