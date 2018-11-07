/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.shader.ShaderProgram;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanShaderType;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanDataBuffer;
import org.joml.Matrix4fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.firststory.firstoracle.vulkan.physicaldevice.rendering.VulkanShader.SHADER_FILE_PATH_FRAGMENT;
import static com.firststory.firstoracle.vulkan.physicaldevice.rendering.VulkanShader.SHADER_FILE_PATH_VERTEX_3D;

/**
 * @author n1t4chi
 */
public class VulkanShaderProgram implements ShaderProgram {
    
    private static final int OFFSET_POSITION = 0;
    private static final int OFFSET_SCALE = 4;
    private static final int OFFSET_ROTATION = 8;
    private static final int OFFSET_COLOUR = 12;
    private static final int OFFSET_ALPHA_CHANNEL = 16;
    
    private static final int INPUT_SIZE = FirstOracleConstants.FLOAT_SIZE_VEC_4F * 5 + FirstOracleConstants.BYTE_SIZE_INT;
    private static final int UNIFORM_SIZE = FirstOracleConstants.FLOAT_SIZE_MATRIX_4F;
    private static final int UNIFORM_DATA_SIZE = FirstOracleConstants.BYTE_SIZE_FLOAT * UNIFORM_SIZE;
    private final VulkanDeviceAllocator allocator;
    private final float[] inBufferData = new float[ INPUT_SIZE ];
    private final VulkanShader fragmentShader;
    private final VulkanBufferProvider bufferLoader;
    private final VulkanShader vertexShader;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final float[] uniformBufferData = new float[ UNIFORM_SIZE ];
    
    public VulkanShaderProgram(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device,
        VulkanBufferProvider bufferLoader
    ) {
        this.allocator = allocator;
        this.vertexShader = new VulkanShader( device, SHADER_FILE_PATH_VERTEX_3D, VulkanShaderType.VERTEX );
        this.fragmentShader = new VulkanShader( device, SHADER_FILE_PATH_FRAGMENT, VulkanShaderType.FRAGMENT );
        this.bufferLoader = bufferLoader;
        Arrays.fill( uniformBufferData, 1 );
    }
    
    @Override
    public void useProgram() {}
    
    @Override
    public void compile() throws IOException {
        vertexShader.compile();
        fragmentShader.compile();
        shaderStages.add( vertexShader.getStageCreateInfo() );
        shaderStages.add( fragmentShader.getStageCreateInfo() );
    }
    
    @Override
    public void dispose() {
        allocator.deregisterShaderProgram( this );
    }
    
    public void disposeUnsafe() {
        vertexShader.dispose();
        fragmentShader.dispose();
        shaderStages.clear();
    }
    
    public List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
    
    VulkanDataBuffer bindUniformData( Matrix4fc camera ) {
        synchronized ( uniformBufferData ) {
            camera.get( uniformBufferData );
            return bufferLoader.createUniformBuffer( uniformBufferData );
        }
    }
    
    float[] getInputData() {
        return inBufferData.clone();
    }
    
    void bindPosition( Position position ) {
        putInputData( OFFSET_POSITION, position.x(), position.y(), position.z() );
    }
    
    void bindScale( Scale scale ) {
        putInputData( OFFSET_SCALE, scale.x(), scale.y(), scale.z() );
    }
    
    void bindRotation( Rotation rotation ) {
        putInputData( OFFSET_ROTATION, rotation.ox(), rotation.oy(), rotation.oz() );
    }
    
    void bindOverlayColour( Colour vector ) {
        putInputData( OFFSET_COLOUR, vector.red(), vector.green(), vector.blue(), vector.alpha() );
    }
    
    void bindMaxAlphaChannel( float value ) {
        putInputData( OFFSET_ALPHA_CHANNEL, value );
    }
    
    VkDescriptorBufferInfo createDescriptorBufferInfo( VulkanDataBuffer uniformBuffer ) {
        return VkDescriptorBufferInfo.calloc()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( uniformBuffer.getMemoryOffset() )
            .range( UNIFORM_DATA_SIZE );
    }
    
    private void putInputData( int offset, float... data ) {
        System.arraycopy( data, 0, inBufferData, offset, data.length );
    }
}
