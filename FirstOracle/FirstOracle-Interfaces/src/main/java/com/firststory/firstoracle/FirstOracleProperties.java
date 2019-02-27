/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

/**
 * @author n1t4chi
 */
public interface FirstOracleProperties {

    String VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY = "VulkanValidationLayersEnabled";
    String VULKAN_VALIDATION_LAYERS_LIST_PROPERTY = "VulkanValidationLayersList";
    String VULKAN_USE_SRGB_PROPERTY = "VulkanUseSRGBIfPossible";
    
    String APPLICATION_CLASS_NAME_PROPERTY = "ApplicationClassName";
    String RENDERING_FRAMEWORK_CLASS_NAME_PROPERTY = "RenderingFrameworkClassName";
    String WINDOW_FRAMEWORK_CLASS_NAME_PROPERTY = "WindowFrameworkClassName";
    String GUI_FRAMEWORK_CLASS_NAME_PROPERTY = "GuiFrameworkClassName";
    
    String DISABLE_TEXTURES_PROPERTY = "DisableTextures";
    String DRAW_BORDER_PROPERTY = "DrawBorder";
    
    String FORCE_ONE_LOOP_CYCLE_PROPERTY = "ForceOneLoopCycle";
    String RENDER_LOOP_PERFORMANCE_LOG_PROPERTY = "EnableRenderLoopPerformanceLog";
    
    String WINDOW_RENDERER_GRID_2D_RENDERER_CLASS_NAME_PROPERTY = "grid2DRendererClassName";
    String WINDOW_RENDERER_GRID_3D_RENDERER_CLASS_NAME_PROPERTY = "grid3DRendererClassName";

}
