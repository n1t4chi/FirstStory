/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.*;

/**
 * @author n1t4chi
 */
class VulkanStage {
    
    private final Map< Texture, List< RenderData > > renderDataByTexture = new HashMap<>();
    private final Set< Texture > textures = new HashSet<>();
    private Camera camera;
    
    void setCamera( Camera camera ) {
        this.camera = camera;
    }
    
    void addRenderData( RenderData data ) {
        renderDataByTexture.computeIfAbsent( data.getTexture(), texture -> new ArrayList<>() ).add( data );
        textures.add( data.getTexture() );
    }
    
    Map< Texture, List< RenderData > > getRenderDataByTexture() {
        return renderDataByTexture;
    }
    
    Set< Texture > getTextures() {
        return textures;
    }
    
    Camera getCamera() {
        return camera;
    }
    
    void clear() {
        renderDataByTexture.forEach( ( key, value ) -> value.clear() );
        renderDataByTexture.clear();
    }
    
}
