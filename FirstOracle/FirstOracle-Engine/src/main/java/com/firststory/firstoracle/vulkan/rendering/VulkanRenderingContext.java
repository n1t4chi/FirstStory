/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.data.Colour;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import org.joml.Matrix4fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;

import java.util.*;

/**
 * @author n1t4chi
 */
public class VulkanRenderingContext implements RenderingContext {
    private final VulkanPhysicalDevice device;
    private final List< VulkanDataBuffer > dataBuffers = new ArrayList<>( 5000 );
    private final VulkanObject2DRenderingContext context2D;
    private final VulkanObject3DRenderingContext context3D;
    private final boolean shouldDrawBorder;
    private final boolean shouldDrawTextures;
    
    private final Map< Matrix4fc, Map< Texture, List< RenderData > > > renderDataByCamera = new LinkedHashMap<>();
    private Colour backgroundColour = FirstOracleConstants.BLACK;
    private Matrix4fc lastCamera = null;
    private VulkanGraphicPipeline lastPipeline = null;
    private final List< VulkanTextureSampler > samplers = new ArrayList<>(  );
    private final List< VulkanDescriptorPool > descriptorsPools = new ArrayList<>(  );
    private VulkanTextureSampler textureSampler;
    
    public VulkanRenderingContext( VulkanPhysicalDevice device ) {
        this(
            device,
            PropertiesUtil.isPropertyTrue( PropertiesUtil.DRAW_BORDER_PROPERTY ),
            !PropertiesUtil.isPropertyTrue( PropertiesUtil.DISABLE_TEXTURES_PROPERTY )
        );
    }
    
    public VulkanRenderingContext( VulkanPhysicalDevice device, boolean shouldDrawBorder, boolean shouldDrawTextures ) {
        this.device = device;
        this.shouldDrawBorder = shouldDrawBorder;
        this.shouldDrawTextures = shouldDrawTextures;
        context2D = new VulkanObject2DRenderingContext( this );
        context3D = new VulkanObject3DRenderingContext( this );
    }
    
    public void setUpSingleRender() {
        getShaderProgram3D().resetUniformData();
        renderDataByCamera.forEach(
            ( camera, renderDataByTexture ) -> renderDataByTexture.forEach(
                ( texture, renderDataList ) -> renderDataList.clear() )
        );
    }
    
    public void tearDownSingleRender(
        VulkanGraphicPipeline trianglePipeline,
        VulkanGraphicPipeline linePipeline,
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
        buffer.beginRenderPass( trianglePipeline.getRenderPass() );
        
        List< Matrix4fc > emptyCameras = new ArrayList<>();
    
        var shader = getShaderProgram3D();
        samplers.add( textureSampler );
        
        renderDataByCamera.forEach( ( camera, renderDataByTexture ) -> {
            if( !renderDataByTexture.isEmpty() ) {
                List< Texture > emptyRenderData = new ArrayList<>();
    
                var descriptorPool = device.getDescriptor().createDescriptorPool( renderDataByTexture.size() );
                descriptorsPools.add( descriptorPool );
    
                shader.bindCamera( camera );
                var uniformBufferInfo = shader.bindUniformData();
                renderDataByTexture.forEach( ( texture, renderDataList ) -> {
                    if ( !renderDataList.isEmpty() ) {
                        renderTextureData( trianglePipeline, linePipeline, buffer, availableDataBuffers, descriptorPool, uniformBufferInfo, texture, renderDataList );
                    } else {
                        emptyRenderData.add( texture );
                    }
                } );
    
                emptyRenderData.forEach( renderDataByTexture::remove );
            } else {
                emptyCameras.add( camera );
            }
        } );
        emptyCameras.forEach( renderDataByCamera::remove );
        
        buffer.fillQueueTearDown();
        graphicCommandPool.submitQueue( buffer );
        graphicCommandPool.executeTearDown( buffer );
    }
    
    private void renderTextureData(
        VulkanGraphicPipeline trianglePipeline,
        VulkanGraphicPipeline linePipeline,
        VulkanGraphicCommandBuffer buffer,
        Deque< VulkanDataBuffer > availableDataBuffers,
        VulkanDescriptorPool descriptorPool,
        VkDescriptorBufferInfo uniformBufferInfo,
        Texture texture,
        List< RenderData > renderDataList
    ) {
        var shader = getShaderProgram3D();
    
        var textureBuffer = device.getTextureLoader().bind( texture );
    
        var descriptorSet = descriptorPool.getNextDescriptorSet();
        descriptorSet.updateDescriptorSet( textureSampler, textureBuffer.getContext(), uniformBufferInfo );
        
        buffer.bindDescriptorSets( linePipeline, descriptorSet );
        buffer.bindDescriptorSets( trianglePipeline, descriptorSet );
        
        //bind data
        renderDataList.forEach( renderData -> {
            var dataBuffer = loadObjectData( availableDataBuffers, renderData );
    
            var loader = device.getVertexAttributeLoader();
    
            var uvBuffer = loader.extractUvMapBuffer(
                renderData.getUvMap(),
                renderData.getUvDirection(),
                renderData.getUvFrame()
            );
            var vertexBuffer = loader.extractVerticesBuffer(
                renderData.getVertices(),
                renderData.getVertexFrame()
            );
            var colouringBuffer = loader.extractColouringBuffer( renderData.getColouring() );
    
            var bufferSize = renderData.getVertices().getVertexLength( renderData.getVertexFrame() );
            
            
            
            VulkanGraphicPipeline pipeline;
            switch ( renderData.getType() ) {
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
            if( lastPipeline != pipeline ) {
                buffer.bindPipeline( pipeline );
                lastPipeline = pipeline;
            }

            buffer.draw(
                vertexBuffer,
                uvBuffer,
                colouringBuffer,
                dataBuffer,
                bufferSize
            );
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
        if( !availableBuffers.isEmpty() ) {
            dataBuffer = availableBuffers.poll();
            dataBuffer.load( data );
        } else {
            dataBuffer = device.getBufferProvider().createVertexBuffer( data );
            dataBuffers.add( dataBuffer );
        }
        return dataBuffer;
    }
    
    public Colour getBackgroundColour() {
        return this.backgroundColour;
    }
    
    @Override
    public void setBackgroundColour( Colour backgroundColour ) {
        this.backgroundColour = backgroundColour;
    }
    
    @Override
    public void render2D( Render< Object2DRenderingContext > context ) {
        context.render( context2D );
    }
    
    @Override
    public void render3D( Render< Object3DRenderingContext > context ) {
        context.render( context3D );
    }
    
    @Override
    public void useRendering2D( Camera2D camera, boolean useDepth ) {
        getShaderProgram2D().bindCamera( camera.getMatrixRepresentation() );
    }
    
    @Override
    public void useRendering3D( Camera3D camera, boolean useDepth ) {
        getShaderProgram3D().bindCamera( camera.getMatrixRepresentation() );
    }
    
    void draw( RenderData data ) {
        var shader = getShaderProgram3D();
        if ( shader.didCameraChange() ) {
            lastCamera = shader.getCamera();
        }
        var renderDataByTexture = renderDataByCamera.computeIfAbsent(
            lastCamera,
            camera -> Collections.synchronizedMap( new LinkedHashMap<>() )
        );
    
        var renderData = renderDataByTexture.computeIfAbsent(
            data.getTexture(),
            texture -> Collections.synchronizedList( new ArrayList<>() )
        );
        renderData.add( data );
    }
    
    boolean shouldUseTextures() {
        return shouldDrawTextures;
    }
    
    boolean shouldDrawBorder() {
        return shouldDrawBorder;
    }
    
    private VulkanShaderProgram3D getShaderProgram3D() {
        return device.getShaderProgram3D();
    }
    
    private VulkanShaderProgram3D getShaderProgram2D() {
        return device.getShaderProgram3D();
    }
}
