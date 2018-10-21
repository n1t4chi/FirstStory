/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.Camera;
import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.RenderData;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.vulkan.*;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.transfer.VulkanTransferCommandPool;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    
    private final VulkanPhysicalDevice device;
    private final List< VulkanDataBuffer > dataBuffers = new ArrayList<>( 5000 );
    private final boolean shouldDrawBorder;
    private final boolean shouldDrawTextures;
    
    private final Stage background = new Stage();
    private final Stage overlay = new Stage();
    private final Stage scene2D = new Stage();
    private final Stage scene3D = new Stage();
    private Colour backgroundColour;
    private final List< VulkanTextureSampler > samplers = new ArrayList<>();
    private final List< VulkanDescriptorPool > descriptorsPools = new ArrayList<>();
    private final ExecutorService executorService;
    
    private final Deque< VulkanDataBuffer > availableDataBuffers = new LinkedList<>();
    
    private VulkanGraphicPipelines trianglePipelines;
    private VulkanGraphicPipelines linePipelines;
    private VulkanTextureSampler textureSampler;
    private VulkanFrameBuffer frameBuffer;
    
    private final RenderStageWorker backgroundWorker = new RenderStageWorker( background );
    private final RenderStageWorker scene2DWorker = new RenderStageWorker( scene2D );
    private final RenderStageWorker scene3DWorker = new RenderStageWorker( scene3D );
    private final RenderStageWorker overlayWorker = new RenderStageWorker( overlay );
    
    public VulkanRenderingContext( VulkanPhysicalDevice device ) {
        this( device,
            PropertiesUtil.isPropertyTrue( PropertiesUtil.DRAW_BORDER_PROPERTY ),
            !PropertiesUtil.isPropertyTrue( PropertiesUtil.DISABLE_TEXTURES_PROPERTY )
        );
    }
    
    public VulkanRenderingContext( VulkanPhysicalDevice device, boolean shouldDrawBorder, boolean shouldDrawTextures ) {
        this.device = device;
        this.shouldDrawBorder = shouldDrawBorder;
        this.shouldDrawTextures = shouldDrawTextures;
        executorService = Executors.newFixedThreadPool( 4 );
    }
    
    public void dispose() {
        executorService.shutdown();
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
        VulkanGraphicCommandPool backgroundGraphicCommandPool,
        VulkanGraphicCommandPool scene2DGraphicCommandPool,
        VulkanGraphicCommandPool scene3DGraphicCommandPool,
        VulkanGraphicCommandPool overlayGraphicCommandPool,
        VulkanImageIndex imageIndex,
        VulkanSwapChain swapChain,
        VulkanTransferCommandPool... transferCommandPools
    ) {
        this.trianglePipelines = trianglePipelines;
        this.linePipelines = linePipelines;
        
        samplers.forEach( VulkanTextureSampler::dispose );
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        availableDataBuffers.clear();
        samplers.clear();
        descriptorsPools.clear();
        
        textureSampler = new VulkanTextureSampler( device );
        
        availableDataBuffers.addAll( dataBuffers );
        
        frameBuffer = device.getCurrentFrameBuffer();
        
        samplers.add( textureSampler );
    
    
        var initialWaitSemaphores = new ArrayList< VulkanSemaphore >();
        var backgroundToScene2dSemaphore = new VulkanSemaphore( device );
        var scene2dToScene3dSemaphore = new VulkanSemaphore( device );
        var scene3dToOverlaySemaphore = new VulkanSemaphore( device );
        initialWaitSemaphores.add( imageIndex.getImageAvailableSemaphore() );
        
        
        var backgroundFuture = executorService.submit( backgroundWorker.prepare(
            backgroundGraphicCommandPool,
            swapChain,
            trianglePipelines.getBackgroundPipeline(),
            linePipelines.getBackgroundPipeline(),
            initialWaitSemaphores,
            backgroundToScene2dSemaphore
        ) );
//        var scene2DFuture = executorService.submit( scene2DWorker.prepare(
//            scene2DGraphicCommandPool,
//            swapChain,
//            trianglePipelines.getScene2DPipeline(),
//            linePipelines.getScene2DPipeline(),
//            Collections.singletonList( backgroundToScene2dSemaphore ),
//            scene2dToScene3dSemaphore
//        ) );
//        var scene3DFuture = executorService.submit( scene3DWorker.prepare(
//            scene3DGraphicCommandPool,
//            swapChain,
//            trianglePipelines.getScene3DPipeline(),
//            linePipelines.getScene3DPipeline(),
//            Collections.singletonList( scene2dToScene3dSemaphore ),
//            scene3dToOverlaySemaphore
//        ) );
        var overlayFuture = executorService.submit( overlayWorker.prepare(
            overlayGraphicCommandPool,
            swapChain,
            trianglePipelines.getOverlayPipeline(),
            linePipelines.getOverlayPipeline(),
//            Collections.singletonList( scene3dToOverlaySemaphore ),
            Collections.singletonList( backgroundToScene2dSemaphore ),
            imageIndex.getRenderFinishedSemaphore()
        ) );
        
        waitThenRenderStageInfo( BACKGROUND, backgroundFuture );
//        waitThenRenderStageInfo( SCENE_2D, scene2DFuture );
//        waitThenRenderStageInfo( SCENE_3D, scene3DFuture );
        waitThenRenderStageInfo( OVERLAY, overlayFuture );
        
        for ( var commandPool : transferCommandPools ) {
            var transferSemaphore = new VulkanSemaphore( device );
            initialWaitSemaphores.add( transferSemaphore );
            commandPool.executeTransfers( transferSemaphore );
        }
        swapChain.presentQueue( imageIndex );
    }
    
    private VulkanGraphicPrimaryCommandBuffer setUpPrimaryBufferForPool(
        VulkanGraphicCommandPool commandPool,
        VulkanSwapChain swapChain
    ) {
        commandPool.resetPrimaryBuffers();
        commandPool.resetSecondaryBuffers();
        var buffer = commandPool.provideNextPrimaryBuffer( swapChain );
        buffer.fillQueueSetup();
        return buffer;
    }
    
    private void waitThenRenderStageInfo(
        String stageName,
        Future< ? > stageFuture
    ) {
        try {
            stageFuture.get();
        } catch ( InterruptedException | ExecutionException ex ) {
            logger.log( Level.WARNING, ex, () -> "Exception while rendering  " + stageName + "." );
        }
    }
    
    private class StageRenderInfo {
        private final VulkanRenderPass renderPass;
        private final List< VulkanGraphicSecondaryCommandBuffer > buffers;
    
        private StageRenderInfo(
            VulkanRenderPass renderPass,
            List< VulkanGraphicSecondaryCommandBuffer > buffers
        ) {
            this.renderPass = renderPass;
            this.buffers = buffers;
        }
    }
    
    private void renderStage(
        Stage stage,
        VulkanGraphicCommandPool graphicCommandPool,
        VulkanSwapChain swapChain,
        VulkanGraphicPipelines.Pipeline trianglePipeline,
        VulkanGraphicPipelines.Pipeline linePipeline,
        List< VulkanSemaphore > initialWaitSemaphores,
        VulkanSemaphore signalSemaphore
    ) {
        synchronized ( VulkanRenderingContext.class ) {
            var primaryBuffer = setUpPrimaryBufferForPool(
                graphicCommandPool,
                swapChain
            );
            var camera = stage.camera;
            var renderDataByTexture = stage.renderDataByTexture;
            var renderPass = trianglePipeline.getRenderPass();
            primaryBuffer.beginRenderPass(
                renderPass,
                frameBuffer,
                backgroundColour
            );
    
            var secondaryBuffersDeque = graphicCommandPool.provideNextSecondaryBuffers( renderDataByTexture.size() );
            var secondaryBuffers = new ArrayList<>( secondaryBuffersDeque );
    
            if ( !renderDataByTexture.isEmpty() ) {
                var shader = getShader();
                var
                    descriptorPool =
                    device
                        .getDescriptor()
                        .createDescriptorPool( renderDataByTexture.size() );
                descriptorsPools.add( descriptorPool );
        
                shader.bindCamera( camera.getMatrixRepresentation() );
                var uniformBufferInfo = shader.bindUniformData();
        
                renderDataByTexture.forEach( ( texture, renderDataList ) -> {
            
                    VulkanGraphicSecondaryCommandBuffer secondaryBuffer;
                    synchronized ( secondaryBuffersDeque ) {
                        secondaryBuffer = secondaryBuffersDeque.pop();
                    }
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
            primaryBuffer.endRenderPass();
            primaryBuffer.fillQueueTearDown();
            graphicCommandPool.submitQueue(
                primaryBuffer,
                initialWaitSemaphores,
                signalSemaphore
            );
        }
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
    
    private class RenderStageWorker implements Runnable {
    
        private final Stage stage;
        private VulkanGraphicCommandPool commandPool;
        private VulkanGraphicPipelines.Pipeline trianglePipeline;
        private VulkanGraphicPipelines.Pipeline linePipeline;
        private List< VulkanSemaphore > initialWaitSemaphores;
        private VulkanSemaphore signalSemaphore;
        private VulkanSwapChain swapChain;
    
        private RenderStageWorker( Stage stage ) {
            this.stage = stage;
        }
        
        RenderStageWorker prepare(
            VulkanGraphicCommandPool commandPool,
            VulkanSwapChain swapChain,
            VulkanGraphicPipelines.Pipeline trianglePipeline,
            VulkanGraphicPipelines.Pipeline linePipeline,
            List< VulkanSemaphore > initialWaitSemaphores,
            VulkanSemaphore signalSemaphore
        ) {
            this.commandPool = commandPool;
            this.swapChain = swapChain;
            this.trianglePipeline = trianglePipeline;
            this.linePipeline = linePipeline;
            this.initialWaitSemaphores = initialWaitSemaphores;
            this.signalSemaphore = signalSemaphore;
            return this;
        }
        @Override
        public void run() {
            renderStage(
                stage,
                commandPool,
                swapChain,
                trianglePipeline,
                linePipeline,
                initialWaitSemaphores,
                signalSemaphore
            );
        }
    }
    
    private interface PipelineSupplier {
        VulkanGraphicPipelines.Pipeline get();
    }
    
    private interface CommandPoolSupplier {
        VulkanGraphicCommandPool get();
    }
    
    private static class LastPipelineHolder {
        private VulkanGraphicPipelines.Pipeline lastPipeline = null;
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
