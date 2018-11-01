/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanFrameworkAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.commands.VulkanCommandBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class VulkanRenderingContext implements RenderingContext {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanRenderingContext.class );
    private static final String OVERLAY = "overlay";
    private static final String SCENE_3D = "scene3D";
    private static final String SCENE_2D = "scene2D";
    private static final String BACKGROUND = "background";
    
    private final VulkanFrameworkAllocator allocator;
    private final VulkanPhysicalDevice device;
    private final List< VulkanDataBuffer > dataBuffers = new ArrayList<>( 5000 );
    private final boolean shouldDrawBorder;
    private final boolean shouldDrawTextures;
    
    private final Stage background = new Stage();
    private final Stage overlay = new Stage();
    private final Stage scene2D = new Stage();
    private final Stage scene3D = new Stage();
    private final ExecutorService executorService;
    private final VulkanTextureSampler textureSampler;
    
    private final Deque< VulkanDataBuffer > availableDataBuffers = new LinkedList<>();
    
    private final RenderStageWorker backgroundWorker = new RenderStageWorker( background );
    private final RenderStageWorker scene2DWorker = new RenderStageWorker( scene2D );
    private final RenderStageWorker scene3DWorker = new RenderStageWorker( scene3D );
    private final RenderStageWorker overlayWorker = new RenderStageWorker( overlay );
    
    private VulkanGraphicPipelines trianglePipelines;
    private VulkanGraphicPipelines linePipelines;
    private VulkanFrameBuffer frameBuffer;
    private Colour backgroundColour;
    
    public VulkanRenderingContext(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this( allocator, device,
            PropertiesUtil.isPropertyTrue( PropertiesUtil.DRAW_BORDER_PROPERTY ),
            !PropertiesUtil.isPropertyTrue( PropertiesUtil.DISABLE_TEXTURES_PROPERTY )
        );
    }
    
    private VulkanRenderingContext(
        VulkanFrameworkAllocator allocator,
        VulkanPhysicalDevice device,
        boolean shouldDrawBorder,
        boolean shouldDrawTextures
    ) {
        this.allocator = allocator;
        this.device = device;
        this.shouldDrawBorder = shouldDrawBorder;
        this.shouldDrawTextures = shouldDrawTextures;
        executorService = Executors.newFixedThreadPool( 4 );
        textureSampler = device.getTextureSampler();
    }
    
    public void dispose() {
        allocator.deregisterRenderingContext( this );
    }
    
    public void disposeUnsafe() {
        executorService.shutdownNow();
    }
    
    public void setUpSingleRender() {
        getShader().resetUniformData();
        background.clear();
        overlay.clear();
        scene2D.clear();
        scene3D.clear();
    }
    
    @Override
    public void renderOverlay( Camera2D camera, List< RenderData > renderDatas ) {
        renderDatas.forEach( overlay::addRenderData );
        overlay.setCamera( camera );
    }
    
    @Override
    public void renderBackground( Camera2D camera, Colour backgroundColour, List< RenderData > renderDatas ) {
        this.backgroundColour = backgroundColour;
        renderDatas.forEach( background::addRenderData );
        background.setCamera( camera );
    }
    
    @Override
    public void renderScene3D( Camera3D camera, List< RenderData > renderDatas ) {
        renderDatas.forEach( scene3D::addRenderData );
        scene3D.setCamera( camera );
    }
    
    @Override
    public void renderScene2D( Camera2D camera, List< RenderData > renderDatas ) {
        renderDatas.forEach( scene2D::addRenderData );
        scene2D.setCamera( camera );
    }
    
    public void tearDownSingleRender(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines linePipelines,
        VulkanImageIndex imageIndex,
        VulkanSwapChain swapChain,
        VulkanTransferCommandPool... transferCommandPools
    ) {
        this.trianglePipelines = trianglePipelines;
        this.linePipelines = linePipelines;
        var deviceAllocator = device.getAllocator();
        
        availableDataBuffers.clear();
        
        availableDataBuffers.addAll( dataBuffers );
        
        frameBuffer = device.getCurrentFrameBuffer();
        
        var backgroundFuture = executorService.submit( backgroundWorker.prepare(
            trianglePipelines.getBackgroundPipeline(),
            linePipelines.getBackgroundPipeline(),
            swapChain
        ) );
        var scene2DFuture = executorService.submit( scene2DWorker.prepare(
            trianglePipelines.getScene2DPipeline(),
            linePipelines.getScene2DPipeline(),
            swapChain
        ) );
        var scene3DFuture = executorService.submit( scene3DWorker.prepare(
            trianglePipelines.getScene3DPipeline(),
            linePipelines.getScene3DPipeline(),
            swapChain
        ) );
        var overlayFuture = executorService.submit( overlayWorker.prepare(
            trianglePipelines.getOverlayPipeline(),
            linePipelines.getOverlayPipeline(),
            swapChain
        ) );
        var backgroundBatchData = waitThenRenderStageInfo( backgroundFuture, BACKGROUND );
        var scene2dBatchData = waitThenRenderStageInfo( scene2DFuture, SCENE_2D );
        var scene3dBatchData = waitThenRenderStageInfo( scene3DFuture, SCENE_3D );
        var overlayBatchData = waitThenRenderStageInfo( overlayFuture, OVERLAY );
    
        var imageAvailableSemaphore = imageIndex.getImageAvailableSemaphore();
        var renderFinishedSemaphore = imageIndex.getRenderFinishedSemaphore();
        
        List< VulkanSemaphore > initialWaitSemaphores = new ArrayList<>(  );
        for ( var transferCommandPool : transferCommandPools ) {
            initialWaitSemaphores.add( transferCommandPool.executeTransfers() );
        }
        initialWaitSemaphores.add( imageAvailableSemaphore );
        
        List< BatchData > batchDatas = new ArrayList<>();
        batchDatas.add( backgroundBatchData );
        batchDatas.add( scene2dBatchData );
        batchDatas.add( scene3dBatchData );
        batchDatas.add( overlayBatchData );
        batchDatas.removeIf( Objects::isNull );
        
        var processedInfo = processBatchDatas( deviceAllocator, batchDatas, initialWaitSemaphores, renderFinishedSemaphore );
        
        var fence = deviceAllocator.createFence();
        
        device.getGraphicQueueFamily().submit( fence, processedInfo.getSubmitInfos() );
        
        swapChain.presentQueue( imageIndex );
        
        fence.executeWhenFinishedThenDispose( () -> {
            processedInfo.getSemaphores().forEach( VulkanSemaphore::dispose );
            batchDatas.forEach( BatchData::dispose );
        } );
    }
    
    private class BatchDatasProcessedInfo {
        private final List< VkSubmitInfo > submitInfos;
        private final List< VulkanSemaphore > semaphores;
    
        private BatchDatasProcessedInfo(
            List< VkSubmitInfo > submitInfos,
            List< VulkanSemaphore > semaphores
        ) {
            this.submitInfos = submitInfos;
            this.semaphores = semaphores;
        }
    
        private List< VkSubmitInfo > getSubmitInfos() {
            return submitInfos;
        }
    
        private List< VulkanSemaphore > getSemaphores() {
            return semaphores;
        }
    }
    
    private BatchDatasProcessedInfo processBatchDatas(
        VulkanDeviceAllocator deviceAllocator,
        List< BatchData > batchDatas,
        List< VulkanSemaphore > initialWaitSemaphores,
        VulkanSemaphore renderFinishedSemaphore
    ) {
        List< VkSubmitInfo > submitInfos = new ArrayList<>(  );
        List< VulkanSemaphore > semaphores = new ArrayList<>( initialWaitSemaphores );
        semaphores.add( renderFinishedSemaphore );
        
        if( batchDatas.isEmpty() ) {
            submitInfos.add( VulkanSubmitInfo.createSubmitInfo(
                initialWaitSemaphores,
                renderFinishedSemaphore
            ) );
        } else {
            VulkanSemaphore waitSemapore = null;
            for ( int i = 0, batchDatasSize = batchDatas.size(); i < batchDatasSize; i++ ) {
                var batchData = batchDatas.get( i );
                VulkanSemaphore signalSemaphore;
                if( i == batchDatasSize - 1 ) {
                    signalSemaphore = renderFinishedSemaphore;
                } else {
                    signalSemaphore = deviceAllocator.createSemaphore();
                    semaphores.add( signalSemaphore );
                }
                VkSubmitInfo submitInfo;
                if ( i == 0 ) {
                    submitInfo = VulkanSubmitInfo.createSubmitInfo(
                        batchData.getPrimaryBuffer(),
                        initialWaitSemaphores,
                        signalSemaphore
                    );
                } else {
                    submitInfo = VulkanSubmitInfo.createSubmitInfo(
                        batchData.getPrimaryBuffer(),
                        waitSemapore,
                        signalSemaphore
                    );
                }
                waitSemapore = signalSemaphore;
                submitInfos.add( submitInfo );
            }
        }
        return new BatchDatasProcessedInfo( submitInfos, semaphores );
    }
    
    private BatchData waitThenRenderStageInfo(
        Future< BatchData > stageFuture,
        String stageName
    ) {
        try {
            return stageFuture.get();
        } catch ( InterruptedException | ExecutionException ex ) {
            logger.log( Level.WARNING, ex, () -> "Exception while rendering  " + stageName + "." );
        }
        return null;
    }
    
    private BatchData renderStage(
        Stage stage,
        VulkanGraphicPipelines.Pipeline trianglePipeline,
        VulkanGraphicPipelines.Pipeline linePipeline,
        VulkanSwapChain swapChain
    ) {
        var camera = stage.camera;
        var renderDataByTexture = stage.renderDataByTexture;
        var renderPass = trianglePipeline.getRenderPass();
        
        var commandPool = device.getAllocator().createGraphicCommandPool();
        var primaryBuffer = commandPool.provideNextPrimaryBuffer();
        primaryBuffer.fillQueueSetup();
        primaryBuffer.beginRenderPass( swapChain, renderPass, frameBuffer, backgroundColour );
        
        var secondaryBuffers = new ArrayList< VulkanGraphicSecondaryCommandBuffer >();
        var descriptorsPools = new ArrayList< VulkanDescriptorPool >();
        if ( !renderDataByTexture.isEmpty() ) {
            var shader = getShader();
            var descriptorPool = device.getDescriptor().createDescriptorPool( renderDataByTexture.size() );
            descriptorsPools.add( descriptorPool );
            
            shader.bindCamera( camera.getMatrixRepresentation() );
            var uniformBufferInfo = shader.bindUniformData();
            
            renderDataByTexture.forEach( ( texture, renderDataList ) -> {
                var secondaryBuffer = commandPool.provideNextSecondaryBuffer();
                synchronized ( secondaryBuffers ) {
                    secondaryBuffers.add( secondaryBuffer );
                }
                secondaryBuffer.update( renderPass, 0, frameBuffer );
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
        return new BatchData( commandPool, primaryBuffer, secondaryBuffers, descriptorsPools );
    }
    
    private void renderData(
        VulkanGraphicPipelines.Pipeline trianglePipeline,
        VulkanGraphicPipelines.Pipeline linePipeline,
        VulkanGraphicSecondaryCommandBuffer buffer,
        VulkanDescriptorPool descriptorPool,
        VkDescriptorBufferInfo uniformBufferInfo,
        Texture texture,
        List< RenderData > renderDataList
    ) {
        var holder = new LastPipelineHolder();
        var textureBuffer = device.getTextureLoader().bind( shouldDrawTextures ? texture : FirstOracleConstants.EMPTY_TEXTURE );
    
    
        var descriptorSet = descriptorPool.getNextDescriptorSet();
        descriptorSet.updateDescriptorSet( textureSampler, textureBuffer.getContext(), uniformBufferInfo );
        buffer.fillQueueSetup();
        buffer.bindDescriptorSets( linePipelines, descriptorSet );
        buffer.bindDescriptorSets( trianglePipelines, descriptorSet );
        
        renderDataList.forEach( renderData -> {
            VulkanGraphicPipelines.Pipeline pipeline;
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
            if ( holder.lastPipeline != pipeline ) {
                buffer.bindPipeline( pipeline );
                holder.lastPipeline = pipeline;
            }
    
            var dataBuffer = loadObjectData( availableDataBuffers, renderData );
    
            var loader = device.getVertexAttributeLoader();
    
            var uvBuffer = loader.extractUvMapBuffer( renderData.getUvMap(), renderData.getUvDirection(), renderData.getUvFrame() );
            var vertexBuffer = loader.extractVerticesBuffer( renderData.getVertices(), renderData.getVertexFrame() );
            var colouringBuffer = loader.extractColouringBuffer( renderData.getColouring() );
    
            var bufferSize = renderData.getVertices().getVertexLength( renderData.getVertexFrame() );
            buffer.draw( vertexBuffer, uvBuffer, colouringBuffer, dataBuffer, bufferSize );
        } );
        buffer.fillQueueTearDown();
    }
    
    private VulkanDataBuffer loadObjectData( Deque< VulkanDataBuffer > availableBuffers, RenderData renderData ) {
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
                dataBuffer = device.getBufferProvider().createQuickVertexBuffer( data );
                synchronized ( dataBuffers ) {
                    dataBuffers.add( dataBuffer );
                }
            }
        }
        return dataBuffer;
    }
    
    private VulkanShaderProgram getShader() {
        return device.getShaderProgram3D();
    }
    
    private class RenderStageWorker implements Callable< BatchData > {
        
        private final Stage stage;
        private VulkanGraphicPipelines.Pipeline trianglePipeline;
        private VulkanGraphicPipelines.Pipeline linePipeline;
        private VulkanSwapChain swapChain;
    
        private RenderStageWorker( Stage stage ) {
            this.stage = stage;
        }
        
        RenderStageWorker prepare(
            VulkanGraphicPipelines.Pipeline trianglePipeline,
            VulkanGraphicPipelines.Pipeline linePipeline,
            VulkanSwapChain swapChain
        ) {
            this.trianglePipeline = trianglePipeline;
            this.linePipeline = linePipeline;
            this.swapChain = swapChain;
            return this;
        }
        
        @Override
        public BatchData call() {
            return renderStage(
                stage,
                trianglePipeline,
                linePipeline,
                swapChain
            );
        }
    }
    
    private static class LastPipelineHolder {
        private VulkanGraphicPipelines.Pipeline lastPipeline = null;
    }
    
    private class BatchData {
        private final VulkanGraphicCommandPool commandPool;
        private final VulkanGraphicPrimaryCommandBuffer primaryBuffer;
        private final List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers;
        private final ArrayList< VulkanDescriptorPool > descriptorsPools;
    
        private BatchData(
            VulkanGraphicCommandPool commandPool,
            VulkanGraphicPrimaryCommandBuffer primaryBuffer,
            List< VulkanGraphicSecondaryCommandBuffer > secondaryBuffers,
            ArrayList< VulkanDescriptorPool > descriptorsPools
        ) {
            this.commandPool = commandPool;
            this.primaryBuffer = primaryBuffer;
            this.secondaryBuffers = secondaryBuffers;
            this.descriptorsPools = descriptorsPools;
        }
    
        void dispose() {
            primaryBuffer.dispose();
            
            secondaryBuffers.forEach( VulkanCommandBuffer::dispose );
            secondaryBuffers.clear();
            descriptorsPools.forEach( VulkanDescriptorPool::dispose );
            descriptorsPools.clear();
            
            commandPool.dispose();
        }
    
        private VulkanGraphicPrimaryCommandBuffer getPrimaryBuffer() {
            return primaryBuffer;
        }
    }
    
    private class Stage {
        
        private final Map< Texture, List< RenderData > > renderDataByTexture = new HashMap<>();
        private Camera camera;
        
        private void setCamera( Camera camera ) {
            this.camera = camera;
        }
        
        private void addRenderData( RenderData data ) {
            renderDataByTexture.computeIfAbsent( data.getTexture(), texture -> new ArrayList<>() ).add( data );
        }
        
        private void clear() {
            renderDataByTexture.forEach( ( key, value ) -> value.clear() );
            renderDataByTexture.clear();
        }
    
    }
}
