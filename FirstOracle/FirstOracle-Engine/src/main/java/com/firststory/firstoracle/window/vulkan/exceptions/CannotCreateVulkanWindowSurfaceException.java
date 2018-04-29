/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.vulkan.VkInstance;

/**
 * @author n1t4chi
 */
public class CannotCreateVulkanWindowSurfaceException extends VulkanException {
    
    public CannotCreateVulkanWindowSurfaceException( Integer errorCode, VkInstance instance, WindowContext window ) {
        super( errorCode, "Cannot create window surface for instance: "+instance +" and window: "+ window  );
    }
}
