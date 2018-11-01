/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.buffer.BufferNotCreatedException;
import com.firststory.firstoracle.buffer.TextureBufferLoader;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
public class VulkanTextureLoader implements TextureBufferLoader< VulkanTextureData > {
    
    private final HashSet< VulkanTextureData > textureDatas = new HashSet<>();
    private final VulkanPhysicalDevice device;
    private final VulkanBufferProvider bufferLoader;
    private final VulkanDeviceAllocator allocator;
    
    public VulkanTextureLoader(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanBufferProvider bufferLoader
    ) {
        this.allocator = allocator;
        this.device = device;
        this.bufferLoader = bufferLoader;
    }
    
    @Override
    public VulkanTextureData create() {
        var textureData = allocator.createTextureData();
        this.textureDatas.add( textureData );
        return textureData;
    }
    
    @Override
    public void bindUnsafe( VulkanTextureData textureData ) {}
    
    @Override
    public void loadUnsafe( VulkanTextureData textureData, ByteBuffer imageBuffer, String name ) {
        createTextureImage( textureData, imageBuffer, name );
        createTextureImageView( textureData );
    }
    
    @Override
    public void deleteUnsafe( VulkanTextureData textureData ) {
        this.textureDatas.remove( textureData );
        textureData.dispose();
    }
    
    public void dispose() {
        allocator.deregisterTextureLoader( this );
    }
    
    public void disposeUnsafe() {
        this.textureDatas.forEach( VulkanTextureData::dispose );
        this.textureDatas.clear();
    }
    
    private void createTextureImageView( VulkanTextureData textureData ) {
        textureData.setImageView(
            textureData.getImage().createImageView( VK10.VK_FORMAT_R8G8B8A8_UNORM, VK10.VK_IMAGE_ASPECT_COLOR_BIT ) );
    }
    
    private void createTextureImage( VulkanTextureData textureData, ByteBuffer imageBuffer, String name ) {
        var w = BufferUtils.createIntBuffer( 1 );
        var h = BufferUtils.createIntBuffer( 1 );
        var c = BufferUtils.createIntBuffer( 1 );
        var pixels = STBImage.stbi_load_from_memory( imageBuffer, w, h, c, STBImage.STBI_rgb_alpha );
        if ( pixels == null ) {
            throw new BufferNotCreatedException( "Cannot load image:" + name );
        }
        var width = w.get( 0 );
        var height = h.get( 0 );
        textureData.setWidth( width );
        textureData.setHeight( height );
        textureData.setBuffer( bufferLoader.createTextureBuffer( pixels ) );
        MemoryUtil.memFree( pixels );
        createImage( textureData );
        
        initialTransitionImageLayout( textureData, VK10.VK_FORMAT_R8G8B8A8_UNORM );
        copyBufferToImage( textureData );
        textureData.getImage().createMipMaps();
    }
    
    private void copyBufferToImage( VulkanTextureData textureData ) {
        device.getTextureTransferCommandPool().addTransferCommands( commandBuffer -> {
            var region = VkBufferImageCopy.calloc()
                .bufferOffset( textureData.getBuffer().getMemoryOffset() )
                .bufferRowLength( 0 )
                .bufferImageHeight( 0 )
                .imageSubresource( VkImageSubresourceLayers.calloc()
                    .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                    .mipLevel( 0 )
                    .baseArrayLayer( 0 )
                    .layerCount( 1 ) )
                .imageOffset( VkOffset3D.calloc().set( 0, 0, 0 ) )
                .imageExtent( VkExtent3D.calloc().set( textureData.getWidth(), textureData.getHeight(), 1 ) );
    
            var regionBuffer = VkBufferImageCopy.calloc( 1 ).put( 0, region );
            
            VK10.vkCmdCopyBufferToImage(
                commandBuffer.getCommandBuffer(),
                textureData.getBuffer().getBufferAddress().getValue(),
                textureData.getImage().getAddress().getValue(),
                VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                regionBuffer
            );
            regionBuffer.free();
        } );
    }
    
    private void initialTransitionImageLayout( VulkanTextureData textureData, int format ) {
        textureData.getImage()
            .transitionImageLayout(
                format,
                VK10.VK_IMAGE_LAYOUT_UNDEFINED,
                VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL
            );
    }
    
    private void createImage( VulkanTextureData textureData ) {
        textureData.setImage( allocator.createInMemoryImage(
            textureData.getWidth(),
            textureData.getHeight(),
            VK10.VK_FORMAT_R8G8B8A8_UNORM,
            VK10.VK_IMAGE_TILING_OPTIMAL,
            new int[]{
                VK10.VK_IMAGE_USAGE_TRANSFER_SRC_BIT,
                VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT,
                VK10.VK_IMAGE_USAGE_SAMPLED_BIT
            },
            new int[]{ VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT }
        ) );
    }
}
