/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

/**
 * @author n1t4chi
 */
class VulkanStages {
    
    private final VulkanStage background;
    private final VulkanStage overlay;
    private final VulkanStage scene2D;
    private final VulkanStage scene3D;
    
    VulkanStages() {
        this.background = new VulkanStage( true );
        this.scene2D = new VulkanStage( false );
        this.scene3D = new VulkanStage( false );
        this.overlay = new VulkanStage( false );
    }
    
    VulkanStage getBackground() {
        return background;
    }
    
    VulkanStage getOverlay() {
        return overlay;
    }
    
    VulkanStage getScene2D() {
        return scene2D;
    }
    
    VulkanStage getScene3D() {
        return scene3D;
    }
}
