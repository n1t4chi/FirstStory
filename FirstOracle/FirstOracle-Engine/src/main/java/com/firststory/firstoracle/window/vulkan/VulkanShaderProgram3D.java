/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.firststory.firstoracle.window.vulkan.VulkanShaderProgram.SHADER_FILE_PATH_FRAGMENT;
import static com.firststory.firstoracle.window.vulkan.VulkanShaderProgram.SHADER_FILE_PATH_VERTEX_3D;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram3D implements ShaderProgram3D {
    
    
    private final VulkanShaderProgram fragmentShader;
    private final VulkanShaderProgram vertexShader;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final VulkanPhysicalDevice device;
    
    VulkanShaderProgram3D( VulkanPhysicalDevice device ) {
        this.device = device;
        vertexShader = new VulkanShaderProgram( device, SHADER_FILE_PATH_VERTEX_3D, ShaderType.VERTEX );
        fragmentShader = new VulkanShaderProgram( device, SHADER_FILE_PATH_FRAGMENT, ShaderType.FRAGMENT );
    }
    
    @Override
    public void bindPosition( Vector3fc vector ) {
    
    }
    
    @Override
    public void bindCamera( Camera3D camera3D ) {
    
    }
    
    @Override
    public void bindCamera( Matrix4fc camera ) {
    
    }
    
    @Override
    public void bindScale( Vector3fc vector ) {
    
    }
    
    @Override
    public void bindRotation( Vector3fc vector ) {
    
    }
    
    @Override
    public void bindOverlayColour( Vector4fc vector ) {
    
    }
    
    @Override
    public void bindMaxAlphaChannel( float value ) {
    
    }
    
    @Override
    public void useProgram() {
    
    }
    
    @Override
    public void compile() throws IOException {
        vertexShader.compile();
        fragmentShader.compile();
        shaderStages.add( vertexShader.getStageCreateInfo() );
        shaderStages.add( fragmentShader.getStageCreateInfo() );
    }
    
    @Override
    public void dispose() {
        vertexShader.dispose();
        fragmentShader.dispose();
        shaderStages.clear();
    }
    
    List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
}
