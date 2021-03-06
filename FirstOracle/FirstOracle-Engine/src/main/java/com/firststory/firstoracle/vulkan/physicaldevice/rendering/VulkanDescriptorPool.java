/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.*;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanDescriptorPoolException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import org.lwjgl.vulkan.*;

import java.util.*;

/**
 * @author n1t4chi
 */
public class VulkanDescriptorPool {
    private final List< VulkanDescriptorSet > setList;
    private final int setCount;
    private final VulkanDeviceAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanDescriptor descriptor;
    
    private final VulkanAddress descriptorPool;
    
    public VulkanDescriptorPool(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanDescriptor descriptor,
        int sets
    ) {
        this.allocator = allocator;
        this.device = device;
        this.descriptor = descriptor;
        this.setCount = sets;
        setList = new ArrayList<>( setCount );
        descriptorPool = createDescriptorPool();
    }
    
    VulkanAddress getAddress() {
        return descriptorPool;
    }
    
    synchronized VulkanDescriptorSet getNextDescriptorSet() {
        var descriptorSet = createDescriptorSet();
        setList.add( descriptorSet );
        return descriptorSet;
    }
    
    private VulkanAddress createDescriptorPool() {
        var createInfo = VkDescriptorPoolCreateInfo.calloc()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO )
            .pPoolSizes( VkDescriptorPoolSize.calloc( 2 )
                .put( 0, createPoolSize( VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER ) )
                .put( 1, createPoolSize( VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER ) ) )
            .maxSets( setCount );
        return VulkanHelper.createAddress( address -> VK10.vkCreateDescriptorPool(
                device.getLogicalDevice(),
                createInfo,
                null,
                address
            ),
            resultCode -> new CannotCreateVulkanDescriptorPoolException( device, resultCode )
        );
    }
    
    private VkDescriptorPoolSize createPoolSize( int type ) {
        return VkDescriptorPoolSize.calloc().type( type ).descriptorCount( setCount );
    }
    
    private VulkanDescriptorSet createDescriptorSet() {
        return allocator.createDescriptorSet( descriptor, this );
    }
    
    public void dispose() {
        allocator.deregisterDescriptorPool( this );
    }
    
    public void disposeUnsafe() {
        setList.forEach( VulkanDescriptorSet::dispose );
        setList.clear();
        VK10.vkDestroyDescriptorPool( device.getLogicalDevice(), descriptorPool.getValue(), null );
    }
}
