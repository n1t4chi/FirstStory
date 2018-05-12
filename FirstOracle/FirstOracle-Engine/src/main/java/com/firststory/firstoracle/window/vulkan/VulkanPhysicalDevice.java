/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
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
    
//    static final float[] VERTICES = new float[]{
//        /*1*/ /*pos*/ -0.75f, -0.75f, /*col*/ 1.0f, 0.0f, 1.0f,
//        /*2*/ /*pos*/ 0.75f, -0.75f, /*col*/ 1.0f, 1.0f, 0.0f,
//        /*3*/ /*pos*/ 0.0f, 0.75f, /*col*/ 0.0f, 1.0f, 1.0f
//    };
    static final float[] POSITION_1 = new float[]{
        /*3*/ /*pos*/ 0.0f, 0.75f,
        /*2*/ /*pos*/ 0.75f, 0.35f,
        /*1*/ /*pos*/ 0.35f, 0.35f,
    };
    static final float[] COLOUR_1 = new float[]{
        /*1*/  /*col*/ 1.0f, 0.0f, 1.0f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.0f,
        /*3*/ /*col*/ 0.0f, 1.0f, 1.0f
    };
    static final float[] POSITION_2 = new float[]{
        /*3*/ /*pos*/ 0.0f, 0.25f,
        /*2*/ /*pos*/ 0.25f, -0.35f,
        /*1*/ /*pos*/ -0.35f, -0.35f,
    };
    static final float[] COLOUR_2 = new float[]{
        /*1*/  /*col*/ 0.5f, 0.5f, 0.5f,
        /*2*/ /*col*/ 1.0f, 1.0f, 0.5f,
        /*3*/ /*col*/ 0.5f, 1.0f, 1.0f
    };
    private static final int FLOAT_SIZE = 4;
    private static final int MATRIX_SIZE = 4*4;
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
    private final List<VkPipelineShaderStageCreateInfo> shaderStages = new ArrayList<>(  );
    private final VulkanGraphicPipeline graphicPipeline;
    private final Map< Integer, VulkanFrameBuffer > frameBuffers = new HashMap<>(  );
    private final VulkanGraphicCommandPool graphicCommandPool;
    private final VulkanTransferCommandPool transferCommandPool;
    private final Set< VulkanCommandPool > commandPools = new HashSet<>(  );
    private final VulkanSemaphore renderFinishedSemaphore;
    private final VulkanSemaphore imageAvailableSemaphore;
    private final VulkanDataBufferProvider bufferLoader;
    private final VkPhysicalDeviceMemoryProperties memoryProperties;
    private final Map< Integer, VulkanMemoryType > memoryTypes = new HashMap<>();
    private final Map< Integer, VulkanMemoryHeap > memoryHeaps = new HashMap<>();
    private final VulkanShaderProgram vertexShader;
    private final VulkanShaderProgram fragmentShader;
    private final VulkanAddress descriptorSetLayout;
    private final VulkanDataBuffer positionBuffer1;
    private final VulkanDataBuffer positionBuffer2;
    private final VulkanDataBuffer colourBuffer1;
    private final VulkanDataBuffer colourBuffer2;
    private final VulkanUniformBuffer uniformBuffer;
    
    private final float[] uniformBufferData = new float[ UNIFORM_SIZE ];
    private final Matrix4f matrix = new Matrix4f(  );
    private final Vector2f trans = new Vector2f( 0.01f,0.01f );
    private final Vector2f scale = new Vector2f( 1,1 );
    private final VulkanAddress descriptorPool;
    private final VulkanAddress descriptorSet;
    private VulkanDataBuffer textureBuffer;
    private final VulkanTextureLoader textureLoader;
    
    VulkanAddress getDescriptorSetLayout() {
        return descriptorSetLayout;
    }
    
    VulkanPhysicalDevice(
        long deviceAddress,
        VkInstance instance,
        PointerBuffer validationLayerNamesBuffer,
        VulkanWindowSurface surface
    ) throws CannotCreateVulkanPhysicalDeviceException {
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
    
        try{
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
        graphicCommandPool = new VulkanGraphicCommandPool( this, graphicFamily,
            imageAvailableSemaphore,
            renderFinishedSemaphore
        );
        transferCommandPool = new VulkanTransferCommandPool( this, transferFamily, imageAvailableSemaphore );
        commandPools.add( graphicCommandPool );
        commandPools.add( transferCommandPool );
        
        bufferLoader = new VulkanDataBufferProvider( this );
    
        textureLoader = new VulkanTextureLoader( this, bufferLoader );
        
        descriptorPool = createDescriptorPool();
        descriptorSet = createDescriptorSet();
    
        uniformBuffer = bufferLoader.createUniformBuffer( UNIFORM_SIZE, FLOAT_SIZE );
        
        updateDesciptorSetsOnDevice();
        updateRenderingContext();
    
    
        Texture texture;
        try {
            texture = new Texture( "resources/First Oracle/texture2D.png" );
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
        texture.load( textureLoader );
    
    
    
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
    
        
        logger.finer(
            "finished creating " + this + "[" +
                "\nscore: " + getScore() +
                "\ngraphic family: " + graphicFamily +
                "\npresentation family: " + presentationFamily +
                "\ntransfer family: " + transferFamily +
                "\nqueues: " + availableQueueFamilies.size() +
                "\nextensions: " + availableExtensionProperties.size() +
                "\nmemory types: " + memoryTypesToString() +
            "]"
        );
    }
    
    private void updateDesciptorSetsOnDevice() {
        VkDescriptorBufferInfo bufferInfo = VkDescriptorBufferInfo.create()
            .buffer( uniformBuffer.getBufferAddress().getValue() )
            .offset( 0 )
            .range( UNIFORM_DATA_SIZE )
        ;
        VkWriteDescriptorSet descriptorWrite = VkWriteDescriptorSet.create()
            .sType( VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET )
            .dstSet( descriptorSet.getValue() )
            .dstBinding( 0 )
            .dstArrayElement( 0 )
            .descriptorType( VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER )
            .pBufferInfo( VkDescriptorBufferInfo.create( 1 ).put( 0, bufferInfo ) )
            .pImageInfo( null )
            .pTexelBufferView( null )
        ;
        VK10.vkUpdateDescriptorSets( logicalDevice,
            VkWriteDescriptorSet.create( 1 ).put( 0, descriptorWrite ), null );
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
        VkDescriptorPoolSize poolSize = VkDescriptorPoolSize.create()
            .type( VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER )
            .descriptorCount( 1 );
        VkDescriptorPoolCreateInfo createInfo = VkDescriptorPoolCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO )
            .pPoolSizes( VkDescriptorPoolSize.create( 1 ).put( 0, poolSize ) )
            .maxSets( 1 )
        ;
        return VulkanHelper.createAddress( address ->
            VK10.vkCreateDescriptorPool( logicalDevice, createInfo, null, address ) ,
            resultCode -> new CannotCreateVulkanDescriptorPoolException( this, resultCode )
        );
    }
    
    private VulkanAddress createDescriptorSetLayout() {
        VkDescriptorSetLayoutBinding layoutBinding = VkDescriptorSetLayoutBinding.create()
            .binding( 0 )
            .descriptorType( VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER )
            .descriptorCount( 1 )
            .stageFlags( VK10.VK_SHADER_STAGE_VERTEX_BIT )
            .pImmutableSamplers( null )
        ;
        
        VkDescriptorSetLayoutCreateInfo createInfo = VkDescriptorSetLayoutCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO )
            .pBindings( VkDescriptorSetLayoutBinding.calloc( 1 ).put( layoutBinding ).flip() )
        ;
        
        return VulkanHelper.createAddress( address ->
                VK10.vkCreateDescriptorSetLayout( logicalDevice, createInfo, null, address ),
            resultCode -> new CannotCreateVulkanDescriptorSetLayoutException( this, resultCode )
        );
    }
    
    VulkanTransferCommandPool getTransferCommandPool() {
        return transferCommandPool;
    }
    
    private boolean isGraphicAndTransferQueueSame() {
        return graphicFamily.equals( transferFamily );
    }
    
    boolean isSingleCommandPoolUsed() {
        return commandPools.size() == 1;
    }
    
    public Map< Integer, VulkanMemoryType > getMemoryTypes() {
        return memoryTypes;
    }
    
    VulkanMemoryType selectMemoryType( int desiredType, int... desiredFlags ) {
        List< Integer > collect = memoryTypes.values()
            .stream()
            .map( o -> o.getType().propertyFlags() )
            .collect( Collectors.toList() );
        for ( VulkanMemoryType memoryType : memoryTypes.values() ) {
            if( checkMemoryTypeFlags( memoryType.getIndex(), memoryType.propertyFlags(), desiredType, desiredFlags ) ) {
                return memoryType;
            }
        }
        throw new CannotSelectSuitableMemoryTypeException( this, desiredType, desiredFlags );
    }
    
    private boolean checkMemoryTypeFlags( int index, int memoryFlags, int desiredType, int... desiredFlags ) {
        if( ( desiredType & ( 1 << index ) ) == 0 ) {
            return false;
        }
        return ( memoryFlags & VulkanHelper.flagsToInt( desiredFlags ) ) != 0 ;
    }
    
    private void fillMemoryHeaps() {
        VulkanHelper.iterate( memoryProperties.memoryHeaps(),
            ( index, memoryHeap ) -> memoryHeaps.put( index, new VulkanMemoryHeap( index, memoryHeap ) ) );
    }
    
    private void fillMemoryTypes() {
        VulkanHelper.iterate( memoryProperties.memoryTypes(),
            ( index, memoryType ) -> memoryTypes.put( index, new VulkanMemoryType( index, memoryType ) ) );
    }
    
    @Override
    public int compareTo( VulkanPhysicalDevice o ) {
        return Long.compare( getScore(), o.getScore() );
    }
    
    @Override
    public String toString() {
        return "VulkanPhysicalDevice@" + hashCode() + "[name:" + properties.deviceNameString() + "]";
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
    
    private float rotate = 0;
    void testRender() {
        int index = aquireNextImageIndex();
        
        matrix.identity().scale( ThreadLocalRandom.current().nextFloat()/10+1 ).rotateZ( rotate = rotate + 0.001f );
        matrix.get( uniformBufferData, 0 );
        uniformBufferData[16] = trans.x = trans.x*1.00001f;
        uniformBufferData[17] = trans.y = trans.y*1.00001f;
        uniformBufferData[18] = scale.x = scale.x*1.00001f;
        uniformBufferData[19] = scale.y = scale.y*1.00001f;
        uniformBuffer.load( uniformBufferData );
        
        
        VulkanCommandBuffer currentGraphicCommandBuffer = graphicCommandPool.getCommandBuffer( index );
        currentGraphicCommandBuffer.renderQueue( commandBuffer -> {
            currentGraphicCommandBuffer.bindDescriptorSets( descriptorSet );
            currentGraphicCommandBuffer.drawVertices( positionBuffer1, colourBuffer1 );
            currentGraphicCommandBuffer.drawVertices( positionBuffer2, colourBuffer2 );
        } );
    
        graphicCommandPool.submitQueue( currentGraphicCommandBuffer );
        graphicCommandPool.presentQueue( swapChain, index );
    
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
        return (
            (
                properties.limits().maxMemoryAllocationCount() + properties.limits().maxImageDimension2D() +
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

    private VkPhysicalDeviceMemoryProperties createPhysicalDeviceMemoryProperties() {
        VkPhysicalDeviceMemoryProperties memoryProperties = VkPhysicalDeviceMemoryProperties.create();
        VK10.vkGetPhysicalDeviceMemoryProperties( physicalDevice, memoryProperties );
        return memoryProperties;
    }
    
    private void refreshCommandBuffers() {
        commandPools.forEach( pool -> pool.refreshCommandBuffers( frameBuffers, graphicPipeline, swapChain ) );
    }
    
    int aquireNextImageIndex() {
        try {
            return tryToAquireNextImageIndex();
        } catch ( VulkanNextImageIndexException ex1 ) {
            logger.log( Level.WARNING,"Exception during aquiring next image index" , ex1 );
            try {
                updateRenderingContext();
                return tryToAquireNextImageIndex();
            } catch ( Exception ex2 ) {
                throw new VulkanNextImageIndexException( this, ex1, ex2 );
            }
        }
    }
    
    private int tryToAquireNextImageIndex() {
        int[] imageIndex = new int[1];
        int result = KHRSwapchain.vkAcquireNextImageKHR(
            logicalDevice,
            swapChain.getAddress().getValue(),
            Long.MAX_VALUE,
            imageAvailableSemaphore.getAddress().getValue(),
            VK10.VK_NULL_HANDLE,
            imageIndex
        );
        
        switch( result ) {
            case KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR :
                throw new OutOfDateSwapChainException( this, imageIndex[0] );
            case KHRSwapchain.VK_SUBOPTIMAL_KHR :
                logger.warning(
                    "Swap chain images no longer match surface. Update might be required at later time" );
            case VK10.VK_SUCCESS : break;
            default:
                throw new CannotAquireNextImageIndexException( this, imageIndex[0], result );
        }
        return imageIndex[0];
    }
    
    private void refreshFrameBuffers( Map< Integer, VulkanFrameBuffer > frameBuffers ) {
        disposeFrameBuffers( frameBuffers );
        for ( Map.Entry< Integer, VulkanImageView > entry : swapChain.getImageViews().entrySet() ) {
            frameBuffers.put( entry.getKey(), new VulkanFrameBuffer(
                this,
                entry.getValue(),
                graphicPipeline,
                swapChain
            ) );
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
        VK10.vkEnumerateDeviceExtensionProperties(
            physicalDevice,
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
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.create();
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
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkCreateDevice( physicalDevice, createInfo, null, devicePointer ),
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
        return selectSuitableFamily(
            family -> family.isFlagSet( VK10.VK_QUEUE_TRANSFER_BIT ) && family != graphicFamily,
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
    ) {
        return selectSuitableFamily( familyChecker, familyComparator, () -> {
            logger.warning( "Could not select " + familyName + " family!" );
            throw new CannotSelectVulkanQueueFamilyException( this, familyName );
        } );
    }
    
    private VulkanQueueFamily selectSuitableFamily(
        Predicate< VulkanQueueFamily > familyChecker,
        Comparator< VulkanQueueFamily > familyComparator,
        Supplier< VulkanQueueFamily > noSelectedFamilyAction
    ) {
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
    
    public String memoryTypesToString() {
        StringBuilder memoryTypesString = new StringBuilder(  );
        memoryTypesString.append( "{" );
        memoryTypes.values().forEach( memoryType ->
            memoryTypesString.append( " { heapIndex:" )
                .append( memoryType.heapIndex() )
                .append( ", flags:" )
                .append( memoryType.propertyFlags() )
                .append( " }," ) );
        memoryTypesString.deleteCharAt( memoryTypesString.length()-1 );
        memoryTypesString.append( " }" );
        return memoryTypesString.toString();
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
