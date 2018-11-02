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
        this.background = new VulkanStage();
        this.overlay = new VulkanStage();
        this.scene2D = new VulkanStage();
        this.scene3D = new VulkanStage();
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
