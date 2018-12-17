/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;

import java.util.List;
import java.util.concurrent.Future;

public class RenderParameters {
    
    private final VulkanGraphicPipelines trianglePipelines;
    private final VulkanGraphicPipelines linePipelines;
    private final VulkanSwapChain swapChain;
    private final VulkanImageIndex imageIndex;
    private final VulkanFrameBuffer frameBuffer;
    private final List< VulkanTransferCommandPool > transferCommandPools;
    private final VulkanStages vulkanStages = new VulkanStages();
    private final VulkanDeviceAllocator allocator;
    
    private Colour backgroundColour;
    private Future< VulkanRenderBatchData > backgroundFuture;
    private Future< VulkanRenderBatchData > scene2DFuture;
    private Future< VulkanRenderBatchData > scene3DFuture;
    private Future< VulkanRenderBatchData > overlayFuture;
    
    public void setBackgroundColour( Colour backgroundColour ) {
        this.backgroundColour = backgroundColour;
    }
    
    public void setBackgroundFuture( Future< VulkanRenderBatchData > backgroundFuture ) {
        this.backgroundFuture = backgroundFuture;
    }
    
    public void setScene2DFuture( Future< VulkanRenderBatchData > scene2DFuture ) {
        this.scene2DFuture = scene2DFuture;
    }
    
    public void setScene3DFuture( Future< VulkanRenderBatchData > scene3DFuture ) {
        this.scene3DFuture = scene3DFuture;
    }
    
    public void setOverlayFuture( Future< VulkanRenderBatchData > overlayFuture ) {
        this.overlayFuture = overlayFuture;
    }
    
    public VulkanImageIndex getImageIndex() {
        return imageIndex;
    }
    
    public List< VulkanTransferCommandPool > getTransferCommandPools() {
        return transferCommandPools;
    }
    
    public VulkanStages getVulkanStages() {
        return vulkanStages;
    }
    
    public VulkanDeviceAllocator getAllocator() {
        return allocator;
    }
    
    public Colour getBackgroundColour() {
        return backgroundColour;
    }
    
    public Future< VulkanRenderBatchData > getBackgroundFuture() {
        return backgroundFuture;
    }
    
    public Future< VulkanRenderBatchData > getScene2DFuture() {
        return scene2DFuture;
    }
    
    public Future< VulkanRenderBatchData > getScene3DFuture() {
        return scene3DFuture;
    }
    
    public Future< VulkanRenderBatchData > getOverlayFuture() {
        return overlayFuture;
    }
    
    public RenderParameters(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines linePipelines,
        VulkanSwapChain swapChain,
        VulkanImageIndex imageIndex,
        VulkanFrameBuffer frameBuffer,
        List< VulkanTransferCommandPool > transferCommandPools,
        VulkanDeviceAllocator allocator
    ) {
        this.trianglePipelines = trianglePipelines;
        this.linePipelines = linePipelines;
        this.swapChain = swapChain;
        this.imageIndex = imageIndex;
        this.frameBuffer = frameBuffer;
        this.transferCommandPools = transferCommandPools;
        this.allocator = allocator;
    }
    
    VulkanGraphicPipelines getTrianglePipelines() {
        return trianglePipelines;
    }
    
    VulkanGraphicPipelines getLinePipelines() {
        return linePipelines;
    }
    
    VulkanSwapChain getSwapChain() {
        return swapChain;
    }
    
    VulkanFrameBuffer getFrameBuffer() {
        return frameBuffer;
    }
}
