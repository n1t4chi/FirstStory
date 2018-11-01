/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanDescriptorPoolException;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author n1t4chi
 */
public class VulkanDescriptorPool {
    private final List< VulkanDescriptorSet > setList;
    private final int setCount;
    private final ListIterator< VulkanDescriptorSet > setsIterator;
    private final VulkanPhysicalDevice device;
    private final VulkanDescriptor descriptor;
    
    private final VulkanAddress descriptorPool;
    
    VulkanDescriptorPool( VulkanPhysicalDevice device, VulkanDescriptor descriptor, int sets ) {
        this.device = device;
        this.descriptor = descriptor;
        this.setCount = sets;
        setList = new ArrayList<>( setCount );
        setsIterator = setList.listIterator();
        descriptorPool = createDescriptorPool();
    }
    
    VulkanAddress getAddress() {
        return descriptorPool;
    }
    
    VulkanDescriptorSet getNextDescriptorSet() {
        if ( !setsIterator.hasNext() ) {
            var descriptorSet = createDescriptorSet();
            setsIterator.add( descriptorSet );
            return descriptorSet;
        }
        return setsIterator.next();
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
        return new VulkanDescriptorSet( device, descriptor, this );
    }
    
    public void dispose() {
        VK10.vkDestroyDescriptorPool( device.getLogicalDevice(), descriptorPool.getValue(), null );
    }
}
