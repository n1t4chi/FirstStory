/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanFrameBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanSwapChain;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author n1t4chi
 */
class VulkanRenderStageWorker implements Callable< VulkanRenderBatchData > {
    
    private final VulkanPhysicalDevice device;
    private final VulkanStage stage;
    private final VulkanTextureSampler textureSampler;
    private final List< VulkanDataBuffer > dataBuffers;
    private final Deque< VulkanDataBuffer > availableDataBuffers;
    private final boolean shouldDrawTextures;
    private final boolean shouldDrawBorder;
    private final VulkanPipeline trianglePipeline;
    private final VulkanPipeline linePipeline;
    private final VulkanSwapChain swapChain;
    private final VulkanFrameBuffer frameBuffer;
    private final Colour backgroundColour;
    private final VulkanGraphicPipelines linePipelines;
    private final VulkanGraphicPipelines trianglePipelines;

    VulkanRenderStageWorker(
        VulkanPhysicalDevice device,
        List< VulkanDataBuffer > dataBuffers,
        Deque< VulkanDataBuffer > availableDataBuffers,
        boolean shouldDrawTextures,
        boolean shouldDrawBorder,
        VulkanStage stage,
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline,
        VulkanSwapChain swapChain,
        VulkanFrameBuffer frameBuffer,
        Colour backgroundColour,
        VulkanGraphicPipelines linePipelines,
        VulkanGraphicPipelines trianglePipelines
    ) {
        this.device = device;
        this.stage = stage;
        textureSampler = device.getTextureSampler();
        this.dataBuffers = dataBuffers;
        this.availableDataBuffers = availableDataBuffers;
        this.shouldDrawTextures = shouldDrawTextures;
        this.shouldDrawBorder = shouldDrawBorder;
        this.trianglePipeline = trianglePipeline;
        this.linePipeline = linePipeline;
        this.swapChain = swapChain;
        this.frameBuffer = frameBuffer;
        this.backgroundColour = backgroundColour;
        this.linePipelines = linePipelines;
        this.trianglePipelines = trianglePipelines;
    }
    
    @Override
    public VulkanRenderBatchData call() {
        return renderStage();
    }
    
    private VulkanRenderBatchData renderStage() {
        var camera = stage.getCamera();
        var renderDataByTexture = stage.getRenderDataByTexture();
        var textures = stage.getTextures();
        var renderPass = trianglePipeline.getRenderPass();
        
        var commandPool = device.getAllocator().createGraphicCommandPool();
        var primaryBuffer = commandPool.provideNextPrimaryBuffer();
        primaryBuffer.fillQueueSetup();
        primaryBuffer.beginRenderPass(
            swapChain,
            renderPass,
            frameBuffer,
            backgroundColour
        );
        
        var secondaryBuffers = new ArrayList< VulkanGraphicSecondaryCommandBuffer >();
        var descriptorsPools = new ArrayList< VulkanDescriptorPool >();
        var uniformBuffers = new ArrayList< VulkanDataBuffer>();
        if ( !renderDataByTexture.isEmpty() ) {
            var shader = device.getShaderProgram3D();
            var descriptorPool = device.getDescriptor().createDescriptorPool( renderDataByTexture.size() );
            descriptorsPools.add( descriptorPool );
    
            shader.bindCamera( camera.getMatrixRepresentation() );
            var uniformBuffer = shader.bindUniformData();
            var uniformBufferInfo = shader.createDescriptorBufferInfo( uniformBuffer );
            uniformBuffers.add( uniformBuffer );
    
            renderDataByTexture.forEach( ( texture, renderDataList ) -> {
                var secondaryBuffer = commandPool.provideNextSecondaryBuffer();
                secondaryBuffers.add( secondaryBuffer );
                secondaryBuffer.update(
                    renderPass,
                    0,
                    frameBuffer
                );
                renderData(
                    trianglePipeline,
                    linePipeline,
                    secondaryBuffer,
                    descriptorPool,
                    uniformBufferInfo,
                    texture,
                    renderDataList
                );
            } );
        }
        
        primaryBuffer.executeSecondaryBuffers( secondaryBuffers );
        primaryBuffer.fillQueueTearDown();
        return new VulkanRenderBatchData(
            commandPool,
            primaryBuffer,
            secondaryBuffers,
            descriptorsPools,
            uniformBuffers,
            trianglePipeline,
            linePipeline
        );
    }
    
    private void renderData(
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline,
        VulkanGraphicSecondaryCommandBuffer buffer,
        VulkanDescriptorPool descriptorPool,
        VkDescriptorBufferInfo uniformBufferInfo,
        Texture texture,
        List< RenderData > renderDataList
    ) {
        var holder = new VulkanLastPipelineHolder();
        var textureBuffer = device.getTextureLoader().bind( shouldDrawTextures
            ? texture
            : FirstOracleConstants.EMPTY_TEXTURE
        );
        
        var descriptorSet = descriptorPool.getNextDescriptorSet();
        descriptorSet.updateDescriptorSet(
            textureSampler,
            textureBuffer.getContext(),
            uniformBufferInfo
        );
        buffer.fillQueueSetup();
        buffer.bindDescriptorSets(
            linePipelines,
            descriptorSet
        );
        buffer.bindDescriptorSets(
            trianglePipelines,
            descriptorSet
        );
        
        renderDataList.forEach( renderData -> {
            VulkanPipeline pipeline;
            switch ( renderData.getType() ) {
                case BORDER:
                    if ( !shouldDrawBorder ) {
                        return;
                    }
                case LINES:
                case LINE_LOOP:
                    buffer.setLineWidth( renderData.getLineWidth() );
                    pipeline = linePipeline;
                    break;
                case TRIANGLES:
                default:
                    pipeline = trianglePipeline;
                    break;
            }
            if ( holder.getLastPipeline() != pipeline ) {
                buffer.bindPipeline( pipeline );
                holder.setLastPipeline( pipeline );
            }
            
            var dataBuffer = loadObjectData(
                availableDataBuffers,
                renderData
            );
            
            var loader = device.getVertexAttributeLoader();
            
            var
                uvBuffer =
                loader.extractUvMapBuffer( renderData.getUvMap(),
                    renderData.getUvDirection(),
                    renderData.getUvFrame()
                );
            var vertexBuffer = loader.extractVerticesBuffer(
                renderData.getVertices(),
                renderData.getVertexFrame()
            );
            var colouringBuffer = loader.extractColouringBuffer( renderData.getColouring() );
            
            var
                bufferSize =
                renderData
                    .getVertices()
                    .getVertexLength( renderData.getVertexFrame() );
            buffer.draw(
                vertexBuffer,
                uvBuffer,
                colouringBuffer,
                dataBuffer,
                bufferSize
            );
        } );
        buffer.fillQueueTearDown();
    }
    
    private VulkanDataBuffer loadObjectData(
        Deque< VulkanDataBuffer > availableBuffers,
        RenderData renderData
    ) {
        var shader = device.getShaderProgram3D();
        shader.bindPosition( renderData.getPosition() );
        shader.bindScale( renderData.getScale() );
        shader.bindRotation( renderData.getRotation() );
        shader.bindOverlayColour( renderData.getOverlayColour() );
        shader.bindMaxAlphaChannel( renderData.getMaxAlphaChannel() );
        var data = shader.getInputData();
        
        VulkanDataBuffer dataBuffer;
        synchronized ( availableBuffers ) {
            if ( !availableBuffers.isEmpty() ) {
                dataBuffer = availableBuffers.poll();
                dataBuffer.load( data );
            } else {
                dataBuffer =
                    device
                        .getBufferProvider()
                        .createQuickVertexBuffer( data );
                synchronized ( dataBuffers ) {
                    dataBuffers.add( dataBuffer );
                }
            }
        }
        return dataBuffer;
    }
}
