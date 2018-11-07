/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author n1t4chi
 */
class VulkanStage {
    
    private List< RenderData > renderDatas;
    private final Set< Texture > textures = new HashSet<>();
    private Camera camera;
    private final boolean initialStage;
    
    VulkanStage( boolean initialStage ) {
        this.initialStage = initialStage;
    }
    
    boolean isInitialStage() {
        return initialStage;
    }
    
    void addRenderDatas( List< RenderData > renderDatas ) {
        synchronized ( renderDatas )
        {
            this.renderDatas = new ArrayList<>( renderDatas );
        }
    }
    
    void setCamera( Camera camera ) {
        this.camera = camera;
    }
    
    List< RenderData > getRenderDatas() {
        return renderDatas;
    }
    
    Set< Texture > getTextures() {
        textures.add( FirstOracleConstants.EMPTY_TEXTURE );
        renderDatas.forEach( renderData -> textures.add( renderData.getTexture() ) );
        return textures;
    }
    
    Camera getCamera() {
        return camera;
    }
}
