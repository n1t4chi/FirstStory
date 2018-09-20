/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.window.vulkan.VulkanAddress;
import com.firststory.firstoracle.window.vulkan.VulkanHelper;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanDescriptorPoolException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanDescriptorSetLayoutException;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author n1t4chi
 */
public class VulkanDescriptor {
    
    private static final int MAX_SETS = 1000;
    private final VulkanAddress descriptorSetLayout;
    private final VulkanAddress descriptorPool;
    private final VulkanTextureSampler sampler;
    private final VulkanPhysicalDevice device;
    
    private final List< VulkanDescriptorSet > sets = new ArrayList<>( MAX_SETS );
    private ListIterator< VulkanDescriptorSet > setsIterator;
    
    public VulkanDescriptor( VulkanTextureSampler sampler, VulkanPhysicalDevice device ) {
        this.sampler = sampler;
        this.device = device;
        descriptorSetLayout = createDescriptorSetLayout();
        descriptorPool = createDescriptorPool();
    }
    
    public void dispose() {
        VK10.vkDestroyDescriptorSetLayout( device.getLogicalDevice(), descriptorSetLayout.getValue(), null );
        VK10.vkDestroyDescriptorPool( device.getLogicalDevice(), descriptorPool.getValue(), null );
    }
    
    public VulkanAddress getDescriptorSetLayout() {
        return descriptorSetLayout;
    }
    
    VulkanDescriptorSet getNextDescriptorSet() {
        if ( !setsIterator.hasNext() ) {
            VulkanDescriptorSet descriptorSet = createDescriptorSet();
            setsIterator.add( descriptorSet );
            return descriptorSet;
        }
        return setsIterator.next();
    }
    
    private VulkanDescriptorSet createDescriptorSet() {
        return new VulkanDescriptorSet( this );
    }
    
    void resetDescriptors() {
        setsIterator = sets.listIterator();
    }
    
    VulkanAddress getDescriptorPool() {
        return descriptorPool;
    }
    
    VulkanTextureSampler getSampler() {
        return sampler;
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
    
    private VulkanAddress createDescriptorPool() {
        VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO )
            .pPoolSizes( VkDescriptorPoolSize.create( 2 )
                .put( 0, createPoolSize( VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER ) )
                .put( 1, createPoolSize( VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER ) ) )
            .maxSets( MAX_SETS );
        return VulkanHelper.createAddress( address -> VK10.vkCreateDescriptorPool( device.getLogicalDevice(),
            createInfo,
            null,
            address
            ),
            resultCode -> new CannotCreateVulkanDescriptorPoolException( device, resultCode )
        );
    }
    
    private VkDescriptorPoolSize createPoolSize( int type ) {
        return VkDescriptorPoolSize.create().type( type ).descriptorCount( 1 );
    }
    
    private VulkanAddress createDescriptorSetLayout() {
        VkDescriptorSetLayoutCreateInfo createInfo = VkDescriptorSetLayoutCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO )
            .pBindings( VkDescriptorSetLayoutBinding.calloc( 2 )
                .put( 0,
                    createLayoutBinding( 0, 1, VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, VK10.VK_SHADER_STAGE_VERTEX_BIT )
                )
                .put( 1,
                    createLayoutBinding( 1,
                        1,
                        VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
                        VK10.VK_SHADER_STAGE_FRAGMENT_BIT
                    )
                ) );
        
        return VulkanHelper.createAddress(
            address -> VK10.vkCreateDescriptorSetLayout( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanDescriptorSetLayoutException( device, resultCode )
        );
    }
    
    private VkDescriptorSetLayoutBinding createLayoutBinding( int index, int count, int type, int stageFlag ) {
        return VkDescriptorSetLayoutBinding.create()
            .binding( index )
            .descriptorType( type )
            .descriptorCount( count )
            .stageFlags( stageFlag )
            .pImmutableSamplers( null );
    }
    
}
