/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.data.TextureBuffer;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.window.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.window.vulkan.VulkanTextureData;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanDataBuffer;
import org.joml.Matrix4fc;
import org.joml.Vector4fc;
import org.lwjgl.vulkan.VK10;

import java.util.*;

/**
 * @author n1t4chi
 */
public class VulkanRenderingContext implements RenderingContext {
    private final VulkanPhysicalDevice device;
    private final List< VulkanDataBuffer > dataBuffers = new ArrayList<>( 5000 );
    private final VulkanObject2DRenderingContext context2D;
    private final VulkanObject3DRenderingContext context3D;
    private final VulkanDescriptor descriptor;
    private Vector4fc backgroundColour = FirstOracleConstants.VECTOR_ZERO_4F;
    private Map< Matrix4fc, Map< Texture, List< RenderData > > > renderDataByCamera = new LinkedHashMap<>();
    private Matrix4fc lastCamera = null;
    
    public VulkanRenderingContext( VulkanPhysicalDevice device, VulkanDescriptor descriptor ) {
        this.device = device;
        this.descriptor = descriptor;
        
        context2D = new VulkanObject2DRenderingContext( this );
        context3D = new VulkanObject3DRenderingContext( this );
    }
    
    public void setUpSingleRender(
    ) {
        renderDataByCamera.forEach(
            ( camera, renderDataByTexture ) -> renderDataByTexture.forEach(
                ( texture, renderDataList ) -> renderDataList.clear() )
        );
    }
    private VulkanGraphicPipeline lastPipeline = null;
    
    public void tearDownSingleRender(
        VulkanGraphicCommandPool graphicCommandPool
    ) {
        lastPipeline = null;
        Deque< VulkanDataBuffer > availableBuffers = new LinkedList<>( dataBuffers );
        
        descriptor.resetDescriptors();
    
        VulkanGraphicCommandBuffer buffer = graphicCommandPool.extractNextCommandBuffer();
        buffer.fillQueueSetup();
        
        VulkanShaderProgram3D shader = getShaderProgram3D();
        renderDataByCamera.forEach( ( camera, renderDataByTexture ) -> {
            shader.bindCamera( camera );
            renderDataByTexture.forEach( ( texture, renderDataList ) -> {
                if ( renderDataList.isEmpty() ) {
                    return;
                }
    
                VulkanGraphicPipeline linePipeline =  new VulkanGraphicPipeline( device, VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST );
                device.updatePipeline( linePipeline );
                
                VulkanGraphicPipeline trianglePipeline = new VulkanGraphicPipeline( device, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST );
                device.updatePipeline( trianglePipeline );
                
                VulkanTextureSampler textureSampler = new VulkanTextureSampler( device );
                VulkanDescriptor descriptor = new VulkanDescriptor( device );
                descriptor.resetDescriptors();
                TextureBuffer< VulkanTextureData > textureBuffer = device.getTextureLoader().bind( texture );
    
                VulkanDescriptorSet descriptorSet = descriptor.getNextDescriptorSet();
                descriptorSet.updateDesciptorSet( shader, textureSampler, textureBuffer.getContext() );
                shader.bindUniformData();
                
                buffer.bindDescriptorSets( linePipeline, descriptorSet );
                buffer.bindDescriptorSets( trianglePipeline, descriptorSet );
                buffer.beginRenderPass( linePipeline.getRenderPass() );
                
                //bind data
                renderDataList.forEach( renderData -> {
                    VulkanDataBuffer dataBuffer = loadObjectData( availableBuffers, renderData );
    
                    VulkanVertexAttributeLoader loader = device.getVertexAttributeLoader();
    
                    VulkanDataBuffer uvVuffer = loader.extractUvMapBuffer(
                        renderData.getUvMap(),
                        renderData.getUvDirection(),
                        renderData.getUvFrame()
                    );
                    VulkanDataBuffer vertexBuffer = loader.extractVerticesBuffer(
                        renderData.getVertices(),
                        renderData.getVertexFrame()
                    );
                    VulkanDataBuffer colouringBuffer = loader.extractColouringBuffer( renderData.getColouring() );
    
                    int bufferSize = renderData.getVertices().getVertexLength( renderData.getVertexFrame() );
                    
                    
                    
                    VulkanGraphicPipeline pipeline;
                    switch ( renderData.getType() ) {
                        case LINE_LOOP:
                            buffer.setLineWidth( renderData.getLineWidth() == 0 ? 0 : renderData.getLineWidth() + 1f );
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
                        uvVuffer,
                        colouringBuffer,
                        dataBuffer,
                        bufferSize
                    );
                } );
    
                buffer.endRenderPassIfActive();
                
            } );
        } );
        
        buffer.fillQueueTearDown();
        graphicCommandPool.submitQueue( buffer );
        graphicCommandPool.executeTearDown( buffer );
    }
    
    private VulkanDataBuffer loadObjectData( Deque< VulkanDataBuffer > availableBuffers, RenderData renderData ) {
        VulkanShaderProgram3D shader = device.getShaderProgram3D();
        shader.bindPosition( renderData.getPosition() );
        shader.bindScale( renderData.getScale() );
        shader.bindRotation( renderData.getRotation() );
        shader.bindOverlayColour( renderData.getOverlayColour() );
        shader.bindMaxAlphaChannel( renderData.getMaxAlphaChannel() );
        float[] data = shader.getInputData();
        
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
    
    public Vector4fc getBackgroundColour() {
        return this.backgroundColour;
    }
    
    @Override
    public void setBackgroundColour( Vector4fc backgroundColour ) {
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
        VulkanShaderProgram3D shader = getShaderProgram3D();
        if ( shader.didCameraChange() ) {
            lastCamera = shader.getCamera();
        }
        Map< Texture, List< RenderData > > renderDataByTexture = renderDataByCamera.computeIfAbsent(
            lastCamera,
            camera -> new LinkedHashMap<>()
        );
        
        List< RenderData > renderData = renderDataByTexture.computeIfAbsent(
            data.getTexture(),
            texture -> new ArrayList<>()
        );
        
        renderData.add( data );
    }
    
    boolean shouldUseTextures() {
        return true;
    }
    
    boolean shouldDrawBorder() {
        return true;
    }
    
    private VulkanShaderProgram3D getShaderProgram3D() {
        return device.getShaderProgram3D();
    }
    
    private VulkanShaderProgram3D getShaderProgram2D() {
        return device.getShaderProgram3D();
    }
}
