/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanDescriptorSetLayoutException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;

/**
 * @author n1t4chi
 */
public class VulkanDescriptor {
    
    private final VulkanAddress descriptorSetLayout;
    private final VulkanPhysicalDevice device;
    
    public VulkanDescriptor( VulkanPhysicalDevice device ) {
        this.device = device;
        descriptorSetLayout = createDescriptorSetLayout();
    }
    
    public void dispose() {
        VK10.vkDestroyDescriptorSetLayout( device.getLogicalDevice(), descriptorSetLayout.getValue(), null );
    }
    
    public VulkanAddress getDescriptorSetLayout() {
        return descriptorSetLayout;
    }
    
    public VulkanDescriptorPool createDescriptorPool( int sets ) {
        return new VulkanDescriptorPool( device, this, sets );
    }
    
    VulkanPhysicalDevice getDevice() {
        return device;
    }
    
    private VulkanAddress createDescriptorSetLayout() {
        var createInfo = VkDescriptorSetLayoutCreateInfo.create()
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
