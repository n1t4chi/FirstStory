/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotAllocateVulkanImageMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotBindVulkanImageMemoryException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanImageException;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author n1t4chi
 */
public class VulkanTextureLoader implements TextureBufferLoader<VulkanTexture> {
    
    private final VulkanPhysicalDevice device;
    private final VulkanDataBufferLoader bufferLoader;
    
    VulkanTextureLoader( VulkanPhysicalDevice physicalDevice, VulkanDataBufferLoader bufferLoader ) {
        
        device = physicalDevice;
        this.bufferLoader = bufferLoader;
    }
    
    @Override
    public VulkanTexture create() {
        return new VulkanTexture();
    }
    
    @Override
    public void bind( VulkanTexture textureData ) {
    
    }
    
    @Override
    public void load( VulkanTexture textureData, ByteBuffer imageBuffer, String name ) throws
        BufferNotCreatedException
    {
        IntBuffer w = BufferUtils.createIntBuffer( 1 );
        IntBuffer h = BufferUtils.createIntBuffer( 1 );
        IntBuffer c = BufferUtils.createIntBuffer( 1 );
        ByteBuffer pixels = STBImage.stbi_load_from_memory( imageBuffer, w, h, c, STBImage.STBI_rgb_alpha );
        if ( pixels == null ) {
            throw new BufferNotCreatedException( "Cannot load image:" + name );
        }
        int width = w.get( 0 );
        int height = h.get( 0 );
    
        int imageSize = width * height * 4;
        byte[] data = new byte[imageSize];
    
        pixels.get( data );
    
        textureData.setStagingBuffer( bufferLoader.create() );
        bufferLoader.load( textureData.getStagingBuffer(), data );
        textureData.getStagingBuffer().bind();
    
        createImage( textureData, width, height );
    }
    
    private void createImage( VulkanTexture textureData, int width, int height ) {
        VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO )
            .imageType( VK10.VK_IMAGE_TYPE_2D )
            .extent( VkExtent3D.create().set( width, height, 1 ) )
            .mipLevels( 1 )
            .arrayLayers( 1 )
            .format( VK10.VK_FORMAT_R8G8B8A8_UNORM )
            .tiling( VK10.VK_IMAGE_TILING_OPTIMAL )
            .initialLayout( VK10.VK_IMAGE_LAYOUT_UNDEFINED )
            .usage( VulkanHelper.flagsToInt( VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT, VK10.VK_IMAGE_USAGE_SAMPLED_BIT ) )
            .sharingMode( device.isSingleCommandPoolUsed()
                ? VK10.VK_SHARING_MODE_EXCLUSIVE
                : VK10.VK_SHARING_MODE_CONCURRENT
            )
            .samples( VK10.VK_SAMPLE_COUNT_1_BIT )
            .flags( 0 )
        ;
        
        textureData.setTextureImage( VulkanHelper.createAddress(
            address -> VK10.vkCreateImage( device.getLogicalDevice(), imageCreateInfo, null, address ),
            resultCode -> new CannotCreateVulkanImageException( device, resultCode )
        ) );
        
        VkMemoryRequirements memoryRequirements = VkMemoryRequirements.create();
        VK10.vkGetImageMemoryRequirements(
            device.getLogicalDevice(), textureData.getTextureImage().getValue(), memoryRequirements );
        
        VulkanMemoryType memoryType = device.selectMemoryType(
            memoryRequirements.memoryTypeBits(),
            VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT
        );
        
        VkMemoryAllocateInfo allocateInfo = VkMemoryAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO )
            .allocationSize( memoryRequirements.size() )
            .memoryTypeIndex( memoryType.getIndex() )
            ;
        
        VulkanAddress textureImageMemory = VulkanHelper.createAddress(
            address -> VK10.vkAllocateMemory( device.getLogicalDevice(), allocateInfo, null, address ),
            resultCode -> new CannotAllocateVulkanImageMemoryException( device, resultCode )
        );
        
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkBindImageMemory(
                device.getLogicalDevice(),
                textureData.getTextureImage().getValue(),
                textureImageMemory.getValue(),
                0
            ), resultCode -> new CannotBindVulkanImageMemoryException( device, resultCode )
        );
    }
    
    @Override
    public void delete( VulkanTexture textureData ) {
    
    }
    
    @Override
    public void close() throws Exception {
    
    }
}
