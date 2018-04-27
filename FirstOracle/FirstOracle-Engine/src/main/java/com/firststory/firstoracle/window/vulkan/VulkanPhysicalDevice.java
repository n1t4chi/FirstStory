/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.window.vulkan.exceptions.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class VulkanPhysicalDevice implements Comparable< VulkanPhysicalDevice >, AutoCloseable {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanPhysicalDevice.class );
    private static final Set< String > requiredExtensions = new HashSet<>();
    
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
    private final VkDevice logicalDevice;
    private final VkQueue presentationQueue;
    private final VulkanWindowSurface windowSurface;
    private final List< VkExtensionProperties > availableExtensionProperties;
    private final VulkanSwapChain swapChain;
    private final List<VkPipelineShaderStageCreateInfo> shaderStages = new ArrayList<>(  );
    private final VulkanGraphicPipeline graphicPipeline;
    private final VulkanSemaphore renderFinishedSemaphore;
    private final Map< Integer, VulkanFrameBuffer > frameBuffers = new HashMap<>(  );
    private final VulkanCommandPool commandPool;
    private final VulkanSemaphore imageAvailableSemaphore;
    
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
        assertQueueFamilies();
        
        addUsedQueueFamily( graphicFamily, presentationFamily );
        logicalDevice = createLogicalDevice( validationLayerNamesBuffer );
        presentationQueue = createPresentationQueue();
        
        swapChain = windowSurface.createSwapChain( this );
        
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
    
        graphicPipeline = new VulkanGraphicPipeline( this );
        
        createFrameBuffers();
        
        commandPool = new VulkanCommandPool( this );
        
        imageAvailableSemaphore = new VulkanSemaphore( this );
        renderFinishedSemaphore = new VulkanSemaphore( this );
        
        logger.finer(
            "finished creating " + this + "[" +
                ", score: " + getScore() +
                ", graphic family: " + graphicFamily +
                ", presentation family: " + presentationFamily +
                ", queues: " + availableQueueFamilies.size() +
                ", extensions: " + availableExtensionProperties.size() +
                ", swapChain: " + swapChain +
            "]"
        );
    }
    
    Map< Integer, VulkanFrameBuffer > getFrameBuffers() {
        return frameBuffers;
    }
    
    void testRender() {
        commandPool.testRender();
    }
    
    private void createFrameBuffers() {
        for ( Map.Entry< Integer, VulkanImageView > entry : swapChain.getImageViews().entrySet() ) {
            frameBuffers.put( entry.getKey(), new VulkanFrameBuffer( this, entry.getValue() ) );
        }
    }
    
    private final VulkanShaderProgram vertexShader;
    private final VulkanShaderProgram fragmentShader;
    private static final String VERTEX_SHADER_FILE_PATH = "resources/First Oracle/vert.spv";
    private static final String FRAGMENT_SHADER_FILE_PATH = "resources/First Oracle/frag.spv";
    
    List<VkPipelineShaderStageCreateInfo> getShaderStages() {
        return shaderStages;
    }
    
    VulkanGraphicPipeline getGraphicPipeline() {
        return graphicPipeline;
    }
    
    VulkanQueueFamily getGraphicFamily() {
        return graphicFamily;
    }
    
    VulkanSwapChain getSwapChain() {
        return swapChain;
    }
    
    RenderingContext getRenderingContext() {
        return null;
    }
    
    
    public VulkanShaderProgram getVertexShader() {
        return vertexShader;
    }
    
    public VulkanShaderProgram getFragmentShader() {
        return fragmentShader;
    }
    
    @Override
    public void close() {
        frameBuffers.values().forEach( VulkanFrameBuffer::close );
        graphicPipeline.close();
        swapChain.close();
        vertexShader.dispose();
        fragmentShader.dispose();
        imageAvailableSemaphore.close();
        renderFinishedSemaphore.close();
        VK10.vkDestroyDevice( logicalDevice, null );
    }
    
    @Override
    public int compareTo( VulkanPhysicalDevice o ) {
        return Long.compare( getScore(), o.getScore() );
    }
    
    @Override
    public String toString() {
        return "VulkanPhysicalDevice@" + hashCode() + "[name:" + properties.deviceNameString() + "]";
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
            throw new DeviceHasNotEnoughMemoryException( this );
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
    
    private void assertQueueFamilies() {
        if ( graphicFamily == null ) {
            throw new CannotSelectVulkanQueueFamilyException( this, "graphic" );
        } else if ( presentationFamily == null ) {
            throw new CannotSelectVulkanQueueFamilyException( this, "presentation" );
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
    
    private VkQueue createPresentationQueue() {
        PointerBuffer queuePointer = MemoryStack.stackMallocPointer( 1 );
        VK10.vkGetDeviceQueue( logicalDevice, presentationFamily.getIndex(), 0, queuePointer );
        return new VkQueue( queuePointer.get(), logicalDevice );
        
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
        if ( VK10.vkCreateDevice( physicalDevice, createInfo, null, devicePointer ) != VK10.VK_SUCCESS ) {
            throw new CannotCreateVulkanLogicDeviceException( this );
        }
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
                windowSurface.getAddress(),
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
        VulkanQueueFamily selectedFamily = null;
        for ( VulkanQueueFamily family : availableQueueFamilies ) {
            if ( familyChecker.test( family ) && familyComparator.compare( selectedFamily, family ) < 0 ) {
                selectedFamily = family;
            }
        }
        if ( selectedFamily == null ) {
            logger.warning( "Could not select " + familyName + " family!" );
        }
        return selectedFamily;
    }
    
    private List< VulkanQueueFamily > getAvailableQueueFamilies() {
        int[] queueFamilyCount = new int[1];
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, null );
        VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = VkQueueFamilyProperties.create( queueFamilyCount[0] );
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, queueFamilyPropertiesBuffer );
        List< VulkanQueueFamily > queueFamilyProperties = new ArrayList<>( queueFamilyCount[0] );
        int index = 0;
        while ( queueFamilyPropertiesBuffer.hasRemaining() ) {
            VulkanQueueFamily family = new VulkanQueueFamily( queueFamilyPropertiesBuffer.get(), index++ );
            queueFamilyProperties.add( family );
            logger.finer( this + " -> " + family );
        }
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
