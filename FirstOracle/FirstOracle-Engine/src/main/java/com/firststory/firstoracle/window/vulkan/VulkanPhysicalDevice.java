/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.TextureBuffer;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.window.vulkan.exceptions.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class VulkanPhysicalDevice implements Comparable< VulkanPhysicalDevice > {
    
    static final float[] POSITION_1 = new float[]{
        /*3*/ /*pos*/ 0.0f, 0.0f,
        /*1*/ /*pos*/ 0.5f, 0.5f,
        /*2*/ /*pos*/ 0.5f, 0.0f,
    };
    static final float[] COLOUR_1 = new float[]{
        /*1*/  /*col*/ 1.0f, 0.0f, 1.0f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.0f,
        /*3*/ /*col*/ 0.0f, 1.0f, 1.0f
    };
    static final float[] UVMAP_1 = new float[]{
        /*3*/ /*pos*/ 0f, 0f,
        /*2*/ /*pos*/ 1f, 1f,
        /*1*/ /*pos*/ 1f, 0f,
    };
    static final float[] POSITION_2 = new float[]{
        /*3*/ /*pos*/ 0.0f, 0.0f,
        /*2*/ /*pos*/ 0.0f, 0.5f,
        /*1*/ /*pos*/ 0.5f, 0.5f,
    };
    static final float[] COLOUR_2 = new float[]{
        /*1*/  /*col*/ 0.5f, 0.5f, 0.5f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.5f,
        /*3*/ /*col*/ 0.5f, 1.0f, 1.0f
    };
    static final float[] UVMAP_2 = new float[]{
        /*3*/ /*pos*/ 0f, 0f,
        /*2*/ /*pos*/ 0f, 1f,
        /*1*/ /*pos*/ 1f, 1f,
    };
    private static final int FLOAT_SIZE = 4;
    private static final int MATRIX_SIZE = 4 * 4;
    private static final int VEC_SIZE = 2;
    private static final int UNIFORM_SIZE = MATRIX_SIZE + VEC_SIZE * 2;
    private static final int UNIFORM_DATA_SIZE = FLOAT_SIZE * UNIFORM_SIZE;
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanPhysicalDevice.class );
    private static final Set< String > requiredExtensions = new HashSet<>();
    private static final String VERTEX_SHADER_FILE_PATH = "resources/First Oracle/vert.spv";
    private static final String FRAGMENT_SHADER_FILE_PATH = "resources/First Oracle/frag.spv";
    
    static {
        requiredExtensions.add( KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME );
    }
    
    private final VkPhysicalDevice physicalDevice;
    private final VkPhysicalDeviceFeatures features;
    private final VkPhysicalDeviceProperties properties;
    private final VKCapabilitiesInstance capabilities;
    private final List< VulkanQueueFamily > availableQueueFamilies;
    private final Set< VulkanQueueFamily > usedQueueFamilies = new HashSet<>();
    private final VulkanQueueFamily graphicFamily;
    private final VulkanQueueFamily presentationFamily;
    private final VulkanQueueFamily transferFamily;
    private final VkDevice logicalDevice;
    private final VulkanWindowSurface windowSurface;
    private final List< VkExtensionProperties > availableExtensionProperties;
    private final VulkanSwapChain swapChain;
    private final List< VkPipelineShaderStageCreateInfo > shaderStages = new ArrayList<>();
    private final VulkanGraphicPipeline graphicPipeline;
    private final Map< Integer, VulkanFrameBuffer > frameBuffers = new HashMap<>();
    private final VulkanGraphicCommandPool graphicCommandPool;
    private final VulkanTransferCommandPool vertexDataTransferCommandPool;
    private final VulkanTransferCommandPool textureTransferCommandPool;
    private final Set< VulkanCommandPool > commandPools = new HashSet<>();
    private final VulkanSemaphore renderFinishedSemaphore;
    private final VulkanSemaphore imageAvailableSemaphore;
    private final VulkanDataBufferProvider bufferLoader;
    private final VkPhysicalDeviceMemoryProperties memoryProperties;
    private final Map< Integer, VulkanMemoryType > memoryTypes = new HashMap<>();
    private final Map< Integer, VulkanMemoryHeap > memoryHeaps = new HashMap<>();
    private final VulkanShaderProgram vertexShader;
    private final VulkanShaderProgram fragmentShader;
    private final VulkanAddress descriptorSetLayout;
    private final VulkanDataBuffer< float[] > positionBuffer1;
    private final VulkanDataBuffer< float[] > positionBuffer2;
    private final VulkanDataBuffer< float[] > colourBuffer1;
    private final VulkanDataBuffer< float[] > colourBuffer2;
    private final VulkanStageableDataBuffer< float[] > uvBuffer1;
    private final VulkanStageableDataBuffer< float[] > uvBuffer2;
    private final VulkanUniformBuffer uniformBuffer;
    
    private final float[] uniformBufferData = new float[UNIFORM_SIZE];
    private final Matrix4f matrix = new Matrix4f();
    private final Vector2f trans = new Vector2f( 0.01f, 0.01f );
    private final Vector2f scale = new Vector2f( 1, 1 );
    private final VulkanAddress descriptorPool;
    private final VulkanAddress descriptorSet;
    private final VulkanTextureLoader textureLoader;
    private final VulkanAddress textureSampler;
    private final Texture texture;
    private final TextureBuffer<VulkanTextureData> textureData;
    private VulkanDataBuffer textureBuffer;
    private float rotate = 0;
    
    VulkanPhysicalDevice(
        long deviceAddress, VkInstance instance, PointerBuffer validationLayerNamesBuffer, VulkanWindowSurface surface
    ) throws CannotCreateVulkanPhysicalDeviceException
    {
        windowSurface = surface;
        physicalDevice = new VkPhysicalDevice( deviceAddress, instance );
        capabilities = physicalDevice.getCapabilities();
        features = createDeviceFeatures();
        properties = createDeviceProperties();
        
        assertDeviceProperties();
        
        availableExtensionProperties = createExtensionProperties();
        assertRequiredExtensions();
        
        availableQueueFamilies = getAvailableQueueFamilies();
        graphicFamily = selectGraphicFamily();
        presentationFamily = selectPresentationFamily();
        transferFamily = selectTransferFamily();
        
        addUsedQueueFamily( graphicFamily, presentationFamily, transferFamily );
        logicalDevice = createLogicalDevice( validationLayerNamesBuffer );
        
        memoryProperties = createPhysicalDeviceMemoryProperties();
        fillMemoryTypes();
        fillMemoryHeaps();
        
        imageAvailableSemaphore = new VulkanSemaphore( this );
        renderFinishedSemaphore = new VulkanSemaphore( this );
        
        vertexShader = new VulkanShaderProgram( this, VERTEX_SHADER_FILE_PATH, ShaderType.VERTEX );
        fragmentShader = new VulkanShaderProgram( this, FRAGMENT_SHADER_FILE_PATH, ShaderType.FRAGMENT );
        
        try {
            vertexShader.compile();
            fragmentShader.compile();
        } catch ( IOException ex ) {
            throw new CannotCreateVulkanPhysicalDeviceException( this, ex );
        }
        shaderStages.add( vertexShader.getStageCreateInfo() );
        shaderStages.add( fragmentShader.getStageCreateInfo() );
        
        swapChain = new VulkanSwapChain( this );
        
        this.descriptorSetLayout = createDescriptorSetLayout();
        
        graphicPipeline = new VulkanGraphicPipeline( this );
        graphicCommandPool = new VulkanGraphicCommandPool( this,
            graphicFamily,
            swapChain,
            graphicPipeline,
            imageAvailableSemaphore,
            renderFinishedSemaphore
        );
        vertexDataTransferCommandPool = new VulkanTransferCommandPool( this, transferFamily );
        textureTransferCommandPool = new VulkanTransferCommandPool( this, graphicFamily );
        commandPools.add( graphicCommandPool );
        commandPools.add( vertexDataTransferCommandPool );
        commandPools.add( textureTransferCommandPool );
        
        bufferLoader = new VulkanDataBufferProvider( this );
    
        descriptorPool = createDescriptorPool();
        descriptorSet = createDescriptorSet();
        
        textureSampler = createTextureSampler();
        textureLoader = new VulkanTextureLoader( this, bufferLoader, textureSampler, descriptorSet );
        
        uniformBuffer = bufferLoader.createUniformBuffer( UNIFORM_SIZE, FLOAT_SIZE );
        
        texture = createTexture();
        textureData = texture.getBuffer( textureLoader );
        texture.bind( textureLoader );
        
        updateDesciptorSetsOnDevice();
        updateRenderingContext();
    
        positionBuffer1 = bufferLoader.createFloatBuffer();
        positionBuffer1.load( POSITION_1 );
        positionBuffer1.bind();
        positionBuffer2 = bufferLoader.createFloatBuffer();
        positionBuffer2.load( POSITION_2 );
        positionBuffer2.bind();
        
        colourBuffer1 = bufferLoader.createFloatBuffer();
        colourBuffer1.load( COLOUR_1 );
        colourBuffer1.bind();
        colourBuffer2 = bufferLoader.createFloatBuffer();
        colourBuffer2.load( COLOUR_2 );
        colourBuffer2.bind();
    
        uvBuffer1 = bufferLoader.createFloatBuffer();
        uvBuffer1.load( UVMAP_1 );
        uvBuffer1.bind();
        uvBuffer2 = bufferLoader.createFloatBuffer();
        uvBuffer2.load( UVMAP_2 );
        uvBuffer2.bind();
        
        logger.finer(
            "finished creating " + this + "[" + "\nscore: " + getScore() + "\ngraphic family: " + graphicFamily +
                "\npresentation family: " + presentationFamily + "\ntransfer family: " + transferFamily + "\nqueues: " +
                availableQueueFamilies.size() + "\nextensions: " + availableExtensionProperties.size() +
                "\nmemory types: " + memoryTypesToString() + "]" );
    }
    
    @Override
    public int compareTo( VulkanPhysicalDevice o ) {
        return Long.compare( getScore(), o.getScore() );
    }
    
    @Override
    public String toString() {
        return "VulkanPhysicalDevice@" + hashCode() + "[name:" + properties.deviceNameString() + "]";
    }
    
    public String memoryTypesToString() {
        StringBuilder memoryTypesString = new StringBuilder();
        memoryTypesString.append( "{" );
        memoryTypes.values()
            .forEach( memoryType -> memoryTypesString.append( " { heapIndex:" )
                .append( memoryType.heapIndex() )
                .append( ", flags:" )
                .append( memoryType.propertyFlags() )
                .append( " }," ) );
        memoryTypesString.deleteCharAt( memoryTypesString.length() - 1 );
        memoryTypesString.append( " }" );
        return memoryTypesString.toString();
    }
    
    VulkanGraphicCommandPool getGraphicCommandPool() {
        return graphicCommandPool;
    }
    
    VulkanAddress getDescriptorSetLayout() {
        return descriptorSetLayout;
    }
    
    private VulkanAddress createTextureSampler() {
        VkSamplerCreateInfo createInfo = VkSamplerCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO )
            .magFilter( VK10.VK_FILTER_LINEAR )
            .minFilter( VK10.VK_FILTER_LINEAR )
            .addressModeU( VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT )
            .addressModeV( VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT )
            .addressModeW( VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT )
            .anisotropyEnable( true )
            .maxAnisotropy( 16 )
            .borderColor( VK10.VK_BORDER_COLOR_INT_OPAQUE_BLACK )
            .unnormalizedCoordinates( false )
            .compareEnable( false )
            .compareOp( VK10.VK_COMPARE_OP_ALWAYS )
            .mipmapMode( VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR )
            .mipLodBias( 0f )
            .minLod( 0f )
            .maxLod( 0f )
            ;
        
        return VulkanHelper.createAddress(
            address -> VK10.vkCreateSampler( logicalDevice, createInfo, null, address ),
            resultCode -> new CannotCreateVulkanTextureSamplerException( this, resultCode )
        );
    }
    
    
    private Texture createTexture() {
        Texture texture;
        try {
            texture = new Texture( "resources/First Oracle/texture2D.png" );
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
        texture.load( textureLoader );
        texture.bind( textureLoader );
        return texture;
    }
    
    Map< Integer, VulkanMemoryType > getMemoryTypes() {
        return memoryTypes;
    }
    
    VulkanTransferCommandPool getVertexDataTransferCommandPool() {
        return vertexDataTransferCommandPool;
    }
    
    VulkanTransferCommandPool getTextureTransferCommandPool() {
        return textureTransferCommandPool;
    }
    
    boolean isSingleCommandPoolUsed() {
        return commandPools.size() == 1;
    }
    
    VulkanMemoryType selectMemoryType( int desiredType, int... desiredFlags ) {
        for ( VulkanMemoryType memoryType : memoryTypes.values() ) {
            if( checkMemoryTypeFlags( memoryType.getIndex(), memoryType.propertyFlags(), desiredType, desiredFlags ) ) {
                return memoryType;
            }
        }
        throw new CannotSelectSuitableMemoryTypeException( this, desiredType, desiredFlags );
    }
    
    VulkanDataBufferProvider getBufferLoader() {
        return bufferLoader;
    }
    
    void updateRenderingContext() {
        presentationFamily.waitForQueue();
        swapChain.update( windowSurface );
        graphicPipeline.update( swapChain, shaderStages );
        refreshFrameBuffers( frameBuffers );
        refreshCommandBuffers();
    }
    
    void testRender() {
        matrix.identity().scale( ThreadLocalRandom.current().nextFloat() / 10 + 1 ).rotateZ( rotate = rotate + 0.001f );
        matrix.get( uniformBufferData, 0 );
        uniformBufferData[16] = trans.x = trans.x * 1.00001f;
        uniformBufferData[17] = trans.y = trans.y * 1.00001f;
        uniformBufferData[18] = scale.x = scale.x * 1.00001f;
        uniformBufferData[19] = scale.y = scale.y * 1.00001f;
        uniformBuffer.load( uniformBufferData );
        
        graphicCommandPool.executeQueue( commandBuffer -> {
            commandBuffer.bindDescriptorSets( descriptorSet );
            commandBuffer.drawVertices( positionBuffer1, colourBuffer1, uvBuffer1 );
            commandBuffer.drawVertices( positionBuffer2, colourBuffer2, uvBuffer2 );
        } );
        
        if ( VulkanFramework.validationLayersAreEnabled() ) {
            presentationFamily.waitForQueue();
        }
        
    }
    
    void dispose() {
        presentationFamily.waitForQueue();
        disposeFrameBuffers( frameBuffers );
        commandPools.forEach( VulkanCommandPool::dispose );
        graphicPipeline.dispose();
        VK10.vkDestroyDescriptorSetLayout( logicalDevice, descriptorSetLayout.getValue(), null );
        VK10.vkDestroyDescriptorPool( logicalDevice, descriptorPool.getValue(), null );
        uniformBuffer.close();
        swapChain.dispose();
        bufferLoader.close();
    
        texture.close();
        VK10.vkDestroySampler( logicalDevice, textureSampler.getValue(), null );
        
        vertexShader.dispose();
        fragmentShader.dispose();
        imageAvailableSemaphore.dispose();
        renderFinishedSemaphore.dispose();
        VK10.vkDestroyDevice( logicalDevice, null );
    }
    
    VkDevice getLogicalDevice() {
        return logicalDevice;
    }
    
    boolean isSingleQueueFamilyUsed() {
        return usedQueueFamilies.size() == 1;
    }
    
    VkPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
    
    long getScore() {
        return ( ( properties.limits().maxMemoryAllocationCount() + properties.limits().maxImageDimension2D() +
                    ( availableExtensionProperties.size() + availableQueueFamilies.size() ) * 1000
            ) * typeMultiplier()
        );
    }
    
    IntBuffer createQueueFamilyIndicesBuffer() {
        IntBuffer buffer = MemoryUtil.memAllocInt( usedQueueFamilies.size() );
        usedQueueFamilies.forEach( family -> buffer.put( family.getIndex() ) );
        buffer.flip();
        return buffer;
    }
    
    int aquireNextImageIndex() {
        try {
            return tryToAquireNextImageIndex();
        } catch ( VulkanNextImageIndexException ex1 ) {
            logger.log( Level.WARNING, "Exception during aquiring next image index. Updating rendering context.", ex1 );
            try {
                updateRenderingContext();
                int imageIndex = tryToAquireNextImageIndex();
                logger.log( Level.FINEST, "Aquired image index: " + imageIndex + " after context update." );
                return imageIndex;
            } catch ( Exception ex2 ) {
                throw new VulkanNextImageIndexException( this, ex1, ex2 );
            }
        }
    }
    
    private void updateDesciptorSetsOnDevice() {
        VK10.vkUpdateDescriptorSets( logicalDevice,
            VkWriteDescriptorSet.create( 2 )
                .put( 0, createDescriptorWrite( 0,
                    VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER,
                    VkDescriptorBufferInfo.create( 1 ).put( 0, createBufferInfo() ),
                    null
                ) )
                .put( 1, createDescriptorWrite( 1,
                    VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER,
                    null,
                    VkDescriptorImageInfo.create( 1 ).put( 0, createImageInfo() )
                ) )
            ,
            null
        );
    }
    
    private VkDescriptorImageInfo createImageInfo() {
        return VkDescriptorImageInfo.create()
            .imageLayout( VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL )
            .imageView( textureData.getContext().getImageView().getAddress().getValue() )
            .sampler( textureSampler.getValue() )
        ;
    }
    
    private VkDescriptorBufferInfo createBufferInfo() {
        return VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( 0 )
            .range( UNIFORM_DATA_SIZE );
    }
    
    private VkWriteDescriptorSet createDescriptorWrite(
        int bindingIndex, int type, VkDescriptorBufferInfo.Buffer bufferInfos, VkDescriptorImageInfo.Buffer imageInfos
    ) {
        return VkWriteDescriptorSet.create()
            .sType( VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET )
            .dstSet( descriptorSet.getValue() )
            .dstBinding( bindingIndex )
            .descriptorType( type )
            .dstArrayElement( 0 )
            .pBufferInfo( bufferInfos )
            .pImageInfo( imageInfos )
            .pTexelBufferView( null );
    }
    
    private VulkanAddress createDescriptorSet() {
        VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO )
            .descriptorPool( descriptorPool.getValue() )
            .pSetLayouts( MemoryUtil.memAllocLong( 1 ).put( 0, descriptorSetLayout.getValue() ) )
        ;
        
        return VulkanHelper.createAddress(
            address -> VK10.vkAllocateDescriptorSets( logicalDevice, allocateInfo, address ),
            resultCode -> new CannotCreateVulkanDescriptorSetException( this, resultCode )
        );
    }
    
    private VulkanAddress createDescriptorPool() {
        VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO )
            .pPoolSizes( VkDescriptorPoolSize.create( 2 )
                .put( 0, createPoolSize( VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER ) )
                .put( 1, createPoolSize( VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER ) )
            )
            .maxSets( 1 );
        return VulkanHelper.createAddress(
            address -> VK10.vkCreateDescriptorPool( logicalDevice, createInfo, null, address ),
            resultCode -> new CannotCreateVulkanDescriptorPoolException( this, resultCode )
        );
    }
    
    private VkDescriptorPoolSize createPoolSize( int type ) {
        return VkDescriptorPoolSize.create()
            .type( type )
            .descriptorCount( 1 );
    }
    
    private VulkanAddress createDescriptorSetLayout() {
        VkDescriptorSetLayoutCreateInfo createInfo = VkDescriptorSetLayoutCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO )
            .pBindings( VkDescriptorSetLayoutBinding.calloc( 2 )
                .put( 0, createLayoutBinding(
                    0, VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, VK10.VK_SHADER_STAGE_VERTEX_BIT ) )
                .put( 1, createLayoutBinding(
                    1, VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, VK10.VK_SHADER_STAGE_FRAGMENT_BIT ) )
            )
        ;
        
        return VulkanHelper.createAddress( address ->
                VK10.vkCreateDescriptorSetLayout( logicalDevice, createInfo, null, address ),
            resultCode -> new CannotCreateVulkanDescriptorSetLayoutException( this, resultCode )
        );
    }
    
    private VkDescriptorSetLayoutBinding createLayoutBinding( int index, int type, int stageFlag ) {
        return VkDescriptorSetLayoutBinding.create()
            .binding( index )
            .descriptorType( type )
            .descriptorCount( 1 )
            .stageFlags( stageFlag )
            .pImmutableSamplers( null );
    }
    
    private boolean checkMemoryTypeFlags( int index, int memoryFlags, int desiredType, int... desiredFlags ) {
        if ( ( desiredType & ( 1 << index ) ) == 0 ) {
            return false;
        }
        return ( memoryFlags & VulkanHelper.flagsToInt( desiredFlags ) ) != 0;
    }
    
    private void fillMemoryHeaps() {
        VulkanHelper.iterate( memoryProperties.memoryHeaps(),
            ( index, memoryHeap ) -> memoryHeaps.put( index, new VulkanMemoryHeap( index, memoryHeap ) )
        );
    }
    
    private void fillMemoryTypes() {
        VulkanHelper.iterate( memoryProperties.memoryTypes(),
            ( index, memoryType ) -> memoryTypes.put( index, new VulkanMemoryType( index, memoryType ) )
        );
    }
    
    private VkPhysicalDeviceMemoryProperties createPhysicalDeviceMemoryProperties() {
        VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.create();
        VK10.vkGetPhysicalDeviceMemoryProperties( physicalDevice, memoryProperties );
        return memoryProperties;
    }
    
    private void refreshCommandBuffers() {
        commandPools.forEach( pool -> pool.refreshCommandBuffers( frameBuffers ) );
    }
    
    private int tryToAquireNextImageIndex() {
        int[] imageIndex = new int[1];
        int result = KHRSwapchain.vkAcquireNextImageKHR( logicalDevice,
            swapChain.getAddress().getValue(),
            Long.MAX_VALUE,
            imageAvailableSemaphore.getAddress().getValue(),
            VK10.VK_NULL_HANDLE,
            imageIndex
        );
        
        switch ( result ) {
            case KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR:
                throw new OutOfDateSwapChainException( this, imageIndex[0] );
            case KHRSwapchain.VK_SUBOPTIMAL_KHR:
                logger.warning( "Swap chain images no longer match surface. Update might be required at later time" );
            case VK10.VK_SUCCESS:
                break;
            default:
                throw new CannotAquireNextImageIndexException( this, imageIndex[0], result );
        }
        return imageIndex[0];
    }
    
    private void refreshFrameBuffers( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        disposeFrameBuffers( frameBuffers );
        for ( Map.Entry< Integer, VulkanImageView > entry : swapChain.getImageViews().entrySet() ) {
            frameBuffers.put( entry.getKey(),
                new VulkanFrameBuffer( this, entry.getValue(), graphicPipeline, swapChain )
            );
        }
    }
    
    private void disposeFrameBuffers( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        frameBuffers.forEach( ( integer, vulkanFrameBuffer ) -> vulkanFrameBuffer.dispose() );
        frameBuffers.clear();
    }
    
    private void addUsedQueueFamily( VulkanQueueFamily... family ) {
        if ( family != null ) {
            usedQueueFamilies.addAll( Arrays.asList( family ) );
        }
    }
    
    private void assertDeviceProperties() {
        if ( features == null ) {
            throw new CannotCreateVulkanPhysicalDeviceException( this, "Null features" );
        }
        if ( capabilities == null ) {
            throw new CannotCreateVulkanPhysicalDeviceException( this, "Null capabilities" );
        }
        if ( properties == null ) {
            throw new CannotCreateVulkanPhysicalDeviceException( this, "Null properties" );
        }
        if ( properties.limits().maxMemoryAllocationCount() <= 32 ) {
            throw new DeviceHasNotEnoughMemoryException( this, properties.limits().maxMemoryAllocationCount() );
        }
        if ( !features.samplerAnisotropy() ) {
            throw new CannotCreateVulkanPhysicalDeviceException( this, "Device does not support sampler anisotropy." );
        }
    }
    
    private void assertRequiredExtensions() {
        Set< String > required = new HashSet<>( requiredExtensions );
        required.removeAll( availableExtensionProperties.stream()
            .map( VkExtensionProperties::extensionNameString )
            .collect( Collectors.toSet() ) );
        
        if ( !required.isEmpty() ) {
            throw new DeviceDoesNotHaveAllRequiredExtensionsException( this, required );
        }
    }
    
    private List< VkExtensionProperties > createExtensionProperties() {
        int[] extensionCount = new int[1];
        VK10.vkEnumerateDeviceExtensionProperties( physicalDevice, ( ByteBuffer ) null, extensionCount, null );
        VkExtensionProperties.Buffer extensionPropertiesBuffer = VkExtensionProperties.create( extensionCount[0] );
        VK10.vkEnumerateDeviceExtensionProperties( physicalDevice,
            ( ByteBuffer ) null,
            extensionCount,
            extensionPropertiesBuffer
        );
        List< VkExtensionProperties > extensionProperties = new ArrayList<>( extensionCount[0] );
        extensionPropertiesBuffer.forEach( extensionProperties::add );
        return extensionProperties;
    }
    
    private VkDeviceCreateInfo createDeviceCreateInfo() {
        return VkDeviceCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .pEnabledFeatures( features )
            .pQueueCreateInfos( createQueueFamilyBuffer() )
            .ppEnabledExtensionNames( createEnabledExtensionNamesBuffer() );
    }
    
    private VkDeviceQueueCreateInfo.Buffer createQueueFamilyBuffer() {
        VkDeviceQueueCreateInfo.Buffer buffer = createVkDeviceQueueCreateInfoBuffer( usedQueueFamilies.size() );
        usedQueueFamilies.forEach( family -> {
            VkDeviceQueueCreateInfo queueInfo = VkDeviceQueueCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO )
                .queueFamilyIndex( family.getIndex() )
                .pQueuePriorities( MemoryStack.stackFloats( 1.0f ) );
            buffer.put( queueInfo );
        } );
        buffer.flip();
        return buffer;
    }
    
    private VkDeviceQueueCreateInfo.Buffer createVkDeviceQueueCreateInfoBuffer( int count ) {
        return new VkDeviceQueueCreateInfo.Buffer( BufferUtils.createByteBuffer(
            count * VkDeviceQueueCreateInfo.SIZEOF ) );
    }
    
    private VkPhysicalDeviceFeatures createDeviceFeatures() {
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.create()
            .samplerAnisotropy( true )
        ;
        VK10.vkGetPhysicalDeviceFeatures( physicalDevice, features );
        return features;
    }
    
    private VkPhysicalDeviceProperties createDeviceProperties() {
        VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.create();
        VK10.vkGetPhysicalDeviceProperties( physicalDevice, properties );
        return properties;
    }
    
    private VkDevice createLogicalDevice( PointerBuffer validationLayerNamesBuffer ) {
        VkDeviceCreateInfo createInfo = createDeviceCreateInfo();
        if ( VulkanFramework.validationLayersAreEnabled() ) {
            createInfo.ppEnabledLayerNames( validationLayerNamesBuffer );
        }
        PointerBuffer devicePointer = MemoryUtil.memAllocPointer( 1 );
        VulkanHelper.assertCallOrThrow( () -> VK10.vkCreateDevice( physicalDevice, createInfo, null, devicePointer ),
            resultCode -> new CannotCreateVulkanLogicDeviceException( this, resultCode )
        );
        return new VkDevice( devicePointer.get(), physicalDevice, createInfo );
    }
    
    private PointerBuffer createEnabledExtensionNamesBuffer() {
        PointerBuffer extensionsBuffer = MemoryUtil.memAllocPointer( requiredExtensions.size() );
        requiredExtensions.forEach( ex -> {
            extensionsBuffer.put( MemoryUtil.memAddress( MemoryStack.stackUTF8( ex ) ) );
        } );
        extensionsBuffer.flip();
        return extensionsBuffer;
    }
    
    private VulkanQueueFamily selectTransferFamily() {
        return selectSuitableFamily( family -> family.isFlagSet( VK10.VK_QUEUE_TRANSFER_BIT ) &&
                family != graphicFamily,
            ( first, second ) -> VulkanQueueFamily.compare( first, second ),
            () -> {
                logger.warning( "Selecting graphic family for transfer family." );
                return graphicFamily;
            }
        );
    }
    
    private VulkanQueueFamily selectGraphicFamily() {
        return selectSuitableFamily( family -> family.isFlagSet( VK10.VK_QUEUE_GRAPHICS_BIT ),
            ( first, second ) -> VulkanQueueFamily.compare( first, second ),
            "graphic"
        );
    }
    
    private VulkanQueueFamily selectPresentationFamily() {
        return selectSuitableFamily( family -> {
            int[] supports = new int[1];
            return KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR( physicalDevice,
                family.getIndex(),
                windowSurface.getAddress().getValue(),
                supports
            ) == VK10.VK_SUCCESS;
        }, ( first, second ) -> VulkanQueueFamily.compare( first, second ), "presentation" );
    }
    
    private VulkanQueueFamily selectSuitableFamily(
        Predicate< VulkanQueueFamily > familyChecker,
        Comparator< VulkanQueueFamily > familyComparator,
        String familyName
    )
    {
        return selectSuitableFamily( familyChecker, familyComparator, () -> {
            logger.warning( "Could not select " + familyName + " family!" );
            throw new CannotSelectVulkanQueueFamilyException( this, familyName );
        } );
    }
    
    private VulkanQueueFamily selectSuitableFamily(
        Predicate< VulkanQueueFamily > familyChecker,
        Comparator< VulkanQueueFamily > familyComparator,
        Supplier< VulkanQueueFamily > noSelectedFamilyAction
    )
    {
        VulkanQueueFamily selectedFamily = null;
        for ( VulkanQueueFamily family : availableQueueFamilies ) {
            if ( familyChecker.test( family ) && familyComparator.compare( selectedFamily, family ) < 0 ) {
                selectedFamily = family;
            }
        }
        if ( selectedFamily == null ) {
            return noSelectedFamilyAction.get();
        }
        return selectedFamily;
    }
    
    private List< VulkanQueueFamily > getAvailableQueueFamilies() {
        int[] queueFamilyCount = new int[1];
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, null );
        VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = VkQueueFamilyProperties.create( queueFamilyCount[0] );
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, queueFamilyPropertiesBuffer );
        List< VulkanQueueFamily > queueFamilyProperties = new ArrayList<>( queueFamilyCount[0] );
        
        VulkanHelper.iterate( queueFamilyPropertiesBuffer, ( index, properties ) -> {
            VulkanQueueFamily family = new VulkanQueueFamily( this, properties, index );
            queueFamilyProperties.add( family );
            logger.finer( this + " -> " + family );
        } );
        return queueFamilyProperties;
    }
    
    private long typeMultiplier() {
        switch ( properties.deviceType() ) {
            case VK10.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU:
                return 6;
            case VK10.VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU:
            case VK10.VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU:
                return 5;
            case VK10.VK_PHYSICAL_DEVICE_TYPE_CPU:
            case VK10.VK_PHYSICAL_DEVICE_TYPE_OTHER:
            default:
                return 4;
        }
    }
}
