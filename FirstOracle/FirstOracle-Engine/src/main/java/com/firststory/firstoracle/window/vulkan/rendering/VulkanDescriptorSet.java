/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.window.vulkan.VulkanAddress;
import com.firststory.firstoracle.window.vulkan.VulkanHelper;
import com.firststory.firstoracle.window.vulkan.VulkanTextureData;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanDescriptorSetException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanDescriptorSet {
    
    private final VulkanDescriptor descriptor;
    private final VulkanAddress descriptorSet;
    
    VulkanDescriptorSet( VulkanDescriptor descriptor ) {
        this.descriptor = descriptor;
        
        VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO )
            .descriptorPool( descriptor.getDescriptorPool().getValue() )
            .pSetLayouts( MemoryUtil.memAllocLong( 1 ).put( 0, descriptor.getDescriptorSetLayout().getValue() ) );
        
        descriptorSet = VulkanHelper.createAddress(
            address -> VK10.vkAllocateDescriptorSets(
                descriptor.getDevice().getLogicalDevice(),
                allocateInfo,
                address
            ),
            resultCode -> new CannotCreateVulkanDescriptorSetException( descriptor.getDevice(), resultCode )
        );
    }
    
    void updateDesciptorSet( VulkanShaderProgram shader, VulkanTextureSampler sampler, VulkanTextureData textureData ) {
        VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.create( 1 )
            .put( 0, sampler.createImageInfo( textureData ) );
        
        VK10.vkUpdateDescriptorSets( descriptor.getDevice().getLogicalDevice(),
            VkWriteDescriptorSet.create( 2 )
                .put( 0, createDescriptorWrite( 0,
                    VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                    VkDescriptorBufferInfo.create( 1 ).put( 0, shader.getBufferInfo() ),
                    null
                ) )
                .put( 1, createDescriptorWrite( 1,
                    VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
                    null,
                    imageInfo
                ) ),
            null
        );
    }
    
    public VulkanAddress getAddress() {
        return descriptorSet;
    }
    
    private VkWriteDescriptorSet createDescriptorWrite(
        int bindingIndex,
        int type,
        VkDescriptorBufferInfo.Buffer bufferInfos,
        VkDescriptorImageInfo.Buffer imageInfos
    ) {
        return VkWriteDescriptorSet.create()
            .sType( VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET )
            .dstSet( descriptorSet.getValue() )
            .dstBinding( bindingIndex )
            .descriptorType( type )
            .dstArrayElement( 0 )
            .pBufferInfo( bufferInfos )
            .pImageInfo( imageInfos )
            .pTexelBufferView( null );
    }
}
