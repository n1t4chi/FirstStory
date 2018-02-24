/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.Vulkan;


public class VulkanContext {
    private static VulkanContext instance = new VulkanContext();

    public static VulkanContext getInstance() {
        return instance;
    }
    
    private VulkanContext(){}
    
    public static void terminate() {
        instance = null;
    }
}
