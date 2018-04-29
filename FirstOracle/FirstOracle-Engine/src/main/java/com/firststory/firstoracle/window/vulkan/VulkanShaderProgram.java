/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.shader.ShaderProgram;
import com.firststory.firstoracle.templates.IOUtilities;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanShaderException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author n1t4chi
 */
class VulkanShaderProgram implements ShaderProgram {
    
    private final String filepath;
    private final ShaderType type;
    private VulkanPhysicalDevice physicalDevice;
    private long address;
    private VkPipelineShaderStageCreateInfo stageCreateInfo;
    
    VulkanShaderProgram( VulkanPhysicalDevice physicalDevice, String filepath, ShaderType type ) {
        this.physicalDevice = physicalDevice;
        this.filepath = filepath;
        this.type = type;
    }
    
    public long getAddress() {
        return address;
    }
    
    public VkPipelineShaderStageCreateInfo getStageCreateInfo() {
        return stageCreateInfo;
    }
    
    @Override
    public void useProgram() {
    
    }
    
    @Override
    public void compile() throws IOException {
        address = createShader();
        stageCreateInfo = createStageCreateInfo();
    }
    
    @Override
    public void dispose() {
        VK10.vkDestroyShaderModule( physicalDevice.getLogicalDevice(), address, null );
    }
    
    private VkPipelineShaderStageCreateInfo createStageCreateInfo() {
        VkPipelineShaderStageCreateInfo stageCreateInfo = VkPipelineShaderStageCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO )
            .module( address )
            .pName( MemoryUtil.memUTF8( "main" ) )
        ;
        int stage;
        switch ( type ) {
            case VERTEX:
                stage = VK10.VK_SHADER_STAGE_VERTEX_BIT;
                break;
            case FRAGMENT:
                stage = VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
                break;
            default:
                throw new UnsupportedOperationException( "Unknown shader type:"+type );
        }
        stageCreateInfo.stage( stage );
        return stageCreateInfo;
    }
    
    private long createShader() throws IOException {
        ByteBuffer value = IOUtilities.readBinaryResource( filepath );
        VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO )
            .pCode( value );
        long[] address = new long[1];
        VulkanHelper.assertCallAndThrow(
            () -> VK10.vkCreateShaderModule( physicalDevice.getLogicalDevice(), createInfo, null, address ),
            errorCode -> new CannotCreateVulkanShaderException( physicalDevice, filepath )
        );
        return address[0];
    }
}
