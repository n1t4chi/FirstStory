/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSemaphore;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.util.List;

/**
 * @author n1t4chi
 */
class VulkanRenderBatchDatasProcessedInfo {
    
    private final List< VkSubmitInfo > submitInfos;
    private final List< VulkanSemaphore > semaphores;
    
    VulkanRenderBatchDatasProcessedInfo(
        List< VkSubmitInfo > submitInfos,
        List< VulkanSemaphore > semaphores
    ) {
        this.submitInfos = submitInfos;
        this.semaphores = semaphores;
    }
    
    List< VkSubmitInfo > getSubmitInfos() {
        return submitInfos;
    }
    
    List< VulkanSemaphore > getSemaphores() {
        return semaphores;
    }
}
