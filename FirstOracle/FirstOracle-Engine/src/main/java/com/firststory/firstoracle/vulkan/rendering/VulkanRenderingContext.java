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
import com.firststory.firstoracle.vulkan.VulkanFrameBuffer;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.VulkanTransferCommandPool;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
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
    public static final String OVERLAY = "overlay";
    public static final String SCENE_3D = "scene3D";
    public static final String SCENE_2D = "scene2D";
    public static final String BACKGROUND = "background";
    
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
    private VulkanGraphicCommandPool graphicCommandPool;
    private VulkanFrameBuffer frameBuffer;
    private VulkanGraphicPrimaryCommandBuffer primaryBuffer;
    
    private RenderStageWorker backgroundWorker = new RenderStageWorker(
        background,
        () -> trianglePipelines.getBackgroundPipeline(),
        () -> linePipelines.getBackgroundPipeline()
    );
    private RenderStageWorker scene2DWorker = new RenderStageWorker(
        scene2D,
        () -> trianglePipelines.getScene2DPipeline(),
        () -> linePipelines.getScene2DPipeline()
    );
    private RenderStageWorker scene3DWorker = new RenderStageWorker(
        scene3D,
        () -> trianglePipelines.getScene3DPipeline(),
        () -> linePipelines.getScene3DPipeline()
    );
    private RenderStageWorker overlayWorker = new RenderStageWorker(
        overlay,
        () -> trianglePipelines.getOverlayPipeline(),
        () -> linePipelines.getOverlayPipeline()
    );
    
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
        VulkanGraphicCommandPool graphicCommandPool,
        VulkanTransferCommandPool transferCommandPool
    ) {
        this.trianglePipelines = trianglePipelines;
        this.linePipelines = linePipelines;
        this.graphicCommandPool = graphicCommandPool;
        
        samplers.forEach( VulkanTextureSampler::dispose );
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        availableDataBuffers.clear();
        samplers.clear();
        descriptorsPools.clear();
        
        textureSampler = new VulkanTextureSampler( device );
        
        availableDataBuffers.addAll( dataBuffers );
    
        transferCommandPool.executeTransfers();
        frameBuffer = device.getCurrentFrameBuffer();
        graphicCommandPool.resetPrimaryBuffers();
        graphicCommandPool.resetSecondaryBuffers();
        
        primaryBuffer = graphicCommandPool.provideNextPrimaryBuffer();
        primaryBuffer.fillQueueSetup();
        samplers.add( textureSampler );
    
        renderStageInfo( backgroundWorker.call() );
        renderStageInfo( scene2DWorker.call() );
        renderStageInfo( scene3DWorker.call() );
        renderStageInfo( overlayWorker.call() );
        
//        todo: uncomment
//        var backgroundFuture = executorService.submit( backgroundWorker );
//        var scene2DFuture = executorService.submit( scene2DWorker );
//        var scene3DFuture = executorService.submit( scene3DWorker );
//        var overlayFuture = executorService.submit( overlayWorker );
//
//        getAndRenderStageInfo( backgroundFuture, BACKGROUND );
//        getAndRenderStageInfo( scene2DFuture, SCENE_2D );
//        getAndRenderStageInfo( scene3DFuture, SCENE_3D );
//        getAndRenderStageInfo( overlayFuture, OVERLAY );
    
        transferCommandPool.executeTransfers();
        primaryBuffer.fillQueueTearDown();
        graphicCommandPool.submitQueue( primaryBuffer );
        graphicCommandPool.executeTearDown( device.getCurrentImageIndex() );
        
    }
    
    public void getAndRenderStageInfo( Future< StageRenderInfo > stageFuture, String stageName ) {
        try {
            renderStageInfo( stageFuture.get() );
        } catch ( InterruptedException | ExecutionException ex ) {
            logger.log( Level.WARNING, ex, () -> {
                return "Exception while rendering  " + stageName + ".";
            } );
        }
    }
    
    private void renderStageInfo( StageRenderInfo info ) {
        primaryBuffer.beginRenderPass( info.renderPass, frameBuffer, backgroundColour );
        primaryBuffer.executeSecondaryBuffers( info.buffers );
        primaryBuffer.endRenderPass();
    }
    
    private class StageRenderInfo {
        private final VulkanRenderPass renderPass;
        private final List< VulkanGraphicSecondaryCommandBuffer> buffers;
    
        private StageRenderInfo( VulkanRenderPass renderPass, List< VulkanGraphicSecondaryCommandBuffer > buffers ) {
            this.renderPass = renderPass;
            this.buffers = buffers;
        }
    }
    
    private StageRenderInfo renderStage(
        Stage stage,
        VulkanGraphicPipelines.Pipeline trianglePipeline,
        VulkanGraphicPipelines.Pipeline linePipeline
    ) {
        var camera = stage.camera;
        var renderDataByTexture = stage.renderDataByTexture;
        var renderPass = trianglePipeline.getRenderPass();
        var secondaryBuffersDeque = graphicCommandPool.provideNextSecondaryBuffers( renderDataByTexture.size() );
        var secondaryBuffers = new ArrayList<>( secondaryBuffersDeque );
        
        if ( !renderDataByTexture.isEmpty() ) {
            var shader = getShader();
            var descriptorPool = device.getDescriptor().createDescriptorPool( renderDataByTexture.size() );
            descriptorsPools.add( descriptorPool );
            
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
        
        return new StageRenderInfo( renderPass, secondaryBuffers );
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
        if ( !availableBuffers.isEmpty() ) {
            dataBuffer = availableBuffers.poll();
            dataBuffer.load2( data );
        } else {
            dataBuffer = device.getBufferProvider().createQuickVertexBuffer( data );
            dataBuffers.add( dataBuffer );
        }
        return dataBuffer;
    }
    
    private VulkanShaderProgram getShader() {
        return device.getShaderProgram3D();
    }
    
    private class RenderStageWorker implements Callable< StageRenderInfo > {
        
        private final Stage stage;
        private final PipelineSupplier triangleSupplier;
        private final PipelineSupplier lineSupplier;
        
        private RenderStageWorker( Stage stage, PipelineSupplier triangleSupplier, PipelineSupplier lineSupplier  ) {
            this.stage = stage;
            this.triangleSupplier = triangleSupplier;
            this.lineSupplier = lineSupplier;
        }
        
        @Override
        public StageRenderInfo call() {
            return renderStage( stage, triangleSupplier.get(), lineSupplier.get() );
        }
    }
    
    private interface PipelineSupplier {
        VulkanGraphicPipelines.Pipeline get();
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
