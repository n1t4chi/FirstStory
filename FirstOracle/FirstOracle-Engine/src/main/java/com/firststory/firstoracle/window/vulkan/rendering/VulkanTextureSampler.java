/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.window.vulkan.VulkanAddress;
import com.firststory.firstoracle.window.vulkan.VulkanHelper;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.window.vulkan.VulkanTextureData;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanTextureSamplerException;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

/**
 * @author n1t4chi
 */
public class VulkanTextureSampler {
    
    private final VulkanAddress samplerAddress;
    private final VulkanPhysicalDevice device;
    
    public VulkanTextureSampler( VulkanPhysicalDevice device ) {
        this.device = device;
        samplerAddress = createTextureSampler();
    }
    
    public void dispose() {
        VK10.vkDestroySampler( device.getLogicalDevice(), samplerAddress.getValue(), null );
    }
    
    VkDescriptorImageInfo createImageInfo( VulkanTextureData textureData ) {
        return VkDescriptorImageInfo.create()
            .imageLayout( VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL )
            .imageView( textureData.getImageView().getAddress().getValue() )
            .sampler( samplerAddress.getValue() );
    }
    
    private VulkanAddress createTextureSampler() {
        var createInfo = VkSamplerCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO )
            .magFilter( VK10.VK_FILTER_LINEAR )
            .minFilter( VK10.VK_FILTER_LINEAR )
            .addressModeU( VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT )
            .addressModeV( VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT )
            .addressModeW( VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT )
            .anisotropyEnable( true )
            .maxAnisotropy( 16 )
            .borderColor( VK10.VK_BORDER_COLOR_INT_OPAQUE_BLACK )
            .unnormalizedCoordinates( false )
            .compareEnable( false )
            .compareOp( VK10.VK_COMPARE_OP_ALWAYS )
            .mipmapMode( VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR )
            .mipLodBias( 0f )
            .minLod( 0f )
            .maxLod( 0 );
        
        return VulkanHelper.createAddress(
            address -> VK10.vkCreateSampler( device.getLogicalDevice(), createInfo, null, address ),
            resultCode -> new CannotCreateVulkanTextureSamplerException( device, resultCode )
        );
    }
}
