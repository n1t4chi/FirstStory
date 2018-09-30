/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.VulkanTextureData;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanDescriptorSetException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanDescriptorSet {
    
    private final VulkanPhysicalDevice device;
    private final VulkanDescriptor descriptor;
    private final VulkanDescriptorPool descriptorPool;
    private final VulkanAddress descriptorSet;
    
    VulkanDescriptorSet( VulkanPhysicalDevice device, VulkanDescriptor descriptor, VulkanDescriptorPool descriptorPool ) {
        this.device = device;
        this.descriptor = descriptor;
        this.descriptorPool = descriptorPool;
    
        var allocateInfo = VkDescriptorSetAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO )
            .descriptorPool( descriptorPool.getAddress().getValue() )
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
    
    void updateDescriptorSet( VulkanTextureSampler sampler, VulkanTextureData textureData, VkDescriptorBufferInfo uniformBufferInfo ) {
        var imageInfo = VkDescriptorImageInfo.create( 1 )
            .put( 0, sampler.createImageInfo( textureData ) );
        
        VK10.vkUpdateDescriptorSets( descriptor.getDevice().getLogicalDevice(),
            VkWriteDescriptorSet.create( 2 )
                .put( 0, createDescriptorWrite( 0,
                    VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                    VkDescriptorBufferInfo.create( 1 ).put( 0, uniformBufferInfo ),
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
