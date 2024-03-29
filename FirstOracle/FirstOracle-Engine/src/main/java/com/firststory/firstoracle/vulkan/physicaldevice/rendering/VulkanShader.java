/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.shader.ShaderProgram;
import com.firststory.firstoracle.templates.IOUtilities;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanShaderException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanShaderType;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;

import static com.firststory.firstoracle.FirstOracleConstants.SHADER_FILES_LOCATION;

/**
 * @author n1t4chi
 */
class VulkanShader implements ShaderProgram {
    
    static final String SHADER_FILE_PATH_VERTEX_3D = SHADER_FILES_LOCATION + "shader3D.vk.vert.spv";
    static final String SHADER_FILE_PATH_VERTEX_2D = SHADER_FILES_LOCATION + "shader2D.vk.vert.spv";
    static final String SHADER_FILE_PATH_FRAGMENT = SHADER_FILES_LOCATION + "shader.vk.frag.spv";
    
    private final String filepath;
    private final VulkanShaderType type;
    private final VulkanPhysicalDevice physicalDevice;
    private VulkanAddress address;
    private VkPipelineShaderStageCreateInfo stageCreateInfo;
    
    VulkanShader( VulkanPhysicalDevice physicalDevice, String filepath, VulkanShaderType type ) {
        this.physicalDevice = physicalDevice;
        this.filepath = filepath;
        this.type = type;
    }
    
    VulkanAddress getAddress() {
        return address;
    }
    
    VkPipelineShaderStageCreateInfo getStageCreateInfo() {
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
        if( address.isNotNull() ) {
            VK10.vkDestroyShaderModule( physicalDevice.getLogicalDevice(), address.getValue(), null );
        }
    }
    
    private VkPipelineShaderStageCreateInfo createStageCreateInfo() {
        var stageCreateInfo = VkPipelineShaderStageCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO )
            .module( address.getValue() )
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
    
    private VulkanAddress createShader() throws IOException {
        var value = IOUtilities.readBinaryResource( filepath );
        return VulkanHelper.createAddress(
            () -> VkShaderModuleCreateInfo.calloc()
                .sType( VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO )
                .pCode( value ),
            ( createInfo, address ) -> VK10.vkCreateShaderModule( physicalDevice.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanShaderException( physicalDevice, filepath )
        );
    }
}
