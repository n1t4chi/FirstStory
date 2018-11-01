/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

/**
 * @author n1t4chi
 */
public class VulkanImageIndex {
    
    private final int index;
    private final VulkanSemaphore imageAvailableSemaphore;
    private final VulkanSemaphore renderFinishedSemaphore;
    private final VulkanFrameBuffer frameBuffer;
    
    VulkanImageIndex(
        int index,
        VulkanSemaphore imageAvailableSemaphore,
        VulkanSemaphore renderFinishedSemaphore,
        VulkanFrameBuffer frameBuffer
    ) {
        this.index = index;
        this.imageAvailableSemaphore = imageAvailableSemaphore;
        this.renderFinishedSemaphore = renderFinishedSemaphore;
        this.frameBuffer = frameBuffer;
    }
    
    public int getIndex() {
        return index;
    }
    
    public VulkanSemaphore getImageAvailableSemaphore() {
        return imageAvailableSemaphore;
    }
    
    public VulkanSemaphore getRenderFinishedSemaphore() {
        return renderFinishedSemaphore;
    }
    
    public VulkanFrameBuffer getFrameBuffer() {
        return frameBuffer;
    }
}
