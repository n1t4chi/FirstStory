/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.Runner;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.window.WindowContext;

import java.util.*;

/**
 * @author n1t4chi
 */
public class VulkanFrameworkProvider implements RenderingFrameworkProvider {
    private static final VulkanFrameworkProvider framework = new VulkanFrameworkProvider();
    static {
        Runner.registerFramework( framework );
    }
    
    private final Map<Thread,VulkanFramework > instances = new HashMap<>();
    
    public static VulkanFrameworkProvider getProvider() {
        return framework;
    }
    
    @Override
    public synchronized RenderingFramework getRenderingFramework( WindowContext window ) {
        return instances.computeIfAbsent( Thread.currentThread(), thread -> new VulkanFramework( window ) );
    }
    
    @Override
    public void terminate() {
        instances.forEach( ( thread, vulkanFramework ) -> vulkanFramework.dispose() );
    }
    
    @Override
    public boolean isOpenGL() {
        return false;
    }
    
    private VulkanFrameworkProvider() {}
}
