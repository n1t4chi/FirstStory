/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.*;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanDescriptorSetException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanTextureData;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
public class VulkanDescriptorSet {
    
    private final VulkanDeviceAllocator allocator;
    private final VulkanDescriptor descriptor;
    private final VulkanAddress descriptorSet;
    private VulkanTextureSampler textureSampler;
    
    public VulkanDescriptorSet(
        VulkanDeviceAllocator allocator,
        VulkanDescriptor descriptor,
        VulkanDescriptorPool descriptorPool
    ) {
        this.allocator = allocator;
        this.descriptor = descriptor;
    
        var allocateInfo = VkDescriptorSetAllocateInfo.calloc()
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
    
    void setSampler( VulkanTextureSampler textureSampler ) {
        this.textureSampler = textureSampler;
    }
    
    void updateDescriptorSet( VulkanTextureSampler sampler, VulkanTextureData textureData, VkDescriptorBufferInfo uniformBufferInfo ) {
        var imageInfo = VkDescriptorImageInfo.calloc( 1 )
            .put( 0, sampler.createImageInfo( textureData ) );
        
        VK10.vkUpdateDescriptorSets( descriptor.getDevice().getLogicalDevice(),
            VkWriteDescriptorSet.calloc( 2 )
                .put( 0, createDescriptorWrite( 0,
                    VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                    VkDescriptorBufferInfo.calloc( 1 ).put( 0, uniformBufferInfo ),
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
        return VkWriteDescriptorSet.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET )
            .dstSet( descriptorSet.getValue() )
            .dstBinding( bindingIndex )
            .descriptorType( type )
            .dstArrayElement( 0 )
            .pBufferInfo( bufferInfos )
            .pImageInfo( imageInfos )
            .pTexelBufferView( null );
    }
    
    public void dispose() {
        if( textureSampler != null ) {
            textureSampler.dispose();
            textureSampler = null;
        }
        allocator.deregisterDescriptorSet( this );
    }
    
    public void disposeUnsafe() {
    }
}
