/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector4fc;

import java.util.LinkedList;

/**
 * @author n1t4chi
 */
class VulkanRenderingContext implements RenderingContext {
    
    
    static final float[] POSITION_1 = new float[]{
        /*3*/ /*pos*/ -0.5f, -0.5f, 0.0f,
        /*2*/ /*pos*/ -0.5f, 0.5f, 0.0f,
        /*1*/ /*pos*/ 0.5f, 0.5f, 0.0f,
    };
    static final float[] COLOUR_1 = new float[]{
        /*1*/ /*col*/ 1.0f, 0.0f, 1.0f, 1.0f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.0f, 1.0f,
        /*3*/ /*col*/ 0.0f, 1.0f, 1.0f, 1.0f,
    };
    static final float[] UVMAP_1 = new float[]{
        /*3*/ /*pos*/ 0f, 0f,
        /*2*/ /*pos*/ 1f, 1f,
        /*1*/ /*pos*/ 1f, 0f,
    };
    static final float[] POSITION_2 = new float[]{
        /*3*/ /*pos*/ 1.0f, 1.0f, 0.5f,
        /*1*/ /*pos*/ 1.0f, -1.0f, 0.5f,
        /*2*/ /*pos*/ -1.0f, 1.0f, 0.5f,
    };
    static final float[] COLOUR_2 = new float[]{
        /*1*/ /*col*/ 0.5f, 0.5f, 0.5f, 1.0f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.5f, 1.5f,
        /*3*/ /*col*/ 0.5f, 1.0f, 1.0f, 1.5f,
    };
    static final float[] UVMAP_2 = new float[]{
        /*3*/ /*pos*/ 0f, 0f,
        /*2*/ /*pos*/ 0f, 1f,
        /*1*/ /*pos*/ 1f, 1f,
    };
    static final float[] POSITION_3 = new float[]{
        /*1*/ /*pos*/ 1.0f, 0.0f, 0.1f,
        /*2*/ /*pos*/ 0.0f, -1.0f, 0.1f,
        /*3*/ /*pos*/ -1.0f, 0.0f, 0.1f,
    };
    static final float[] COLOUR_3 = new float[]{
        /*1*/ /*col*/ 0.5f, 0.5f, 0.5f, 1.0f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.5f, 1.5f,
        /*3*/ /*col*/ 0.5f, 1.0f, 1.0f, 1.5f,
    };
    static final float[] UVMAP_3 = new float[]{
        /*3*/ /*pos*/ 0f, 0f,
        /*2*/ /*pos*/ 0f, 1f,
        /*1*/ /*pos*/ 1f, 1f,
    };
    
    private final VulkanPhysicalDevice device;
    private VulkanAddress descriptorSet;
    private VulkanGraphicCommandBuffer commandBuffer;
    private VulkanCommands commands = new VulkanCommands();
    private final VulkanStageableDataBuffer< float[] > positionBuffer1;
    private final VulkanStageableDataBuffer< float[] > positionBuffer2;
    private final VulkanStageableDataBuffer< float[] > positionBuffer3;
    private final VulkanStageableDataBuffer< float[] > colourBuffer1;
    private final VulkanStageableDataBuffer< float[] > colourBuffer2;
    private final VulkanStageableDataBuffer< float[] > colourBuffer3;
    private final VulkanStageableDataBuffer< float[] > uvBuffer1;
    private final VulkanStageableDataBuffer< float[] > uvBuffer2;
    private final VulkanStageableDataBuffer< float[] > uvBuffer3;
    
    VulkanRenderingContext( VulkanPhysicalDevice device ) {
        this.device = device;
    
        VulkanDataBufferProvider bufferProvider = device.getBufferProvider();
        positionBuffer1 = bufferProvider.createFloatBuffer();
        positionBuffer1.load( POSITION_1 );
        positionBuffer1.bind();
        positionBuffer2 = bufferProvider.createFloatBuffer();
        positionBuffer2.load( POSITION_2 );
        positionBuffer2.bind();
        positionBuffer3 = bufferProvider.createFloatBuffer();
        positionBuffer3.load( POSITION_3 );
        positionBuffer3.bind();
    
        colourBuffer1 = bufferProvider.createFloatBuffer();
        colourBuffer1.load( COLOUR_1 );
        colourBuffer1.bind();
        colourBuffer2 = bufferProvider.createFloatBuffer();
        colourBuffer2.load( COLOUR_2 );
        colourBuffer2.bind();
        colourBuffer3 = bufferProvider.createFloatBuffer();
        colourBuffer3.load( COLOUR_3 );
        colourBuffer3.bind();
    
        uvBuffer1 = bufferProvider.createFloatBuffer();
        uvBuffer1.load( UVMAP_1 );
        uvBuffer1.bind();
        uvBuffer2 = bufferProvider.createFloatBuffer();
        uvBuffer2.load( UVMAP_2 );
        uvBuffer2.bind();
        uvBuffer3 = bufferProvider.createFloatBuffer();
        uvBuffer3.load( UVMAP_3 );
        uvBuffer3.bind();

        commands.add( commandBuffer -> {
            device.getShaderProgram3D().bindUniformData( descriptorSet, commandBuffer );
            commandBuffer.drawVertices( positionBuffer1, uvBuffer1, colourBuffer1 );
            commandBuffer.drawVertices( positionBuffer2, uvBuffer2, colourBuffer2 );
            device.getShaderProgram3D().bindMaxAlphaChannel( 0.5f );
            device.getShaderProgram3D().bindUniformData( descriptorSet, commandBuffer );
            commandBuffer.drawVertices( positionBuffer3, uvBuffer3, colourBuffer3 );
        });
    }
    
    @Override
    public VulkanShaderProgram2D getShaderProgram2D() {
        return device.getShaderProgram2D();
    }
    
    @Override
    public VulkanShaderProgram3D getShaderProgram3D() {
        return device.getShaderProgram3D();
    }
    
    @Override
    public VulkanVertexAttributeLoader getVertexAttributeLoader() {
        return device.getVertexAttributeLoader();
    }
    
    @Override
    public VulkanTextureLoader getTextureLoader() {
        return device.getTextureLoader();
    }
    
    @Override
    public void setLineWidth( float width ) {
    
    }
    
    @Override
    public void drawLines( int bufferedAmount ) {
    
    }
    
    @Override
    public void drawTriangles( int bufferSize ) {
//        device.getShaderProgram3D().bindUniformData( descriptorSet, commandBuffer );
//        commandBuffer.drawVertices( positionBuffer2, uvBuffer2, colourBuffer2 );
    }
    
    @Override
    public void drawLineLoop( int bufferSize ) {
    
    }
    
    @Override
    public void enableVertexAttributes() {}
    
    @Override
    public void disableVertexAttributes() {}
    
    @Override
    public void setBackgroundColour( Vector4fc backgroundColour ) {
    
    }
    
    @Override
    public void disableDepth() {}
    
    @Override
    public void enableDepth() {}
    
    @Override
    public boolean getUseTexture() {
        return true;
    }
    
    @Override
    public boolean getDrawBorder() {
        return false;
    }
    
    @Override
    public Vector4fc getBorderColour() {
        return null;
    }
    
    void setUpSingleRender( VulkanAddress descriptorSet, VulkanGraphicCommandBuffer commandBuffer ) {
        this.descriptorSet = descriptorSet;
        this.commandBuffer = commandBuffer;
    }
    
    void tearDownSingleRender( VulkanGraphicCommandPool graphicCommandPool ) {
        commandBuffer.fillQueue( commands );
        //commands.clear();
        graphicCommandPool.submitQueue( commandBuffer );
        graphicCommandPool.executeTearDown( commandBuffer );
    }
    
    private class VulkanCommands implements VulkanCommand< VulkanGraphicCommandBuffer > {
        
        private LinkedList< VulkanCommand< VulkanGraphicCommandBuffer > > commands = new LinkedList<>();
        
        @Override
        public void execute( VulkanGraphicCommandBuffer commandBuffer ) {
            commands.forEach( vulkanCommand -> vulkanCommand.execute( commandBuffer ) );
        }
        
        public void clear() {
            commands.clear();
        }
        
        void add( VulkanCommand< VulkanGraphicCommandBuffer > command ) {
            commands.addLast( command );
        }
    }
}
