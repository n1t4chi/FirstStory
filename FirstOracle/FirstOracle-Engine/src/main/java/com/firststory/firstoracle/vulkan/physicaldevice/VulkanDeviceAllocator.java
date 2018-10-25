/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.vulkan.VulkanFrameworkAllocator;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.*;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @author n1t4chi
 */
public class VulkanDeviceAllocator {
    
    private final VulkanFrameworkAllocator allocator;
    private final Set< VulkanFrameBuffer > frameBuffers = new HashSet<>();
    private final Set< VulkanTransferCommandPool > transferCommandPools = new HashSet<>();
    private final Set< VulkanGraphicCommandPool > graphicCommandPools = new HashSet<>();
    private final Set< VulkanGraphicPipelines > graphicPipelines = new HashSet<>();
    
    private final Set< VulkanSwapChain > swapChains = new HashSet<>();
    private final Set< VulkanBufferProvider > bufferProviders = new HashSet<>();
    private final Set< VulkanDescriptor > descriptors = new HashSet<>();
    private final Set< VulkanTextureLoader > textureLoaders = new HashSet<>();
    private final Set< VulkanDepthResources > depthResourcess = new HashSet<>();
    private final Set< VulkanShaderProgram > shaderPrograms3D = new HashSet<>();
    private final Set< VulkanTextureSampler > textureSamplers = new HashSet<>();
    private final Set< VulkanSemaphore > semaphores = new HashSet<>();
    
    public VulkanDeviceAllocator( VulkanFrameworkAllocator allocator ) {
        this.allocator = allocator;
    }
    
    public void disposeUnsafe() {
        VulkanHelper.safeForEach( frameBuffers, this::deregisterFrameBuffer );
        VulkanHelper.safeForEach( transferCommandPools, this::deregisterTransferCommandPool );
        VulkanHelper.safeForEach( graphicCommandPools, this::deregisterGraphicCommandPool );
        VulkanHelper.safeForEach( graphicPipelines, this::deregisterGraphicPipelines );
        VulkanHelper.safeForEach( swapChains, this::deregisterSwapChain );
        VulkanHelper.safeForEach( bufferProviders, this::deregisterBufferProvider );
        VulkanHelper.safeForEach( descriptors, this::deregisterDescriptor );
        VulkanHelper.safeForEach( textureLoaders, this::deregisterTextureLoader );
        VulkanHelper.safeForEach( depthResourcess, this::deregisterDepthResource );
        VulkanHelper.safeForEach( shaderPrograms3D, this::deregisterShaderProgram3D );
        VulkanHelper.safeForEach( textureSamplers, this::deregisterTextureSampler );
        VulkanHelper.safeForEach( semaphores, this::deregisterSemaphore );
    }
    
    public VulkanSemaphore createSemaphore( VulkanPhysicalDevice device ) {
        return VulkanHelper.register( semaphores,
            () -> new VulkanSemaphore( this, device )
        );
    }
    
    void deregisterSemaphore( VulkanSemaphore semaphore ) {
        VulkanHelper.deregister( semaphores, semaphore, VulkanSemaphore::disposeUnsafe );
    }
    
    VulkanTextureSampler createTextureSampler( VulkanPhysicalDevice device ) {
        return VulkanHelper.register( textureSamplers,
            () -> new VulkanTextureSampler( this, device )
        );
    }
    
    public void deregisterTextureSampler( VulkanTextureSampler sampler ) {
        VulkanHelper.deregister( textureSamplers, sampler, VulkanTextureSampler::disposeUnsafe );
    }
    
    VulkanShaderProgram createShaderProgram3D( VulkanPhysicalDevice device, VulkanBufferProvider bufferLoader ) {
        return VulkanHelper.register( shaderPrograms3D,
            () -> new VulkanShaderProgram( this, device, bufferLoader )
        );
    }
    
    public void deregisterShaderProgram3D( VulkanShaderProgram shader ) {
        VulkanHelper.deregister( shaderPrograms3D, shader, VulkanShaderProgram::disposeUnsafe );
    }
    
    VulkanDepthResources createDepthResource( VulkanPhysicalDevice device ) {
        return VulkanHelper.register( depthResourcess,
            () -> new VulkanDepthResources( this, device )
        );
    }
    
    void deregisterDepthResource( VulkanDepthResources depthResources ) {
        VulkanHelper.deregister( depthResourcess, depthResources, VulkanDepthResources::disposeUnsafe );
    }
    
    VulkanTextureLoader createTextureLoader( VulkanPhysicalDevice device, VulkanBufferProvider bufferLoader ) {
        return VulkanHelper.register( textureLoaders,
            () -> new VulkanTextureLoader( this, device, bufferLoader )
        );
    }
    
    void deregisterTextureLoader( VulkanTextureLoader textureLoader ) {
        VulkanHelper.deregister( textureLoaders, textureLoader, VulkanTextureLoader::disposeUnsafe );
    }
    
    VulkanDescriptor createDescriptor( VulkanPhysicalDevice device ) {
        return VulkanHelper.register( descriptors,
            () -> new VulkanDescriptor( this, device )
        );
    }
    
    public void deregisterDescriptor( VulkanDescriptor descriptor ) {
        VulkanHelper.deregister( descriptors, descriptor, VulkanDescriptor::disposeUnsafe );
    }
    
    
    VulkanBufferProvider createBufferProvider(
        VulkanPhysicalDevice device,
        VulkanTransferCommandPool vertexDataTransferCommandPool,
        VulkanTransferCommandPool quickDataTransferCommandPool,
        VulkanTransferCommandPool uniformDataTransferCommandPool,
        VulkanTransferCommandPool textureTransferCommandPool,
        long uniformBufferOffsetAlignment
    ) {
        return VulkanHelper.register( bufferProviders, () -> new VulkanBufferProvider(
            this,
            device,
            vertexDataTransferCommandPool,
            quickDataTransferCommandPool,
            uniformDataTransferCommandPool,
            textureTransferCommandPool, uniformBufferOffsetAlignment
        ) );
    }
    
    public void deregisterBufferProvider( VulkanBufferProvider bufferProvider ) {
        VulkanHelper.deregister( bufferProviders, bufferProvider, VulkanBufferProvider::disposeUnsafe );
    }
    
    VulkanSwapChain createSwapChain( VulkanPhysicalDevice device ) {
        return VulkanHelper.register( swapChains,
            () -> new VulkanSwapChain( this, device )
        );
    }
    
    void deregisterSwapChain( VulkanSwapChain swapChain ) {
        VulkanHelper.deregister( swapChains, swapChain, VulkanSwapChain::disposeUnsafe );
    }
    
    public void dispose() {
        allocator.deregisterPhysicalDeviceAllocator( this );
    }
    
    VulkanGraphicPipelines createGraphicPipelines(
        VulkanPhysicalDevice device,
        int topologyType
    ) {
        return VulkanHelper.register( graphicPipelines,
            () -> new VulkanGraphicPipelines( this, device, topologyType )
        );
    }
    
    public void deregisterGraphicPipelines( VulkanGraphicPipelines pipelines ) {
        VulkanHelper.deregister( graphicPipelines, pipelines, VulkanGraphicPipelines::disposeUnsafe );
    }
    
    VulkanGraphicCommandPool createGraphicCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        return VulkanHelper.register( graphicCommandPools,
            () -> new VulkanGraphicCommandPool( this, device, usedQueueFamily )
        );
    }
    
    public void deregisterGraphicCommandPool( VulkanGraphicCommandPool pool ) {
        VulkanHelper.deregister( graphicCommandPools, pool, VulkanGraphicCommandPool::disposeUnsafe );
    }
    
    VulkanTransferCommandPool createTransferCommandPool(
        VulkanPhysicalDevice device,
        VulkanQueueFamily usedQueueFamily
    ) {
        return VulkanHelper.register( transferCommandPools,
            () -> new VulkanTransferCommandPool( this, device, usedQueueFamily )
        );
    }
    
    public void deregisterTransferCommandPool( VulkanTransferCommandPool pool ) {
        VulkanHelper.deregister( transferCommandPools, pool, VulkanTransferCommandPool::disposeUnsafe );
    }
    
    VulkanFrameBuffer createFrameBuffer(
        VulkanPhysicalDevice device,
        VulkanImageView imageView,
        VulkanRenderPass renderPass,
        VulkanSwapChain swapChain,
        VulkanDepthResources depthResources
    ) {
        return VulkanHelper.register( frameBuffers, () -> new VulkanFrameBuffer(
            this,
            device,
            imageView,
            renderPass,
            swapChain,
            depthResources
        ) );
    }
    
    void deregisterFrameBuffer( VulkanFrameBuffer buffer ) {
        VulkanHelper.deregister( frameBuffers, buffer, VulkanFrameBuffer::disposeUnsafe );
    }
}
