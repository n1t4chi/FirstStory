/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.Runner;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author n1t4chi
 */
public class VulkanFrameworkProvider implements RenderingFrameworkProvider {
    private static final VulkanFrameworkProvider framework = new VulkanFrameworkProvider();
    static {
        Runner.registerFramework( framework );
    }
    
    private final Map<Thread,VulkanFramework > instances = new HashMap<>();
    
    public static VulkanFrameworkProvider getFramework() {
        return framework;
    }
    
    @Override
    public synchronized RenderingFramework getRenderingContext() {
        return instances.computeIfAbsent( Thread.currentThread(), thread -> new VulkanFramework() );
    }
    @Override
    public void terminate() {}
    
    @Override
    public boolean isOpenGL() {
        return false;
    }
    
    private VulkanFrameworkProvider() {}
}
