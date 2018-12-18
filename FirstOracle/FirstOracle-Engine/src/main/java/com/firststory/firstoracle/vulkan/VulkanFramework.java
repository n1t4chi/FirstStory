/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.glfw.GLFWVulkan;

import java.util.logging.Logger;

public class VulkanFramework implements RenderingFramework {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanFramework.class );
    
    public static boolean validationLayersAreEnabled() {
        return PropertiesUtil.isPropertyTrue( PropertiesUtil.VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY );
    }
    
    private final VulkanInstance instance;
    
    VulkanFramework( WindowContext window ) {
        if ( !GLFWVulkan.glfwVulkanSupported() ) {
            throw new RuntimeException( "GLFW Vulkan not supported!" );
        }
        instance = new VulkanInstance( window );
        logger.finer( "Finished creating Vulkan Framework: " + this );
    }
    
    @Override
    public void updateViewPort( int x, int y, int width, int height ) {
        instance.updateRenderingContext();
    }
    
    @Override
    public void invoke( FrameworkCommands commands ) throws Exception {
        commands.execute( this );
    }
    
    @Override
    public void compileShaders() {}
    
    @Override
    public void dispose() {
        instance.dispose();
    }
    
    @Override
    public void render( Renderer renderer, double lastFrameUpdate ) {
        instance.render( renderer, lastFrameUpdate );
    }
}
