/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.rendering.RenderingContext;
import org.joml.Vector4fc;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author n1t4chi
 */
class VulkanRenderingContext implements RenderingContext {
    
    static final float[] COLOUR = new float[ 200 ];
    static {
        Arrays.fill( COLOUR, 1 );
    }
    
    
    private final VulkanPhysicalDevice device;
    private final VulkanVertexAttributeLoader attributeLoader;
    private VulkanAddress descriptorSet;
    private VulkanGraphicCommandBuffer commandBuffer;
    private VulkanCommands commands = new VulkanCommands();
    private final VulkanArrayBuffer colourBuffer;
    
    VulkanRenderingContext( VulkanPhysicalDevice device ) {
        this.device = device;
        this.attributeLoader = device.getVertexAttributeLoader();
    
        
        VulkanDataBufferProvider bufferProvider = device.getBufferProvider();
        
        colourBuffer = bufferProvider.createFloatBuffer();
        colourBuffer.load( COLOUR );
        colourBuffer.bind();
        
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
        device.getShaderProgram3D().bindUniformData( descriptorSet, commandBuffer );
        device.getShaderProgram2D().bindUniformData( descriptorSet, commandBuffer );
        commandBuffer.drawVertices( attributeLoader.getLastBoundPositionBuffer(), attributeLoader.getLastBoundUvMapBuffer(),
            colourBuffer,
            bufferSize
        );
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
        commandBuffer.fillQueueSetup();
    }
    
    void tearDownSingleRender( VulkanGraphicCommandPool graphicCommandPool ) {
    
//        commands.add( commandBuffer -> {
//            device.getShaderProgram3D().bindUniformData( descriptorSet, commandBuffer );
//            vertices3D_1.bind( attributeLoader, 0 );
//            uvMap_1.bind( attributeLoader, 0, 0 );
//            drawTriangles2( vertices3D_1.getVertexSize() );
//        });
//        commands.add( commandBuffer -> {
//            vertices3D_2.bind( attributeLoader, 0 );
//            uvMap_2.bind( attributeLoader, 0, 0 );
//            drawTriangles2( vertices3D_2.getVertexSize() );
//        });
        commandBuffer.fillQueue( commands );
        commands.clear();
        graphicCommandPool.submitQueue( commandBuffer );
        graphicCommandPool.executeTearDown( commandBuffer );
    }
    
    private class VulkanCommands implements VulkanCommand< VulkanGraphicCommandBuffer > {
        
        private final LinkedList< VulkanCommand< VulkanGraphicCommandBuffer > > commands = new LinkedList<>();
        
        @Override
        public void execute( VulkanGraphicCommandBuffer commandBuffer ) {
            VulkanCommand< VulkanGraphicCommandBuffer > command;
            while ( ( command  = commands.poll() ) != null ) {
                command.execute( commandBuffer );
            }
        }
        
        public void clear() {
            commands.clear();
        }
        
        void add( VulkanCommand< VulkanGraphicCommandBuffer > command ) {
            commands.addFirst( command );
        }
    }
}
