/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.*;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferData;

import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
public class VulkanDeviceAllocator {
    
    private final VulkanFrameworkAllocator allocator;
    private final VulkanPhysicalDevice device;
    
    private final VulkanImmutableObjectsRegistry< VulkanFrameBuffer > frameBuffers = new VulkanImmutableObjectsRegistry<>( VulkanFrameBuffer::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanTransferCommandPool > transferCommandPools = new VulkanImmutableObjectsRegistry<>( VulkanTransferCommandPool::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanGraphicCommandPool > graphicCommandPools = new VulkanReusableObjectsRegistry<>( VulkanGraphicCommandPool::disposeUnsafe, pool -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanGraphicPipelines > graphicPipelines = new VulkanImmutableObjectsRegistry<>( VulkanGraphicPipelines::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanSwapChain > swapChains = new VulkanImmutableObjectsRegistry<>( VulkanSwapChain::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanBufferProvider > bufferProviders = new VulkanImmutableObjectsRegistry<>( VulkanBufferProvider::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDescriptor > descriptors = new VulkanImmutableObjectsRegistry<>( VulkanDescriptor::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDescriptorPool > descriptorPools = new VulkanImmutableObjectsRegistry<>( VulkanDescriptorPool::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDescriptorSet > descriptorSets = new VulkanImmutableObjectsRegistry<>( VulkanDescriptorSet::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanTextureLoader > textureLoaders = new VulkanImmutableObjectsRegistry<>( VulkanTextureLoader::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDepthResources > depthResources = new VulkanImmutableObjectsRegistry<>( VulkanDepthResources::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanShaderProgram > shaderPrograms = new VulkanImmutableObjectsRegistry<>( VulkanShaderProgram::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanTextureSampler > textureSamplers = new VulkanImmutableObjectsRegistry<>(VulkanTextureSampler::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanImageView > imageViews = new VulkanImmutableObjectsRegistry<>( VulkanImageView::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanInMemoryImage > inMemoryImages = new VulkanReusableObjectsRegistry<>( VulkanInMemoryImage::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanSwapChainImage > swapChainImages = new VulkanReusableObjectsRegistry<>( VulkanSwapChainImage::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanSemaphore > semaphores = new VulkanImmutableObjectsRegistry<>( VulkanSemaphore::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanTextureData > textureDatas = new VulkanReusableObjectsRegistry<>( VulkanTextureData::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanFence > fences = new VulkanReusableObjectsRegistry<>( VulkanFence::disposeUnsafe, fence -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanCommandBufferAllocator<?> > commandBufferAllocators = new VulkanImmutableObjectsRegistry<>( VulkanCommandBufferAllocator::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanTransferData > transferDatas = new VulkanReusableObjectsRegistry<>( data -> {}, data -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanPipelineAllocator > pipelineAllocators = new VulkanImmutableObjectsRegistry<>( VulkanPipelineAllocator::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDataBufferAllocator > dataBufferAllocators = new VulkanImmutableObjectsRegistry<>( VulkanDataBufferAllocator::disposeUnsafe );
    
    
    public VulkanDeviceAllocator(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this.allocator = allocator;
        this.device = device;
    }
    
    public void disposeUnsafe() {
        frameBuffers.dispose();
        transferCommandPools.dispose();
        graphicCommandPools.dispose();
        graphicPipelines.dispose();
        swapChains.dispose();
        bufferProviders.dispose();
        descriptors.dispose();
        textureLoaders.dispose();
        textureDatas.dispose();
        inMemoryImages.dispose();
        swapChainImages.dispose();
        imageViews.dispose();
        
        depthResources.dispose();
        shaderPrograms.dispose();
        textureSamplers.dispose();
        semaphores.dispose();
        fences.dispose();
        commandBufferAllocators.dispose();
        pipelineAllocators.dispose();
        dataBufferAllocators.dispose();
    }
    
    public VulkanDataBufferAllocator createDataBufferAllocator() {
        return dataBufferAllocators.register( () -> new VulkanDataBufferAllocator( this ) );
    }
    
    void deregisterDataBufferAllocator( VulkanDataBufferAllocator dataBufferAllocator ) {
        dataBufferAllocators.deregister( dataBufferAllocator );
    }
    
    public VulkanPipelineAllocator createPipelineAllocator(
        VulkanAddress pipelineLayout,
        int topologyType,
        boolean pipelinesFirstUseOnly,
        boolean keepInitialDepthAttachment
    ) {
        return pipelineAllocators.register( () -> new VulkanPipelineAllocator(
            this,
            device,
            pipelineLayout,
            topologyType,
            pipelinesFirstUseOnly,
            keepInitialDepthAttachment
        ) );
    }
    
    void deregisterPipelineAllocator( VulkanPipelineAllocator pipelineAllocator ) {
        pipelineAllocators.deregister( pipelineAllocator );
    }
    
    public VulkanTransferData createTransferData(
        VulkanAddress source,
        long sourceOffset,
        VulkanAddress destination,
        long destinationOffset,
        long length
    ) {
        return transferDatas.register(
            VulkanTransferData::new,
            data -> data.set( source, sourceOffset, destination, destinationOffset, length )
        );
    }
    
    public void deregisterTransferData( VulkanTransferData image ) {
        transferDatas.deregister( image );
    }
    
    @SuppressWarnings( "unchecked" )
    public < CommandBuffer extends VulkanCommandBuffer< ? > > VulkanCommandBufferAllocator< CommandBuffer > createBufferAllocator(
        Supplier< CommandBuffer > supplier
    ) {
        return ( VulkanCommandBufferAllocator< CommandBuffer > ) commandBufferAllocators.register(
            () -> new VulkanCommandBufferAllocator<>( this, supplier )
        );
    }
    
    void deregisterBufferAllocator( VulkanCommandBufferAllocator< ? > allocator ) {
        commandBufferAllocators.deregister( allocator );
    }
    
    public VulkanFence createFence() {
        return fences.register(
            () -> new VulkanFence( this, device ),
            VulkanFence::update
        );
    }
    
    public void deregisterFence( VulkanFence image ) {
        fences.deregister( image );
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
        swapChainImages.deregister( image );
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
        inMemoryImages.deregister( image );
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
        imageViews.deregister( sampler );
    }
    
    public VulkanTextureData createTextureData() {
        return textureDatas.register( () -> new VulkanTextureData( this ), data -> {} );
    }
    
    public void deregisterTextureData( VulkanTextureData textureData ) {
        textureDatas.deregister( textureData );
    }
    
    public VulkanSemaphore createSemaphore() {
        return semaphores.register(
            () -> new VulkanSemaphore( this, device )
        );
    }
    
    public void deregisterSemaphore( VulkanSemaphore semaphore ) {
        semaphores.deregister( semaphore );
    }
    
    public VulkanTextureSampler createTextureSampler() {
        return textureSamplers.register(
            () -> new VulkanTextureSampler( this, device )
        );
    }
    
    public void deregisterTextureSampler( VulkanTextureSampler sampler ) {
        textureSamplers.deregister( sampler );
    }
    
    public VulkanShaderProgram createShaderProgram( VulkanBufferProvider bufferLoader ) {
        return shaderPrograms.register(
            () -> new VulkanShaderProgram( this, device, bufferLoader )
        );
    }
    
    public void deregisterShaderProgram( VulkanShaderProgram shader ) {
        shaderPrograms.deregister( shader );
    }
    
    public VulkanDepthResources createDepthResource() {
        return depthResources.register(
            () -> new VulkanDepthResources( this, device )
        );
    }
    
    public void deregisterDepthResource( VulkanDepthResources depthResources ) {
        this.depthResources.deregister( depthResources );
    }
    
    public VulkanTextureLoader createTextureLoader( VulkanBufferProvider bufferLoader ) {
        return textureLoaders.register(
            () -> new VulkanTextureLoader( this, device, bufferLoader )
        );
    }
    
    public void deregisterTextureLoader( VulkanTextureLoader textureLoader ) {
        textureLoaders.deregister( textureLoader );
    }
    
    public VulkanDescriptorSet createDescriptorSet(
        VulkanDescriptor descriptor,
        VulkanDescriptorPool pool
    ) {
        return descriptorSets.register(
            () -> new VulkanDescriptorSet( this, descriptor, pool )
        );
    }
    
    public void deregisterDescriptorSet( VulkanDescriptorSet set ) {
        descriptorSets.deregister( set );
    }
    
    public VulkanDescriptorPool createDescriptorPool(
        VulkanDescriptor descriptor,
        int sets
    ) {
        return descriptorPools.register(
            () -> new VulkanDescriptorPool( this, device, descriptor, sets )
        );
    }
    
    public void deregisterDescriptorPool( VulkanDescriptorPool descriptor ) {
        descriptorPools.deregister( descriptor );
    }
    
    public VulkanDescriptor createDescriptor() {
        return descriptors.register(
            () -> new VulkanDescriptor( this, device )
        );
    }
    
    public void deregisterDescriptor( VulkanDescriptor descriptor ) {
        descriptors.deregister( descriptor );
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
        bufferProviders.deregister( bufferProvider );
    }
    
    public VulkanSwapChain createSwapChain() {
        return swapChains.register(
            () -> new VulkanSwapChain( this, device )
        );
    }
    
    public void deregisterSwapChain( VulkanSwapChain swapChain ) {
        swapChains.deregister( swapChain );
    }
    
    public VulkanGraphicPipelines createGraphicPipelines( int topologyType ) {
        return graphicPipelines.register(
            () -> new VulkanGraphicPipelines( this, device, topologyType )
        );
    }
    
    public void deregisterGraphicPipelines( VulkanGraphicPipelines pipelines ) {
        graphicPipelines.deregister( pipelines );
    }
    
    public VulkanGraphicCommandPool createGraphicCommandPool() {
        return graphicCommandPools.register(
            () -> new VulkanGraphicCommandPool( this, device ),
            pool -> {}
        );
    }
    
    public void deregisterGraphicCommandPool( VulkanGraphicCommandPool pool ) {
        graphicCommandPools.deregister( pool );
    }
    
    public VulkanTransferCommandPool createTransferCommandPool(
        VulkanQueueFamily usedQueueFamily
    ) {
        return transferCommandPools.register(
            () -> new VulkanTransferCommandPool( this, device, usedQueueFamily )
        );
    }
    
    public void deregisterTransferCommandPool( VulkanTransferCommandPool pool ) {
        transferCommandPools.deregister( pool );
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
        frameBuffers.deregister( buffer );
    }
    
    public void dispose() {
        allocator.deregisterPhysicalDeviceAllocator( this );
    }
}
