/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.*;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;

/**
 * @author n1t4chi
 */
public class VulkanDeviceAllocator {
    
    private final VulkanFrameworkAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final VulkanImmutableObjectsRegistry< VulkanFrameBuffer > frameBuffers = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanTransferCommandPool > transferCommandPools = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanGraphicCommandPool > graphicCommandPools = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanGraphicPipelines > graphicPipelines = new VulkanImmutableObjectsRegistry<>();
    
    private final VulkanImmutableObjectsRegistry< VulkanSwapChain > swapChains = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanBufferProvider > bufferProviders = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanDescriptor > descriptors = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanTextureLoader > textureLoaders = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanDepthResources > depthResources = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanShaderProgram > shaderPrograms3D = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanTextureSampler > textureSamplers = new VulkanImmutableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanImageView > imageViews = new VulkanImmutableObjectsRegistry<>();
    private final VulkanReusableObjectsRegistry< VulkanInMemoryImage > inMemoryImages = new VulkanReusableObjectsRegistry<>();
    private final VulkanReusableObjectsRegistry< VulkanSwapChainImage > swapChainImages = new VulkanReusableObjectsRegistry<>();
    private final VulkanImmutableObjectsRegistry< VulkanSemaphore > semaphores = new VulkanImmutableObjectsRegistry<>();
    private final VulkanReusableObjectsRegistry< VulkanTextureData > textureDatas = new VulkanReusableObjectsRegistry<>();
    
    
    public VulkanDeviceAllocator(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this.allocator = allocator;
        this.device = device;
    }
    
    public void disposeUnsafe() {
        frameBuffers.forEach( this::deregisterFrameBuffer );
        transferCommandPools.forEach( this::deregisterTransferCommandPool );
        graphicCommandPools.forEach( this::deregisterGraphicCommandPool );
        graphicPipelines.forEach( this::deregisterGraphicPipelines );
        swapChains.forEach( this::deregisterSwapChain );
        bufferProviders.forEach( this::deregisterBufferProvider );
        descriptors.forEach( this::deregisterDescriptor );
        textureLoaders.forEach( this::deregisterTextureLoader );
        textureDatas.forEach( this::deregisterTextureData );
        inMemoryImages.forEach( this::deregisterInMemoryImage );
        swapChainImages.forEach( this::deregisterSwapChainImage );
        imageViews.forEach( this::deregisterImageView );
        
        depthResources.forEach( this::deregisterDepthResource );
        shaderPrograms3D.forEach( this::deregisterShaderProgram3D );
        textureSamplers.forEach( this::deregisterTextureSampler );
        semaphores.forEach( this::deregisterSemaphore );
    }
    
    public VulkanSwapChainImage createSwapChainImage(
        VulkanAddress address,
        int index
    ) {
        return swapChainImages.register(
            () -> new VulkanSwapChainImage( this, device ),
            image -> image.update( address, index )
        );
    }
    
    public void deregisterSwapChainImage( VulkanSwapChainImage image ) {
        swapChainImages.deregister( image, VulkanSwapChainImage::disposeUnsafe );
    }
    
    public VulkanInMemoryImage createInMemoryImage(
        int width,
        int height,
        int format,
        int tiling,
        int[] usageFlags,
        int[] desiredMemoryFlags
    ) {
        return inMemoryImages.register(
            () -> new VulkanInMemoryImage( this, device ),
            image -> image.update(
                width,
                height,
                format,
                tiling,
                usageFlags,
                desiredMemoryFlags
            )
        );
    }
    
    public void deregisterInMemoryImage( VulkanInMemoryImage image ) {
        inMemoryImages.deregister( image, VulkanInMemoryImage::disposeUnsafe );
    }
    
    public VulkanImageView createImageView(
        VulkanImage image,
        int format,
        int aspectMask,
        int mipLevels
    ) {
        return imageViews.register(
            () -> new VulkanImageView( this, device, image, format, aspectMask, mipLevels )
        );
    }
    
    public void deregisterImageView( VulkanImageView sampler ) {
        imageViews.deregister( sampler, VulkanImageView::disposeUnsafe );
    }
    
    public VulkanTextureData createTextureData() {
        return textureDatas.register( () -> new VulkanTextureData( this ), data -> {} );
    }
    
    public void deregisterTextureData( VulkanTextureData textureData ) {
        textureDatas.deregister( textureData, VulkanTextureData::disposeUnsafe );
    }
    
    public VulkanSemaphore createSemaphore() {
        return semaphores.register(
            () -> new VulkanSemaphore( this, device )
        );
    }
    
    public void deregisterSemaphore( VulkanSemaphore semaphore ) {
        semaphores.deregister( semaphore, VulkanSemaphore::disposeUnsafe );
    }
    
    public VulkanTextureSampler createTextureSampler() {
        return textureSamplers.register(
            () -> new VulkanTextureSampler( this, device )
        );
    }
    
    public void deregisterTextureSampler( VulkanTextureSampler sampler ) {
        textureSamplers.deregister( sampler, VulkanTextureSampler::disposeUnsafe );
    }
    
    public VulkanShaderProgram createShaderProgram3D( VulkanBufferProvider bufferLoader ) {
        return shaderPrograms3D.register(
            () -> new VulkanShaderProgram( this, device, bufferLoader )
        );
    }
    
    public void deregisterShaderProgram3D( VulkanShaderProgram shader ) {
        shaderPrograms3D.deregister( shader, VulkanShaderProgram::disposeUnsafe );
    }
    
    public VulkanDepthResources createDepthResource() {
        return depthResources.register(
            () -> new VulkanDepthResources( this, device )
        );
    }
    
    public void deregisterDepthResource( VulkanDepthResources depthResources ) {
        this.depthResources.deregister( depthResources, VulkanDepthResources::disposeUnsafe );
    }
    
    public VulkanTextureLoader createTextureLoader( VulkanBufferProvider bufferLoader ) {
        return textureLoaders.register(
            () -> new VulkanTextureLoader( this, device, bufferLoader )
        );
    }
    
    public void deregisterTextureLoader( VulkanTextureLoader textureLoader ) {
        textureLoaders.deregister( textureLoader, VulkanTextureLoader::disposeUnsafe );
    }
    
    public VulkanDescriptor createDescriptor() {
        return descriptors.register(
            () -> new VulkanDescriptor( this, device )
        );
    }
    
    public void deregisterDescriptor( VulkanDescriptor descriptor ) {
        descriptors.deregister( descriptor, VulkanDescriptor::disposeUnsafe );
    }
    
    
    public VulkanBufferProvider createBufferProvider(
        VulkanTransferCommandPool vertexDataTransferCommandPool,
        VulkanTransferCommandPool quickDataTransferCommandPool,
        VulkanTransferCommandPool uniformDataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool,
        long uniformBufferOffsetAlignment
    ) {
        return bufferProviders.register( () -> new VulkanBufferProvider(
            this,
            device,
            vertexDataTransferCommandPool,
            quickDataTransferCommandPool,
            uniformDataTransferCommandPool,
            textureTransferCommandPool, uniformBufferOffsetAlignment
        ) );
    }
    
    public void deregisterBufferProvider( VulkanBufferProvider bufferProvider ) {
        bufferProviders.deregister( bufferProvider, VulkanBufferProvider::disposeUnsafe );
    }
    
    public VulkanSwapChain createSwapChain() {
        return swapChains.register(
            () -> new VulkanSwapChain( this, device )
        );
    }
    
    public void deregisterSwapChain( VulkanSwapChain swapChain ) {
        swapChains.deregister( swapChain, VulkanSwapChain::disposeUnsafe );
    }
    
    public void dispose() {
        allocator.deregisterPhysicalDeviceAllocator( this );
    }
    
    public VulkanGraphicPipelines createGraphicPipelines(
        int topologyType
    ) {
        return graphicPipelines.register(
            () -> new VulkanGraphicPipelines( this, device, topologyType )
        );
    }
    
    public void deregisterGraphicPipelines( VulkanGraphicPipelines pipelines ) {
        graphicPipelines.deregister( pipelines, VulkanGraphicPipelines::disposeUnsafe );
    }
    
    public VulkanGraphicCommandPool createGraphicCommandPool(
        VulkanQueueFamily usedQueueFamily
    ) {
        return graphicCommandPools.register(
            () -> new VulkanGraphicCommandPool( this, device, usedQueueFamily )
        );
    }
    
    public void deregisterGraphicCommandPool( VulkanGraphicCommandPool pool ) {
        graphicCommandPools.deregister( pool, VulkanGraphicCommandPool::disposeUnsafe );
    }
    
    public VulkanTransferCommandPool createTransferCommandPool(
        VulkanQueueFamily usedQueueFamily
    ) {
        return transferCommandPools.register(
            () -> new VulkanTransferCommandPool( this, device, usedQueueFamily )
        );
    }
    
    public void deregisterTransferCommandPool( VulkanTransferCommandPool pool ) {
        transferCommandPools.deregister( pool, VulkanTransferCommandPool::disposeUnsafe );
    }
    
    public VulkanFrameBuffer createFrameBuffer(
        VulkanImageView imageView,
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        VulkanDepthResources depthResources
    ) {
        return frameBuffers.register( () -> new VulkanFrameBuffer(
            this,
            device,
            imageView,
            renderPass,
            swapChain,
            depthResources
        ) );
    }
    
    public void deregisterFrameBuffer( VulkanFrameBuffer buffer ) {
        frameBuffers.deregister( buffer, VulkanFrameBuffer::disposeUnsafe );
    }
    
}
