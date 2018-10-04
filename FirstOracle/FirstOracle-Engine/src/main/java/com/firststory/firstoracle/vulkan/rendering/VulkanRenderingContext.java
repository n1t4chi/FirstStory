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
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import org.joml.Matrix4fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author n1t4chi
 */
public class VulkanRenderingContext implements RenderingContext {
    
    private final VulkanPhysicalDevice device;
    private final List< VulkanDataBuffer > dataBuffers = new ArrayList<>( 5000 );
    private final boolean shouldDrawBorder;
    private final boolean shouldDrawTextures;
    
    private final Map< Matrix4fc, Map< Texture, List< RenderData > > > renderDataByCamera = new LinkedHashMap<>();
    private final Stage background = new Stage();
    private final Stage overlay = new Stage();
    private final Stage scene2D = new Stage();
    private final Stage scene3D = new Stage();
    private Colour backgroundColour = FirstOracleConstants.BLACK;
    private final List< VulkanTextureSampler > samplers = new ArrayList<>();
    private final List< VulkanDescriptorPool > descriptorsPools = new ArrayList<>();
    private VulkanGraphicPipelines.Pipeline lastPipeline = null;
    private VulkanTextureSampler textureSampler;
    
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
    }
    
    public void setUpSingleRender() {
        getShader().resetUniformData();
        background.clear();
        overlay.clear();
        scene2D.clear();
        scene3D.clear();
        renderDataByCamera.forEach( ( camera, renderDataByTexture ) -> renderDataByTexture.forEach( ( texture, renderDataList ) -> renderDataList.clear() ) );
    }
    
    private enum StageType {
        background, scene2D, scene3D, overlay
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
        VulkanGraphicCommandPool graphicCommandPool
    ) {
        samplers.forEach( VulkanTextureSampler::dispose );
        descriptorsPools.forEach( VulkanDescriptorPool::dispose );
        samplers.clear();
        descriptorsPools.clear();
        
        textureSampler = new VulkanTextureSampler( device );
        
        lastPipeline = null;
        Deque< VulkanDataBuffer > availableDataBuffers = new LinkedList<>( dataBuffers );
        
        var buffer = graphicCommandPool.extractNextCommandBuffer();
        buffer.fillQueueSetup();
        samplers.add( textureSampler );
//        buffer.beginRenderPass( trianglePipelines.getBackgroundPipeline().getRenderPass(), backgroundColour );
        background.forEach( ( camera, renderDataByTexture ) -> renderStage( trianglePipelines, linePipelines, availableDataBuffers, buffer, camera, renderDataByTexture, StageType.background ) );
        scene2D.forEach( ( camera, renderDataByTexture ) -> renderStage( trianglePipelines, linePipelines, availableDataBuffers, buffer, camera, renderDataByTexture, StageType.scene2D ) );
        scene3D.forEach( ( camera, renderDataByTexture ) -> renderStage( trianglePipelines, linePipelines, availableDataBuffers, buffer, camera, renderDataByTexture, StageType.scene3D ) );
        overlay.forEach( ( camera, renderDataByTexture ) -> renderStage( trianglePipelines, linePipelines, availableDataBuffers, buffer, camera, renderDataByTexture, StageType.overlay ) );
        
        
        buffer.fillQueueTearDown();
        graphicCommandPool.submitQueue( buffer );
        graphicCommandPool.executeTearDown( buffer );
    }
    
    private void renderStage(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines linePipelines,
        Deque< VulkanDataBuffer > availableDataBuffers,
        VulkanGraphicCommandBuffer buffer,
        Camera camera,
        Map< Texture, List< RenderData > > renderDataByTexture,
        StageType type
    ) {
        if ( renderDataByTexture.isEmpty() ) {
            return;
        }
        VulkanGraphicPipelines.Pipeline trianglePipeline;
        VulkanGraphicPipelines.Pipeline linePipeline;
        switch ( type ) {
            case background:
                trianglePipeline = trianglePipelines.getBackgroundPipeline();
                linePipeline = linePipelines.getBackgroundPipeline();
                break;
            case overlay:
                trianglePipeline = trianglePipelines.getOverlayPipeline();
                linePipeline = linePipelines.getOverlayPipeline();
                break;
            case scene2D:
                trianglePipeline = trianglePipelines.getScene2DPipeline();
                linePipeline = linePipelines.getScene2DPipeline();
                break;
            case scene3D:
                trianglePipeline = trianglePipelines.getScene3DPipeline();
                linePipeline = linePipelines.getScene3DPipeline();
                break;
            default: throw new RuntimeException( "Unknown stage type: " + type );
        }
        
//        device.getDepthResources().clearDepthImage( buffer, buffer.getStencilValue() );
        buffer.beginRenderPass( trianglePipeline.getRenderPass(), backgroundColour );
        
        var shader = getShader();
        var descriptorPool = device.getDescriptor().createDescriptorPool( renderDataByTexture.size() );
        descriptorsPools.add( descriptorPool );
        
        shader.bindCamera( camera.getMatrixRepresentation() );
        var uniformBufferInfo = shader.bindUniformData();
        renderDataByTexture.forEach( ( texture, renderDataList ) -> {
            if ( !renderDataList.isEmpty() ) {
                renderTextureData(
                    trianglePipelines,
                    trianglePipeline,
                    linePipelines,
                    linePipeline,
                    buffer,
                    availableDataBuffers,
                    descriptorPool,
                    uniformBufferInfo,
                    texture,
                    renderDataList
                );
            }
        } );
        buffer.endRenderPass();
    }
    
    private void renderTextureData(
        VulkanGraphicPipelines trianglePipelines,
        VulkanGraphicPipelines.Pipeline trianglePipeline,
        VulkanGraphicPipelines linePipelines,
        VulkanGraphicPipelines.Pipeline linePipeline,
        VulkanGraphicCommandBuffer buffer,
        Deque< VulkanDataBuffer > availableDataBuffers,
        VulkanDescriptorPool descriptorPool,
        VkDescriptorBufferInfo uniformBufferInfo,
        Texture texture,
        List< RenderData > renderDataList
    ) {
        var textureBuffer = device.getTextureLoader().bind( shouldDrawTextures ? texture : FirstOracleConstants.EMPTY_TEXTURE );
        
        var descriptorSet = descriptorPool.getNextDescriptorSet();
        descriptorSet.updateDescriptorSet( textureSampler, textureBuffer.getContext(), uniformBufferInfo );
        
        buffer.bindDescriptorSets( linePipelines, descriptorSet );
        buffer.bindDescriptorSets( trianglePipelines, descriptorSet );
        
        //bind data
        renderDataList.forEach( renderData -> {
            var dataBuffer = loadObjectData( availableDataBuffers, renderData );
            
            var loader = device.getVertexAttributeLoader();
            
            var uvBuffer = loader.extractUvMapBuffer( renderData.getUvMap(), renderData.getUvDirection(), renderData.getUvFrame() );
            var vertexBuffer = loader.extractVerticesBuffer( renderData.getVertices(), renderData.getVertexFrame() );
            var colouringBuffer = loader.extractColouringBuffer( renderData.getColouring() );
            
            var bufferSize = renderData.getVertices().getVertexLength( renderData.getVertexFrame() );
            
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
            if ( lastPipeline != pipeline ) {
                buffer.bindPipeline( pipeline );
                lastPipeline = pipeline;
            }
            
            buffer.draw( vertexBuffer, uvBuffer, colouringBuffer, dataBuffer, bufferSize );
        } );
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
            dataBuffer.load( data );
        } else {
            dataBuffer = device.getBufferProvider().createVertexBuffer( data );
            dataBuffers.add( dataBuffer );
        }
        return dataBuffer;
    }
    
    private VulkanShaderProgram getShader() {
        return device.getShaderProgram3D();
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
        
        private void forEach( BiConsumer< Camera, Map< Texture, List< RenderData > > > consumer ) {
            consumer.accept( camera, renderDataByTexture );
        }
    }
}
