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
import com.firststory.firstoracle.vulkan.allocators.VulkanFrameworkAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;

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
    private final List< VulkanDescriptorPool > descriptorsPools = new ArrayList<>();
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
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        descriptorsPools.clear();
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
    
    public List< VulkanSemaphore > tearDownSingleRender(
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
        var deviceAllocator = device.getAllocator();
        
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        descriptorsPools.clear();
        
        availableDataBuffers.clear();
        
        availableDataBuffers.addAll( dataBuffers );
        
        frameBuffer = device.getCurrentFrameBuffer();
    
        var backgroundFuture = executorService.submit( backgroundWorker.prepare(
            backgroundGraphicCommandPool,
            trianglePipelines.getBackgroundPipeline(),
            linePipelines.getBackgroundPipeline(),
            swapChain
        ) );
        var scene2DFuture = executorService.submit( scene2DWorker.prepare(
            scene2DGraphicCommandPool,
            trianglePipelines.getScene2DPipeline(),
            linePipelines.getScene2DPipeline(),
            swapChain
            
        ) );
        var scene3DFuture = executorService.submit( scene3DWorker.prepare(
            scene3DGraphicCommandPool,
            trianglePipelines.getScene3DPipeline(),
            linePipelines.getScene3DPipeline(),
            swapChain
            
        ) );
        var overlayFuture = executorService.submit( overlayWorker.prepare(
            overlayGraphicCommandPool,
            trianglePipelines.getOverlayPipeline(),
            linePipelines.getOverlayPipeline(),
            swapChain
            
        ) );

        var bgPrimaryBuffer = waitThenRenderStageInfo( backgroundFuture, BACKGROUND );
        var s2PrimaryBuffer = waitThenRenderStageInfo( scene2DFuture, SCENE_2D );
        var s3PrimaryBuffer = waitThenRenderStageInfo( scene3DFuture, SCENE_3D );
        var ovPrimaryBuffer = waitThenRenderStageInfo( overlayFuture, OVERLAY );
        
        var semaphoreBackgroundToScene2d = deviceAllocator.createSemaphore();
        var semaphoreScene2dToScene3d = deviceAllocator.createSemaphore();
        var semaphoreScene3dToOverlay = deviceAllocator.createSemaphore();
        
        for ( var transferCommandPool : transferCommandPools ) {
            transferCommandPool.executeTransfers();
        }
        var backgroundSubmitInfo = backgroundGraphicCommandPool.createSubmitInfo(
            bgPrimaryBuffer,
            imageIndex.getImageAvailableSemaphore(),
            semaphoreBackgroundToScene2d
        );
        var scene2DSubmitInfo = scene2DGraphicCommandPool.createSubmitInfo(
            s2PrimaryBuffer,
            semaphoreBackgroundToScene2d,
            semaphoreScene2dToScene3d
        );
        var scene3DSubmitInfo = scene3DGraphicCommandPool.createSubmitInfo(
            s3PrimaryBuffer,
            semaphoreScene2dToScene3d,
            semaphoreScene3dToOverlay
        );
        var overlaySubmitInfo = overlayGraphicCommandPool.createSubmitInfo(
            ovPrimaryBuffer,
            semaphoreScene3dToOverlay,
            imageIndex.getRenderFinishedSemaphore()
        );
        device.getGraphicQueueFamily().submit(
            backgroundSubmitInfo,
            scene2DSubmitInfo,
            scene3DSubmitInfo,
            overlaySubmitInfo
        );
        swapChain.presentQueue( imageIndex );
        return Arrays.asList(
            semaphoreBackgroundToScene2d,
            semaphoreScene2dToScene3d,
            semaphoreScene3dToOverlay
        );
    }
    
    private VulkanGraphicPrimaryCommandBuffer waitThenRenderStageInfo(
        Future< VulkanGraphicPrimaryCommandBuffer > stageFuture,
        String stageName
    ) {
        try {
            return stageFuture.get();
        } catch ( InterruptedException | ExecutionException ex ) {
            logger.log( Level.WARNING, ex, () -> "Exception while rendering  " + stageName + "." );
        }
        return null;
    }
    
    private VulkanGraphicPrimaryCommandBuffer renderStage(
        Stage stage,
        VulkanGraphicPipelines.Pipeline trianglePipeline,
        VulkanGraphicPipelines.Pipeline linePipeline,
        VulkanGraphicCommandPool graphicCommandPool,
        VulkanSwapChain swapChain
    ) {
        var camera = stage.camera;
        var renderDataByTexture = stage.renderDataByTexture;
        var renderPass = trianglePipeline.getRenderPass();
        
        graphicCommandPool.resetPrimaryBuffers();
        graphicCommandPool.resetSecondaryBuffers();
        var primaryBuffer = graphicCommandPool.provideNextPrimaryBuffer( swapChain );
        primaryBuffer.fillQueueSetup();
        primaryBuffer.beginRenderPass( renderPass, frameBuffer, backgroundColour );
        
        var secondaryBuffersDeque = graphicCommandPool.provideNextSecondaryBuffers( renderDataByTexture.size() );
        var secondaryBuffers = new ArrayList<>( secondaryBuffersDeque );
    
        if ( !renderDataByTexture.isEmpty() ) {
            var shader = getShader();
            VulkanDescriptorPool descriptorPool;
            synchronized ( descriptorsPools ) {
                descriptorPool = device.getDescriptor().createDescriptorPool( renderDataByTexture.size() );
                descriptorsPools.add( descriptorPool );
            }
            
            shader.bindCamera( camera.getMatrixRepresentation() );
            var uniformBufferInfo = shader.bindUniformData();
            
            renderDataByTexture.forEach( ( texture, renderDataList ) -> {
    
                VulkanGraphicSecondaryCommandBuffer secondaryBuffer;
                synchronized ( secondaryBuffersDeque ) {
                    secondaryBuffer = secondaryBuffersDeque.pop();
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
        primaryBuffer.endRenderPass();
        primaryBuffer.fillQueueTearDown();
        return primaryBuffer;
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
    
    private class RenderStageWorker implements Callable< VulkanGraphicPrimaryCommandBuffer > {
        
        private final Stage stage;
        private VulkanGraphicCommandPool commandPool;
        private VulkanGraphicPipelines.Pipeline trianglePipeline;
        private VulkanGraphicPipelines.Pipeline linePipeline;
        private VulkanSwapChain swapChain;
    
        private RenderStageWorker( Stage stage ) {
            this.stage = stage;
        }
        
        RenderStageWorker prepare(
            VulkanGraphicCommandPool commandPool,
            VulkanGraphicPipelines.Pipeline trianglePipeline,
            VulkanGraphicPipelines.Pipeline linePipeline,
            VulkanSwapChain swapChain
        ) {
            this.commandPool = commandPool;
            this.trianglePipeline = trianglePipeline;
            this.linePipeline = linePipeline;
            this.swapChain = swapChain;
            return this;
        }
        
        @Override
        public VulkanGraphicPrimaryCommandBuffer call() {
            return renderStage(
                stage,
                trianglePipeline,
                linePipeline,
                commandPool,
                swapChain
            );
        }
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
