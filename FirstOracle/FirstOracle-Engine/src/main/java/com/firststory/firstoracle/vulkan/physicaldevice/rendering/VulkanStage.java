/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author n1t4chi
 */
class VulkanStage {
    
    private final Map< Texture, List< RenderData > > renderDataByTexture = new HashMap<>();
    private Camera camera;
    
    void setCamera( Camera camera ) {
        this.camera = camera;
    }
    
    void addRenderData( RenderData data ) {
        renderDataByTexture
            .computeIfAbsent(
                data.getTexture(),
                texture -> new ArrayList<>()
            )
            .add( data );
    }
    
    Map< Texture, List< RenderData > > getRenderDataByTexture() {
        return renderDataByTexture;
    }
    
    Camera getCamera() {
        return camera;
    }
    
    void clear() {
        renderDataByTexture.forEach( ( key, value ) -> value.clear() );
        renderDataByTexture.clear();
    }
    
}
