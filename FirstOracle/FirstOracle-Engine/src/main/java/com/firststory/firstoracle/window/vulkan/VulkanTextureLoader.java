/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.data.BufferNotCreatedException;
import com.firststory.firstoracle.data.TextureBufferLoader;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;

/**
 * @author n1t4chi
 */
public class VulkanTextureLoader implements TextureBufferLoader<VulkanTextureData > {
    
    private final HashSet<VulkanTextureData> textureData = new HashSet<>(  );
    private final VulkanPhysicalDevice device;
    private final VulkanDataBufferProvider bufferLoader;
    private final VulkanAddress textureSampler;
    private final VulkanAddress descriptorSet;
    
    VulkanTextureLoader(
        VulkanPhysicalDevice physicalDevice,
        VulkanDataBufferProvider bufferLoader,
        VulkanAddress textureSampler,
        VulkanAddress descriptorSet
    ) {
        
        device = physicalDevice;
        this.bufferLoader = bufferLoader;
        this.textureSampler = textureSampler;
        this.descriptorSet = descriptorSet;
    }
    
    @Override
    public VulkanTextureData create() {
        VulkanTextureData textureData = new VulkanTextureData();
        this.textureData.add( textureData );
        return textureData;
    }
    
    @Override
    public void bind( VulkanTextureData textureData ) {
    }
    
    @Override
    public void load( VulkanTextureData textureData, ByteBuffer imageBuffer, String name ) {
        createTextureImage( textureData, imageBuffer, name );
        createTextureImageView( textureData );
    }
    
    private void createTextureImageView( VulkanTextureData textureData ) {
        textureData.setImageView( textureData.getImage().createImageView( VK10.VK_FORMAT_R8G8B8A8_UNORM,
            VK10.VK_IMAGE_ASPECT_COLOR_BIT
        ) );
        
    }
    
    private void createTextureImage( VulkanTextureData textureData, ByteBuffer imageBuffer, String name ) {
        IntBuffer w = BufferUtils.createIntBuffer( 1 );
        IntBuffer h = BufferUtils.createIntBuffer( 1 );
        IntBuffer c = BufferUtils.createIntBuffer( 1 );
        ByteBuffer pixels = STBImage.stbi_load_from_memory( imageBuffer, w, h, c, STBImage.STBI_rgb_alpha );
        if ( pixels == null ) {
            throw new BufferNotCreatedException( "Cannot load image:" + name );
        }
        int width = w.get( 0 );
        int height = h.get( 0 );
        textureData.setWidth( width );
        textureData.setHeight( height );
        textureData.setBuffer( bufferLoader.createMappableBuffer() );
        textureData.getBuffer().createBuffer( pixels.capacity(), 1 );
        textureData.getBuffer().load( pixels );
        textureData.getBuffer().bind();
        
        createImage( textureData );
    
        initialTransitionImageLayout(
            textureData,
            VK10.VK_FORMAT_R8G8B8A8_UNORM
        );
        copyBufferToImage( textureData );
        postCopyTransitionImageLayout(
            textureData,
            VK10.VK_FORMAT_R8G8B8A8_UNORM
        );
    }
    
    private void copyBufferToImage( VulkanTextureData textureData ) {
        device.getTextureTransferCommandPool().executeQueue( commandBuffer -> {
            VkBufferImageCopy region = VkBufferImageCopy.create()
                .bufferOffset(0)
                .bufferRowLength(0)
                .bufferImageHeight(0)
                .imageSubresource( VkImageSubresourceLayers.create()
                    .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                    .mipLevel(0)
                    .baseArrayLayer(0)
                    .layerCount(1)
                )
                .imageOffset( VkOffset3D.create().set( 0,0,0 ) )
                .imageExtent( VkExtent3D.create().set( textureData.getWidth(), textureData.getHeight(), 1 ) )
            ;
    
            VkBufferImageCopy.Buffer regionBuffer = VkBufferImageCopy.calloc( 1 ).put( 0, region );
    
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
        textureData.getImage().transitionImageLayout(
            format,
            VK10.VK_IMAGE_LAYOUT_UNDEFINED,
            VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL
//            0,
//            VK10.VK_ACCESS_TRANSFER_WRITE_BIT,
//            VK10.VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT,
//            VK10.VK_PIPELINE_STAGE_TRANSFER_BIT
        );
    }
    
    private void postCopyTransitionImageLayout( VulkanTextureData textureData, int format ) {
        textureData.getImage().transitionImageLayout(
            format,
            VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
            VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
//            VK10.VK_ACCESS_TRANSFER_WRITE_BIT,
//            VK10.VK_ACCESS_SHADER_READ_BIT,
//            VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
//            VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT
        );
    }
    
    private void createImage( VulkanTextureData textureData ) {
        textureData.setImage( new VulkanInMemoryImage(
            this.device,
            textureData.getWidth(),
            textureData.getHeight(),
            VK10.VK_FORMAT_R8G8B8A8_UNORM,
            VK10.VK_IMAGE_TILING_OPTIMAL,
            new int[]{ VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT, VK10.VK_IMAGE_USAGE_SAMPLED_BIT },
            new int[]{ VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT }
        ) );
    }
    
    @Override
    public void delete( VulkanTextureData textureData ) {
        this.textureData.remove( textureData );
        textureData.close();
    }
    
    @Override
    public void close() {
        this.textureData.forEach( VulkanTextureData::close );
        this.textureData.clear();
    }
}
