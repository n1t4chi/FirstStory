/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.rendering.Object2DRenderingContext;
import com.firststory.firstoracle.rendering.Object3DRenderingContext;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.window.vulkan.buffer.VulkanDataBuffer;
import org.joml.Vector4fc;

import java.util.Arrays;

/**
 * @author n1t4chi
 */
class VulkanRenderingContext implements RenderingContext {
    
    static final float[] COLOUR = new float[ 400 ];
    static {
        Arrays.fill( COLOUR, 0 );
    }
    
    
    private final VulkanPhysicalDevice device;
    private final VulkanVertexAttributeLoader attributeLoader;
    private VulkanAddress descriptorSet;
    private VulkanGraphicCommandBuffer commandBuffer;
    private VulkanTransferCommandBuffer transferBuffer;
    private final VulkanDataBuffer colourBuffer;
    private final VulkanDataBuffer[] dataBuffers = new VulkanDataBuffer[500];
    private int iterator = 0;
    private Vector4fc backgroundColour = FirstOracleConstants.VECTOR_ZERO_4F;
    private final VulkanObject2DRenderingContext context2D;
    private final VulkanObject3DRenderingContext context3D;
    
    VulkanRenderingContext( VulkanPhysicalDevice device ) {
        this.device = device;
        this.attributeLoader = device.getVertexAttributeLoader();
    
        
        VulkanBufferProvider bufferProvider = device.getBufferProvider();
    
        float[] floats = getShaderProgram3D().getInputData();
        
        colourBuffer = bufferProvider.createVertexBuffer( COLOUR );
        for ( int i = 0; i < dataBuffers.length; i++ ) {
            dataBuffers[ i ] = bufferProvider.createVertexBuffer( floats );
        }
    
        context2D = new VulkanObject2DRenderingContext( this );
        context3D = new VulkanObject3DRenderingContext( this );
    }
    
    void setLineWidth( float width ) {
    
    }
    
    VulkanShaderProgram3D getShaderProgram2D() {
        return device.getShaderProgram3D();
    }
    
    VulkanShaderProgram3D getShaderProgram3D() {
        return device.getShaderProgram3D();
    }
    
    VulkanVertexAttributeLoader getVertexAttributeLoader() {
        return device.getVertexAttributeLoader();
    }
    
    VulkanTextureLoader getTextureLoader() {
        return device.getTextureLoader();
    }
    
    void drawTriangles( int bufferSize ) {
        float[] floats = getShaderProgram3D().getInputData();
        getShaderProgram3D().bindUniformData( descriptorSet, commandBuffer );
        
        final int currentIterator = iterator;
        dataBuffers[ currentIterator ].load( floats );
//        commands.add( commandBuffer1 -> {
        commandBuffer.drawVertices(
            attributeLoader.getLastBoundPositionBuffer(),
            attributeLoader.getLastBoundUvMapBuffer(),
            colourBuffer,
            dataBuffers[ currentIterator ],
            bufferSize
        );
//        } );
        iterator = (iterator + 1) % dataBuffers.length;
    }
    
    void drawLineLoop( int bufferSize ) {
    
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
    
    boolean shouldUseTextures() {
        return true;
    }
    
    boolean shouldDrawBorder() {
        return false;
    }
    
    Vector4fc getBackgroundColour( ) {
        return this.backgroundColour;
    }
    
    void setUpSingleRender(
        VulkanAddress descriptorSet,
        VulkanGraphicCommandBuffer commandBuffer,
        VulkanTransferCommandBuffer transferBuffer
    ) {
        this.descriptorSet = descriptorSet;
        this.commandBuffer = commandBuffer;
        this.transferBuffer = transferBuffer;
        commandBuffer.fillQueueSetup();
        iterator = 0;
    }
    
    void tearDownSingleRender( VulkanGraphicCommandPool graphicCommandPool ) {
    
        commandBuffer.fillQueueTearDown();
        graphicCommandPool.submitQueue( commandBuffer );
        graphicCommandPool.executeTearDown( commandBuffer );
    }
}
