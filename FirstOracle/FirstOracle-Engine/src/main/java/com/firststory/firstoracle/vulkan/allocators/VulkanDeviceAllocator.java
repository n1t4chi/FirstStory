/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.allocators;

import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.*;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.*;

import java.util.*;
import java.util.function.*;

/**
 * @author n1t4chi
 */
public class VulkanDeviceAllocator {
    
    private final VulkanFrameworkAllocator allocator;
    private final VulkanPhysicalDevice device;
    
    private final List< VulkanRegistry > registryList = new ArrayList<>();
    
    private final VulkanImmutableObjectsRegistry< VulkanFrameBuffer > frameBuffers = newImmutableRegistry( VulkanFrameBuffer::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanTransferCommandPool > transferCommandPools = newImmutableRegistry( VulkanTransferCommandPool::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanGraphicCommandPool > graphicCommandPools = newReusableRegistry( VulkanGraphicCommandPool::disposeUnsafe, pool -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanGraphicPipelines > graphicPipelines = newImmutableRegistry( VulkanGraphicPipelines::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanSwapChain > swapChains = newImmutableRegistry( VulkanSwapChain::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanBufferProvider > bufferProviders = newImmutableRegistry( VulkanBufferProvider::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDescriptor > descriptors = newImmutableRegistry( VulkanDescriptor::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDescriptorPool > descriptorPools = newImmutableRegistry( VulkanDescriptorPool::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDescriptorSet > descriptorSets = newImmutableRegistry( VulkanDescriptorSet::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanTextureLoader > textureLoaders = newImmutableRegistry( VulkanTextureLoader::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDepthResources > depthResources = newImmutableRegistry( VulkanDepthResources::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanShaderProgram > shaderPrograms = newImmutableRegistry( VulkanShaderProgram::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanTextureSampler > textureSamplers = newReusableRegistry(VulkanTextureSampler::disposeUnsafe, pool -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanImageView > imageViews = newImmutableRegistry( VulkanImageView::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanInMemoryImage > inMemoryImages = newReusableRegistry( VulkanInMemoryImage::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanSwapChainImage > swapChainImages = newReusableRegistry( VulkanSwapChainImage::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanSemaphore > semaphores = newImmutableRegistry( VulkanSemaphore::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanTextureData > textureDatas = newReusableRegistry( VulkanTextureData::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanFence > fences = newReusableRegistry( VulkanFence::disposeUnsafe, fence -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanCommandBufferAllocator<?> > commandBufferAllocators = newImmutableRegistry( VulkanCommandBufferAllocator::disposeUnsafe );
    private final VulkanReusableObjectsRegistry< VulkanTransferData > transferDatas = newReusableRegistry( data -> {}, data -> {} );
    private final VulkanImmutableObjectsRegistry< VulkanPipelineAllocator > pipelineAllocators = newImmutableRegistry( VulkanPipelineAllocator::disposeUnsafe );
    private final VulkanImmutableObjectsRegistry< VulkanDataBufferAllocator > dataBufferAllocators = newImmutableRegistry( VulkanDataBufferAllocator::disposeUnsafe );
    
    private <T> VulkanImmutableObjectsRegistry< T > newImmutableRegistry( Consumer< T > disposeAction ) {
        var registry = new VulkanImmutableObjectsRegistry<>( disposeAction );
        registryList.add( registry );
        return registry;
    }
    
    private <T> VulkanReusableObjectsRegistry< T > newReusableRegistry( Consumer< T > disposeFreeAction ) {
        var registry = new VulkanReusableObjectsRegistry<>( disposeFreeAction );
        registryList.add( registry );
        return registry;
    }
    
    private <T> VulkanReusableObjectsRegistry< T > newReusableRegistry( Consumer< T > disposeFreeAction, Consumer<T> disposeUsedAction ) {
        var registry = new VulkanReusableObjectsRegistry<>( disposeFreeAction, disposeUsedAction );
        registryList.add( registry );
        return registry;
    }
    
    
    
    
    public VulkanDeviceAllocator(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this.allocator = allocator;
        this.device = device;
    }
    
    public void disposeUnsafe() {
        registryList.forEach( VulkanRegistry::dispose );
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
            () -> new VulkanTextureSampler( this, device ),
            vulkanTextureSampler -> {}
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
