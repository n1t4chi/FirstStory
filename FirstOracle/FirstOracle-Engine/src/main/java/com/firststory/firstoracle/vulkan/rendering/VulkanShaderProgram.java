/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.rendering;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Colour;
import com.firststory.firstoracle.data.Position;
import com.firststory.firstoracle.data.Rotation;
import com.firststory.firstoracle.data.Scale;
import com.firststory.firstoracle.shader.ShaderProgram;
import com.firststory.firstoracle.vulkan.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.VulkanShaderType;
import com.firststory.firstoracle.vulkan.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.buffer.VulkanDataBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.io.IOException;
import java.util.*;

import static com.firststory.firstoracle.vulkan.rendering.VulkanShader.SHADER_FILE_PATH_FRAGMENT;
import static com.firststory.firstoracle.vulkan.rendering.VulkanShader.SHADER_FILE_PATH_VERTEX_3D;

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
    private final float[] inBufferData = new float[ INPUT_SIZE ];
    private final VulkanShader fragmentShader;
    private final VulkanBufferProvider bufferLoader;
    private final VulkanShader vertexShader;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final VulkanPhysicalDevice device;
    private final List< VulkanDataBuffer > uniformBuffers = new ArrayList<>( 50 );
    private final Deque< VulkanDataBuffer > uniformBuffersIterator  = new LinkedList<>();
    private final float[] uniformBufferData = new float[ UNIFORM_SIZE ];
    private Matrix4fc camera = null;
    private boolean cameraChanged = true;
    
    public VulkanShaderProgram( VulkanPhysicalDevice device, VulkanBufferProvider bufferLoader ) {
        this.device = device;
        this.vertexShader = new VulkanShader( device, SHADER_FILE_PATH_VERTEX_3D, VulkanShaderType.VERTEX );
        this.fragmentShader = new VulkanShader( device, SHADER_FILE_PATH_FRAGMENT, VulkanShaderType.FRAGMENT );
        this.bufferLoader = bufferLoader;
        clearValues();
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
        uniformBuffers.forEach( VulkanDataBuffer::close );
        uniformBuffers.clear();
        uniformBuffersIterator.clear();
        vertexShader.dispose();
        fragmentShader.dispose();
        shaderStages.clear();
    }
    
    public List< VkPipelineShaderStageCreateInfo > getShaderStages() {
        return shaderStages;
    }
    
    void bindCamera( Matrix4fc camera ) {
        putUniformData( camera );
    }
    
    float[] getInputData() {
        return inBufferData.clone();
    }
    
    void bindPosition( Vector3fc vector ) {
        putInputData( OFFSET_POSITION, vector.x(), vector.y(), vector.z() );
    }
    
    void bindPosition( Vector2fc vector ) {
        putInputData( OFFSET_POSITION, vector.x(), vector.y(), 0 );
    }
    
    void bindPosition( Position position ) {
        putInputData( OFFSET_POSITION, position.x(), position.y(), position.z() );
    }
    
    void bindScale( Vector2fc vector ) {
        putInputData( OFFSET_SCALE, vector.x(), vector.y(), 0 );
    }
    
    void bindScale( Vector3fc vector ) {
        putInputData( OFFSET_SCALE, vector.x(), vector.y(), vector.z() );
    }
    
    void bindScale( Scale scale ) {
        putInputData( OFFSET_SCALE, scale.x(), scale.y(), scale.z() );
    }
    
    void bindRotation( float rotation ) {
        putInputData( OFFSET_ROTATION, 0 , 0, rotation );
    }
    
    void bindRotation( Vector3fc vector ) {
        putInputData( OFFSET_ROTATION, vector.x(), vector.y(), vector.z() );
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
    
    void resetUniformData() {
        uniformBuffersIterator.clear();
        uniformBuffersIterator.addAll( uniformBuffers );
    }
    
    VkDescriptorBufferInfo bindUniformData() {
        var buffer = uniformBuffersIterator.poll();
        if( buffer == null ) {
            buffer = bufferLoader.createUniformBuffer( uniformBufferData );
            uniformBuffers.add( buffer );
        } else {
            buffer.load( uniformBufferData );
        }
        return createDescriptorBufferInfo( buffer );
    }
    
    Matrix4fc getCamera() {
        cameraChanged = false;
        return camera;
    }
    
    boolean didCameraChange() {
        return cameraChanged;
    }
    
    void putUniformData( Matrix4fc camera ) {
        this.camera = new Matrix4f( camera );
        cameraChanged = true;
        camera.get( uniformBufferData );
    }
    
    void clearValues() {
        Arrays.fill( uniformBufferData, 1 );
    }
    
    private void putInputData( int offset, float... data ) {
        System.arraycopy( data, 0, inBufferData, offset, data.length );
    }
    
    private VkDescriptorBufferInfo createDescriptorBufferInfo( VulkanDataBuffer uniformBuffer ) {
        return VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( uniformBuffer.getMemoryOffset() )
            .range( UNIFORM_DATA_SIZE );
    }
}
