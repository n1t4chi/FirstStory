/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import com.firststory.firsttools.FirstToolsConstants;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
class VulkanRenderStageWorker implements Callable< VulkanRenderBatchData > {
    private static final Logger logger = FirstToolsConstants.getLogger( VulkanRenderStageWorker.class );
    
    private final VulkanPhysicalDevice device;
    private final ExecutorService executorService;
    private final VulkanStage stage;
    private final List< VulkanDataBuffer > dataBuffers;
    private final Deque< VulkanDataBuffer > availableDataBuffers;
    private final boolean shouldDrawTextures;
    private final boolean shouldDrawBorder;
    private final RenderParameters renderParameters;
    private static final int CHUNK_SIZE = 2000;
    
    public VulkanRenderStageWorker(
        VulkanPhysicalDevice device,
        List< VulkanDataBuffer > dataBuffers,
        Deque< VulkanDataBuffer > availableDataBuffers,
        boolean shouldDrawTextures,
        boolean shouldDrawBorder,
        ExecutorService executorService,
        VulkanStage stage,
        RenderParameters renderParameters
    ) {
        this.device = device;
        this.executorService = executorService;
        this.stage = stage;
        this.dataBuffers = dataBuffers;
        this.availableDataBuffers = availableDataBuffers;
        this.shouldDrawTextures = shouldDrawTextures;
        this.shouldDrawBorder = shouldDrawBorder;
        this.renderParameters = renderParameters;
    }
    
    @Override
    public VulkanRenderBatchData call() {
        return renderStage();
    }
    
    private VulkanRenderBatchData renderStage() {
        var uniformBuffers = new ArrayList< VulkanDataBuffer>();
        var uniformBuffer = device.getShaderProgram().bindUniformData( stage.getCamera().getMatrixRepresentation() );
        uniformBuffers.add( uniformBuffer );
        var uniformBufferInfo = device.getShaderProgram().createDescriptorBufferInfo( uniformBuffer );
        
        var renderDatas = stage.getRenderDatas();
        List< List< RenderData > > batches = new ArrayList<>(  );
        for( var i=0; i < renderDatas.size() ; i += CHUNK_SIZE ) {
            batches.add( renderDatas.subList( i, Math.min( i + CHUNK_SIZE, renderDatas.size() ) ) );
        }
        if( batches.isEmpty() ) {
            batches.add( Collections.emptyList() );
        }
        var first = true;
        var futures = new ArrayList< Future< VulkanRenderBatchPartialData> >();
    
        var textures = stage.getTextures();
        var descriptorPool = device.getDescriptor().createDescriptorPool( textures.size() );
        var setsByTexture = new HashMap< Texture, VulkanDescriptorSet >();
        
        for ( var renderDataList : batches ) {
            var trianglePipeline = first
                ? stage.isInitialStage()
                    ? renderParameters.getTrianglePipelines().getFirstRenderPipeline()
                    : renderParameters.getTrianglePipelines().getFirstStageRenderPipeline()
                : renderParameters.getTrianglePipelines().getContinuingRenderPipeline()
            ;
            var linePipeline = first
                ? stage.isInitialStage()
                    ? renderParameters.getLinePipelines().getFirstRenderPipeline()
                    : renderParameters.getLinePipelines().getFirstStageRenderPipeline()
                : renderParameters.getLinePipelines().getContinuingRenderPipeline()
            ;
            first = false;
            
            futures.add( executorService.submit( () -> renderBatch(
                trianglePipeline,
                linePipeline,
                uniformBufferInfo,
                descriptorPool,
                setsByTexture,
                renderDataList
            ) ) );
        }
        var partialDatas = futures.stream()
            .map( vulkanRenderBatchDataFuture -> {
                try {
                    return vulkanRenderBatchDataFuture.get();
                } catch ( Exception ex ) {
                    logger.log( Level.SEVERE, "exception while rendering batch", ex );
                    return null;
                }
            } )
            .filter( Objects::nonNull )
            .collect( Collectors.toList() )
        ;
        return new VulkanRenderBatchData( device.getDescriptor().createDescriptorPool( textures.size() ), uniformBuffers, partialDatas, setsByTexture );
    }
    
    private VulkanRenderBatchPartialData renderBatch(
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline,
        VkDescriptorBufferInfo uniformBufferInfo,
        VulkanDescriptorPool descriptorPool,
        HashMap< Texture, VulkanDescriptorSet > setsByTexture,
        List< RenderData > renderDataList
    ) {
        var renderPass = trianglePipeline.getRenderPass();
        var commandPool = device.getAllocator().createGraphicCommandPool();
        var primaryBuffer = commandPool.provideNextPrimaryBuffer();
        primaryBuffer.fillQueueSetup();
        primaryBuffer.beginRenderPass(
            renderParameters.getSwapChain(),
            renderPass,
            renderParameters.getFrameBuffer(),
            renderParameters.getBackgroundColour()
        );
        var secondaryBuffers = List.of( renderDataList(
            trianglePipeline,
            linePipeline,
            renderPass,
            commandPool,
            descriptorPool,
            setsByTexture,
            uniformBufferInfo,
            renderDataList
        ) );
        primaryBuffer.executeSecondaryBuffers( secondaryBuffers );
        primaryBuffer.fillQueueTearDown();
        return new VulkanRenderBatchPartialData(
            commandPool,
            primaryBuffer,
            secondaryBuffers,
            trianglePipeline,
            linePipeline
        );
    }
    
    private VulkanGraphicSecondaryCommandBuffer renderDataList(
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline,
        VulkanRenderPass renderPass,
        VulkanGraphicCommandPool commandPool,
        VulkanDescriptorPool descriptorPool,
        HashMap< Texture, VulkanDescriptorSet > setsByTexture,
        VkDescriptorBufferInfo uniformBufferInfo,
        List< RenderData > renderDataList
    ) {
        var secondaryBuffer = commandPool.provideNextSecondaryBuffer();
        secondaryBuffer.update(
            renderPass,
            0,
            renderParameters.getFrameBuffer()
        );
        secondaryBuffer.fillQueueSetup();
    
        renderDataList.forEach( renderData -> renderData(
            trianglePipeline,
            linePipeline,
            secondaryBuffer,
            descriptorPool,
            setsByTexture,
            uniformBufferInfo,
            renderData
        ) );
        
        secondaryBuffer.fillQueueTearDown();
        return secondaryBuffer;
    }
    
    private void renderData(
        VulkanPipeline trianglePipeline,
        VulkanPipeline linePipeline,
        VulkanGraphicSecondaryCommandBuffer buffer,
        VulkanDescriptorPool descriptorPool,
        HashMap< Texture, VulkanDescriptorSet > setsByTexture,
        VkDescriptorBufferInfo uniformBufferInfo,
        RenderData renderData
    ) {
        VulkanPipeline pipeline;
        switch ( renderData.getType() ) {
            case BORDER:
                if ( !shouldDrawBorder ) {
                    return;
                }
            case LINES:
            case LINE_LOOP:
                pipeline = linePipeline;
                break;
            case TRIANGLES:
            default:
                pipeline = trianglePipeline;
                break;
        }
        buffer.bindPipeline( pipeline );
        buffer.setLineWidth( renderData.getLineWidth() );
        
        var dataBuffer = loadObjectData(
            availableDataBuffers,
            renderData
        );
        
        var loader = device.getVertexAttributeLoader();
        
        var uvBuffer = loader.extractUvMapBuffer( renderData.getUvMap(),
            renderData.getUvDirection(),
            renderData.getUvFrame()
        );
        var vertexBuffer = loader.extractVerticesBuffer(
            renderData.getVertices(),
            renderData.getVertexFrame()
        );
        var colouringBuffer = loader.extractColouringBuffer( renderData.getColouring() );
        
        var bufferSize = renderData.getVertices().getVertexLength( renderData.getVertexFrame() );
        VulkanDescriptorSet descriptorSet;
        synchronized ( setsByTexture ) {
            descriptorSet = setsByTexture.computeIfAbsent( getUsedTexture( renderData.getTexture() ), usedTexture -> {
                var set = descriptorPool.getNextDescriptorSet();
                var textureBuffer = device.getTextureLoader().bind( usedTexture );
                var textureSampler = device.getTextureSampler();
                set.updateDescriptorSet(
                    textureSampler,
                    textureBuffer.getContext(),
                    uniformBufferInfo
                );
                set.setSampler( textureSampler );
                return set;
            } );
        
            buffer.bindDescriptorSets(
                pipeline,
                descriptorSet
            );
            
            buffer.draw(
                vertexBuffer,
                uvBuffer,
                colouringBuffer,
                dataBuffer,
                bufferSize
            );
        }
    }
    
    private Texture getUsedTexture( Texture texture ) {
        return shouldDrawTextures ? texture : FirstOracleConstants.EMPTY_TEXTURE;
    }
    
    private VulkanDataBuffer loadObjectData(
        Deque< VulkanDataBuffer > availableBuffers,
        RenderData renderData
    ) {
        var shader = device.getShaderProgram();
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
                dataBuffer = device
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
