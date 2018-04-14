/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.vulkan.VkInstance;

/**
 * @author n1t4chi
 */
class CannotCreateVulkanWindowSurfaceException extends VulkanException {
    
    public CannotCreateVulkanWindowSurfaceException( VkInstance instance, WindowContext window ) {
        super( "Cannot create window surface for instance: "+instance +" and window: "+ window  );
    }
}
