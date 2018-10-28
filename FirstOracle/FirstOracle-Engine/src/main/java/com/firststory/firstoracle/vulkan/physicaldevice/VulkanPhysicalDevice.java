/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.vulkan.VulkanFramework;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.VulkanWindowSurface;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.allocators.VulkanFrameworkAllocator;
import com.firststory.firstoracle.vulkan.exceptions.*;
import com.firststory.firstoracle.vulkan.physicaldevice.buffer.VulkanBufferProvider;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.*;
import com.firststory.firstoracle.vulkan.physicaldevice.transfer.VulkanTransferCommandPool;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class VulkanPhysicalDevice implements Comparable< VulkanPhysicalDevice > {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanPhysicalDevice.class );
    private static final Set< String > requiredExtensions = new HashSet<>();
    
    static {
        requiredExtensions.add( KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME );
    }
    
    private final VulkanFrameworkAllocator instanceAllocator;
    private final VulkanDeviceAllocator allocator;
    private final VkPhysicalDevice physicalDevice;
    private final VkPhysicalDeviceFeatures features;
    private final VkPhysicalDeviceProperties properties;
    private final VKCapabilitiesInstance capabilities;
    private final List< VulkanQueueFamily > availableQueueFamilies;
    private final Set< VulkanQueueFamily > usedQueueFamilies = new HashSet<>();
    private final VulkanQueueFamily graphicFamily;
    private final VulkanQueueFamily presentationFamily;
    private final VulkanQueueFamily vertexTransferFamily;
    private final VulkanQueueFamily uniformTransferFamily;
    private final VulkanQueueFamily quickTransferFamily;
    private final VkDevice logicalDevice;
    private final VulkanWindowSurface windowSurface;
    private final List< VkExtensionProperties > availableExtensionProperties;
    
    private final VulkanGraphicPipelines trianglePipelines;
    private final VulkanGraphicPipelines linePipelines;
    
    private final Map< Integer, VulkanFrameBuffer > frameBuffers = new HashMap<>();
    
    private final VulkanTransferCommandPool vertexDataTransferCommandPool;
    private final VulkanTransferCommandPool uniformDataTransferCommandPool ;
    private final VulkanTransferCommandPool quickDataTransferCommandPool;
    private final VulkanTransferCommandPool textureTransferCommandPool;
    private final VkPhysicalDeviceMemoryProperties memoryProperties;
    private final Map< Integer, VulkanMemoryType > memoryTypes = new HashMap<>();
    private final Map< Integer, VulkanMemoryHeap > memoryHeaps = new HashMap<>();
    
    private final VulkanSwapChain swapChain;
    private final VulkanBufferProvider bufferProvider;
    private final VulkanDescriptor descriptor;
    private final VulkanTextureLoader textureLoader;
    private final VulkanDepthResources depthResources;
    private final VulkanShaderProgram shaderProgram3D;
    private final VulkanTextureSampler textureSampler;
    
    private final VulkanVertexAttributeLoader vertexAttributeLoader;
    private final ExecutorService executorService;
    
    private VulkanImageIndex currentImageIndex;
    
    public VulkanPhysicalDevice(
        VulkanFrameworkAllocator instanceAllocator,
        long deviceAddress,
        VkInstance instance,
        PointerBuffer validationLayerNamesBuffer,
        VulkanWindowSurface surface
    ) throws CannotCreateVulkanPhysicalDeviceException {
        this.instanceAllocator = instanceAllocator;
        allocator = instanceAllocator.createPhysicalDeviceAllocator( this );
        executorService = Executors.newCachedThreadPool();
        try {
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
            
            var transferFamilies = new LinkedList< VulkanQueueFamily >();
            quickTransferFamily = selectTransferFamily( transferFamilies );
            transferFamilies.add( quickTransferFamily );
            uniformTransferFamily = selectTransferFamily( transferFamilies );
            transferFamilies.add( uniformTransferFamily );
            vertexTransferFamily = selectTransferFamily( transferFamilies );
            transferFamilies.add( vertexTransferFamily );
            
            usedQueueFamilies.add( graphicFamily );
            usedQueueFamilies.add( presentationFamily );
            usedQueueFamilies.addAll( transferFamilies );
            logicalDevice = createLogicalDevice( validationLayerNamesBuffer );
            
            memoryProperties = createPhysicalDeviceMemoryProperties();
            fillMemoryTypes();
            fillMemoryHeaps();
            
            swapChain = allocator.createSwapChain();
        
            trianglePipelines = allocator.createGraphicPipelines( VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST );
            linePipelines = allocator.createGraphicPipelines( VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST );
            
            vertexDataTransferCommandPool = allocator.createTransferCommandPool(vertexTransferFamily );
            uniformDataTransferCommandPool = allocator.createTransferCommandPool( uniformTransferFamily );
            quickDataTransferCommandPool = allocator.createTransferCommandPool( quickTransferFamily );
            textureTransferCommandPool = allocator.createTransferCommandPool( graphicFamily );
        
            bufferProvider = allocator.createBufferProvider(
                vertexDataTransferCommandPool,
                quickDataTransferCommandPool,
                uniformDataTransferCommandPool,
                textureTransferCommandPool,
                extractUniformBufferOffsetAlignment()
            );
        
        
            depthResources = allocator.createDepthResource();
            descriptor = allocator.createDescriptor();
            textureLoader = allocator.createTextureLoader( bufferProvider );
            textureSampler = allocator.createTextureSampler();
            
            shaderProgram3D = allocator.createShaderProgram( bufferProvider );
            //shaderProgram2D = new VulkanShaderProgram2D( this, bufferProvider );
            try {
                shaderProgram3D.compile();
                //shaderProgram2D.compile();
            } catch ( IOException ex ) {
                throw new CannotCreateVulkanPhysicalDeviceException( this, ex );
            }
            
            updateRenderingContext();
            
            vertexAttributeLoader = new VulkanVertexAttributeLoader( bufferProvider );
            
            
            
            logger.finer( "finished creating " + this + "[" +
                "\nscore: " + getScore() +
                "\ngraphic family: " + graphicFamily +
                "\npresentation family: " + presentationFamily +
                "\ntransfer family: " + vertexTransferFamily +
                "\nqueues: " + availableQueueFamilies.size() +
                "\nextensions: " + availableExtensionProperties.size() +
                "\nmemory types: " + memoryTypesToString() +
            "]" );
        } catch ( CannotCreateVulkanPhysicalDeviceException ex ) {
            allocator.dispose();
            throw ex;
        } catch ( Exception ex ) {
            allocator.dispose();
            throw new CannotCreateVulkanPhysicalDeviceException( this, ex );
        }
    }
    
    public VulkanFrameBuffer getCurrentFrameBuffer() {
        return frameBuffers.get( currentImageIndex.getIndex() );
    }
    
    public VulkanQueueFamily getGraphicQueueFamily() {
        return graphicFamily;
    }
    
    public VulkanTextureSampler getTextureSampler() {
        return textureSampler;
    }
    
    public VulkanDeviceAllocator getAllocator() {
        return allocator;
    }
    
    public ExecutorService getEventExecutorService() {
        return executorService;
    }
    
    VulkanQueueFamily getGraphicFamily() {
        return graphicFamily;
    }
    
    private long extractUniformBufferOffsetAlignment() {
        return properties.limits().minUniformBufferOffsetAlignment();
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
        var memoryTypesString = new StringBuilder();
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
    
    public VulkanDescriptor getDescriptor() {
        return descriptor;
    }
    
    public VulkanVertexAttributeLoader getVertexAttributeLoader() {
        return vertexAttributeLoader;
    }
    
    public VulkanShaderProgram getShaderProgram3D() {
        return shaderProgram3D;
    }
    
    public VulkanTextureLoader getTextureLoader() {
        return textureLoader;
    }
    
    public VulkanTransferCommandPool getTextureTransferCommandPool() {
        return textureTransferCommandPool;
    }
    
    public VulkanMemoryType selectMemoryType( int desiredType, int... desiredFlags ) {
        for ( var memoryType : memoryTypes.values() ) {
            if( checkMemoryTypeFlags( memoryType.getIndex(), memoryType.propertyFlags(), desiredType, desiredFlags ) ) {
                return memoryType;
            }
        }
        throw new CannotSelectSuitableMemoryTypeException( this, desiredType, desiredFlags );
    }
    
    public VulkanBufferProvider getBufferProvider() {
        return bufferProvider;
    }
    
    public void updateRenderingContext() {
        presentationFamily.waitForQueue();
        swapChain.update( windowSurface );
        depthResources.update( swapChain );
    
        updatePipeline( trianglePipelines );
        updatePipeline( linePipelines );
        refreshFrameBuffers( swapChain, this.trianglePipelines.getOverlayPipeline().getRenderPass(), depthResources );
    }
    
    private void updatePipeline( VulkanGraphicPipelines pipeline ) {
        pipeline.update(
            swapChain,
            shaderProgram3D.getShaderStages(),
            depthResources,
            descriptor.getDescriptorSetLayout()
        );
    }
    
    public void setUpSingleRender( VulkanRenderingContext renderingContext ) {
        renderingContext.setUpSingleRender();
    }
    
    public void tearDownSingleRender( VulkanRenderingContext renderingContext ) {
        currentImageIndex = acquireNextImageIndex();
        renderingContext.tearDownSingleRender(
            trianglePipelines,
            linePipelines,
            getCurrentImageIndex(),
            swapChain,
            vertexDataTransferCommandPool,
            quickDataTransferCommandPool,
            uniformDataTransferCommandPool,
            textureTransferCommandPool
        );
    }
    
    public void dispose() {
        instanceAllocator.deregisterPhysicalDevice( this );
    }
    
    public void disposeUnsafe() {
        presentationFamily.waitForQueue();
        
        allocator.dispose();
        frameBuffers.clear();
    
        try {
            executorService.awaitTermination( 30, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            logger.log( Level.SEVERE, "Interrupted while trying to stop executors.", e );
        }
        VK10.vkDestroyDevice( logicalDevice, null );
    }
    
    public VkDevice getLogicalDevice() {
        return logicalDevice;
    }
    
    public boolean isSingleQueueFamilyUsed() {
        return usedQueueFamilies.size() == 1;
    }
    
    public VkPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
    
    long getScore() {
        return ( (
            properties.limits().maxMemoryAllocationCount() +
                properties.limits().maxImageDimension2D() +
                properties.limits().maxDescriptorSetSamplers() +
                ( availableExtensionProperties.size() + availableQueueFamilies.size() ) * 1000
        ) * typeMultiplier()
        );
    }
    
    public IntBuffer createQueueFamilyIndicesBuffer() {
        var buffer = MemoryUtil.memAllocInt( usedQueueFamilies.size() );
        usedQueueFamilies.forEach( family -> buffer.put( family.getIndex() ) );
        buffer.flip();
        return buffer;
    }
    
    public VulkanImageIndex getCurrentImageIndex() {
        return currentImageIndex;
    }
    
    private VulkanImageIndex acquireNextImageIndex() {
        try {
            return tryToAcquireNextImageIndex();
        } catch ( VulkanNextImageIndexException ex1 ) {
            logger.log( Level.WARNING, "Exception during acquiring next image index. Updating rendering context.", ex1 );
            try {
                updateRenderingContext();
                var imageIndex = tryToAcquireNextImageIndex();
                logger.log( Level.FINEST, "Acquired image index: " + imageIndex + " after context update." );
                return imageIndex;
            } catch ( Exception ex2 ) {
                throw new VulkanNextImageIndexException( this, ex1, ex2 );
            }
        }
    }
    
    VulkanFormatProperty findFormatProperty( Set< Integer > candidates, Integer tiling, Integer features ) {
        var properties = createFormatProperties( candidates );
        return selectFormatProperty( tiling, features, properties );
    }
    
    private VulkanFormatProperty selectFormatProperty(
        Integer tiling, Integer features, Set< VulkanFormatProperty > properties
    ) {
        for ( var property : properties ) {
            if ( tiling == VK10.VK_IMAGE_TILING_LINEAR && ( property.linearTilingFeatures() & features ) == features ||
                tiling == VK10.VK_IMAGE_TILING_OPTIMAL && ( property.optimalTilingFeatures() & features ) == features )
            {
                return property;
            }
        }
        throw new CannotSelectVulkanFormatPropertyException( this );
    }
    
    private Set< VulkanFormatProperty > createFormatProperties( Set< Integer > candidates ) {
        Set< VulkanFormatProperty > properties = new HashSet<>(  );
        for( var format : candidates ) {
            var property = VkFormatProperties.create();
            VK10.vkGetPhysicalDeviceFormatProperties( physicalDevice, format, property );
            properties.add( new VulkanFormatProperty( format, property ) );
        }
        return properties;
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
        var memoryProperties = VkPhysicalDeviceMemoryProperties.create();
        VK10.vkGetPhysicalDeviceMemoryProperties( physicalDevice, memoryProperties );
        return memoryProperties;
    }
    
    private VulkanImageIndex tryToAcquireNextImageIndex() {
        var imageIndex = new int[1];
        var semaphore = allocator.createSemaphore();
        var result = KHRSwapchain.vkAcquireNextImageKHR(
            logicalDevice,
            swapChain.getAddress().getValue(),
            Long.MAX_VALUE,
            semaphore.getAddress().getValue(),
            VK10.VK_NULL_HANDLE,
            imageIndex
        );
        
        try{
            switch ( result ) {
                case KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR:
                    throw new OutOfDateSwapChainException( this, imageIndex[0] );
                case KHRSwapchain.VK_SUBOPTIMAL_KHR:
                    logger.warning( "Swap chain images no longer match surface. Update might be required at later time" );
                case VK10.VK_SUCCESS:
                    break;
                default:
                    throw new CannotAcquireNextImageIndexException( this, imageIndex[0], result );
            }
        } catch ( RuntimeException ex ) {
            semaphore.dispose();
            throw ex;
        }
    
        return new VulkanImageIndex( imageIndex[0], semaphore, allocator.createSemaphore() );
    }
    
    private void refreshFrameBuffers(
        VulkanSwapChain swapChain,
        VulkanRenderPass renderPass,
        VulkanDepthResources depthResources
    ) {
        disposeFrameBuffers();
        for ( var entry : swapChain.getImageViews().entrySet() ) {
            frameBuffers.put( entry.getKey(),
                allocator.createFrameBuffer( entry.getValue(), renderPass, swapChain, depthResources )
            );
        }
    }
    
    private void disposeFrameBuffers() {
        frameBuffers.forEach( ( integer, vulkanFrameBuffer ) -> vulkanFrameBuffer.dispose() );
        frameBuffers.clear();
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
        if( properties.limits().maxDescriptorSetSamplers() <= 95 ) {
            throw new DeviceHasNotEnoughSamplers( this, properties.limits().maxDescriptorSetSamplers() );
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
        var extensionCount = new int[1];
        VK10.vkEnumerateDeviceExtensionProperties( physicalDevice, ( ByteBuffer ) null, extensionCount, null );
        var extensionPropertiesBuffer = VkExtensionProperties.create( extensionCount[0] );
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
        var buffer = createVkDeviceQueueCreateInfoBuffer( usedQueueFamilies.size() );
        usedQueueFamilies.forEach( family -> {
            var queueInfo = VkDeviceQueueCreateInfo.create()
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
        var features = VkPhysicalDeviceFeatures.create()
            .samplerAnisotropy( true )
            ;
        VK10.vkGetPhysicalDeviceFeatures( physicalDevice, features );
        return features;
    }
    
    private VkPhysicalDeviceProperties createDeviceProperties() {
        var properties = VkPhysicalDeviceProperties.create();
        VK10.vkGetPhysicalDeviceProperties( physicalDevice, properties );
        return properties;
    }
    
    private VkDevice createLogicalDevice( PointerBuffer validationLayerNamesBuffer ) {
        var createInfo = createDeviceCreateInfo();
        if ( VulkanFramework.validationLayersAreEnabled() ) {
            createInfo.ppEnabledLayerNames( validationLayerNamesBuffer );
        }
        var devicePointer = MemoryUtil.memAllocPointer( 1 );
        VulkanHelper.assertCallOrThrow( () -> VK10.vkCreateDevice( physicalDevice, createInfo, null, devicePointer ),
            resultCode -> new CannotCreateVulkanLogicDeviceException( this, resultCode )
        );
        return new VkDevice( devicePointer.get(), physicalDevice, createInfo );
    }
    
    private PointerBuffer createEnabledExtensionNamesBuffer() {
        var extensionsBuffer = MemoryUtil.memAllocPointer( requiredExtensions.size() );
        requiredExtensions.forEach( ex -> extensionsBuffer.put( MemoryUtil.memAddress( MemoryStack.stackUTF8( ex ) ) ) );
        extensionsBuffer.flip();
        return extensionsBuffer;
    }
    
    private VulkanQueueFamily selectTransferFamily( Deque< VulkanQueueFamily > alreadySelectedFamilies ) {
        return selectSuitableFamily(
            family ->
                family.isFlagSet( VK10.VK_QUEUE_TRANSFER_BIT ) &&
                family != graphicFamily &&
                !alreadySelectedFamilies.contains( family )
            ,
            ( first, second ) -> VulkanQueueFamily.compare( first, second ),
            () -> {
                var family = alreadySelectedFamilies.poll();
                if( family != null ) {
                    logger.warning( "Selecting previous family for transfer family." );
                    return family;
                } else {
                    logger.warning( "Selecting graphic family for transfer family." );
                    return graphicFamily;
                }
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
            var supports = new int[1];
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
        for ( var family : availableQueueFamilies ) {
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
        var queueFamilyCount = new int[1];
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, null );
        var queueFamilyPropertiesBuffer = VkQueueFamilyProperties.create( queueFamilyCount[0] );
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, queueFamilyPropertiesBuffer );
        List< VulkanQueueFamily > queueFamilyProperties = new ArrayList<>( queueFamilyCount[0] );
        
        VulkanHelper.iterate( queueFamilyPropertiesBuffer, ( index, properties ) -> {
            var family = new VulkanQueueFamily( this, properties, index );
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
