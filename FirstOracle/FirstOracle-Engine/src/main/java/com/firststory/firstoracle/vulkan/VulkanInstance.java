/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.vulkan.allocators.VulkanFrameworkAllocator;
import com.firststory.firstoracle.vulkan.physicaldevice.VulkanPhysicalDevice;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.CannotCreateVulkanInstanceException;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.CannotCreateVulkanPhysicalDeviceException;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.NoDeviceSupportingVulkanEnoughException;
import com.firststory.firstoracle.vulkan.physicaldevice.exceptions.NoDeviceSupportingVulkanException;
import com.firststory.firstoracle.vulkan.physicaldevice.rendering.VulkanRenderingContext;
import com.firststory.firstoracle.window.WindowContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
class VulkanInstance {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanInstance.class );
    
    private final VulkanFrameworkAllocator allocator;
    private final VkInstanceCreateInfo createInfo;
    private final VkInstance instance;
    private final VkApplicationInfo applicationInfo;
    private final Set< VkExtensionProperties > extensionProperties;
    private final PriorityQueue< VulkanPhysicalDevice > physicalDevices;
    private final VulkanPhysicalDevice mainPhysicalDevice;
    private final Map< String, Long > enabledExtensions;
    private final Set< VkLayerProperties > layerProperties;
    private final List< String > validationLayerNames;
    private final VulkanWindowSurface windowSurface;
    private final VulkanRenderingContext renderingContext;
    private final VulkanDebugCallback debugCallback;
    
    VulkanInstance( WindowContext window ){
        allocator = new VulkanFrameworkAllocator();
        
        layerProperties = getLayerProperties();
        enabledExtensions = createEnabledExtensions();
        validationLayerNames = createValidationLayerNames();
        
        applicationInfo = createApplicationInfo();
        
        createInfo = createCreateInfo();
        extensionProperties = getExtensionProperties();
        instance = createInstance();
        debugCallback = allocator.createDebugCallback( instance );
        windowSurface = allocator.createWindowSurface( instance, window );
        physicalDevices = createPhysicalDevices();
        mainPhysicalDevice = selectMainPhysicalDevice();
        renderingContext = allocator.createRenderingContext( mainPhysicalDevice );
    }
    
    public void dispose() {
        allocator.dispose();
        VK10.vkDestroyInstance( instance, null );
    }
    
    void updateRenderingContext() {
        mainPhysicalDevice.updateRenderingContext();
    }
    
    void render( Renderer renderer, double lastFrameUpdate ) {
        mainPhysicalDevice.setUpSingleRender( renderingContext );
        renderer.render( renderingContext, lastFrameUpdate );
        mainPhysicalDevice.tearDownSingleRender( renderingContext );
    }
    
    private VulkanPhysicalDevice selectMainPhysicalDevice() {
        return physicalDevices.peek();
    }
    
    private Map< String, Long > createEnabledExtensions() {
        Map< String, Long > extensions = new HashMap<>();
        addGlfwInstanceExtensions( extensions );
        if ( PropertiesUtil.isDebugMode() ) {
            addExtension( extensions, EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME );
            
        }
        return extensions;
    }
    
    private List< String > createValidationLayerNames() {
        if ( !VulkanFramework.validationLayersAreEnabled() ) {
            return new ArrayList<>();
        }
        
        var layerNames = layerProperties.stream()
            .map( VkLayerProperties::layerNameString )
            .collect( Collectors.toSet() );
        var validationLayers = PropertiesUtil.getListFromProperty( PropertiesUtil.VULKAN_VALIDATION_LAYERS_LIST_PROPERTY );
        
        for ( var iterator = validationLayers.iterator(); iterator.hasNext(); ) {
            var layer = iterator.next();
            if ( !layerNames.contains( layer ) ) {
                iterator.remove();
                logger.warning( "Validation layer: " + layer + " is not available." );
            }
        }
        return validationLayers;
    }
    
    private void addExtension( Map< String, Long > enabledExtensions, String extensionName ) {
        enabledExtensions.put( extensionName, MemoryUtil.memAddress( MemoryStack.stackUTF8( extensionName ) ) );
    }
    
    private void addGlfwInstanceExtensions( Map< String, Long > enabledExtensions ) {
        var glfwInstanceExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        while ( glfwInstanceExtensions.hasRemaining() ) {
            var address = glfwInstanceExtensions.get();
            enabledExtensions.put( MemoryUtil.memUTF8( address ), address );
        }
    }
    
    private Set< VkLayerProperties > getLayerProperties() {
        var layerCount = new int[1];
        VK10.vkEnumerateInstanceLayerProperties( layerCount, null );
        var layerBuffer = VkLayerProperties.create( layerCount[0] );
        VK10.vkEnumerateInstanceLayerProperties( layerCount, layerBuffer );
        Set< VkLayerProperties > set = new HashSet<>( layerCount[0] );
        layerBuffer.iterator().forEachRemaining( set::add );
        return set;
    }
    
    private Set< VkExtensionProperties > getExtensionProperties() {
        var propertyCount = new int[1];
        VK10.vkEnumerateInstanceExtensionProperties( ( ByteBuffer ) null, propertyCount, null );
        var extensionsBuffer = VkExtensionProperties.create( propertyCount[0] );
        VK10.vkEnumerateInstanceExtensionProperties( ( ByteBuffer ) null, propertyCount, extensionsBuffer );
        Set< VkExtensionProperties > set = new HashSet<>( propertyCount[0] );
        extensionsBuffer.iterator().forEachRemaining( set::add );
        return set;
    }
    
    private PriorityQueue< VulkanPhysicalDevice > createPhysicalDevices() {
        var deviceCount = new int[1];
        VK10.vkEnumeratePhysicalDevices( instance, deviceCount, null );
        if ( deviceCount[0] == 0 ) {
            throw new NoDeviceSupportingVulkanException();
        }
        
        var devicesBuffer = PointerBuffer.allocateDirect( deviceCount[0] );
        VK10.vkEnumeratePhysicalDevices( instance, deviceCount, devicesBuffer );
        var queue = new PriorityQueue<>( VulkanPhysicalDevice::compareTo );
        while ( devicesBuffer.hasRemaining() ) {
            try {
                queue.add( this.allocator.createPhysicalDevice(
                    devicesBuffer.get(),
                    instance,
                    createValidationLayerNamesBuffer(),
                    windowSurface
                ) );
            } catch ( CannotCreateVulkanPhysicalDeviceException ex ) {
                logger.log( Level.WARNING, "Could not create physical device ", ex );
            }
        }
        if ( queue.isEmpty() ) {
            throw new NoDeviceSupportingVulkanEnoughException();
        }
        return queue;
    }
    
    private VkInstance createInstance() {
        var instancePointer = MemoryStack.stackCallocPointer( 1 );
        
        VulkanHelper.assertCallOrThrow(
            () -> VK10.vkCreateInstance( createInfo, null, instancePointer ),
            CannotCreateVulkanInstanceException::new
        );
        return new VkInstance( instancePointer.get(), createInfo );
    }
    
    private VkInstanceCreateInfo createCreateInfo() {
        return VkInstanceCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .pApplicationInfo( applicationInfo )
            .ppEnabledExtensionNames( createEnabledExtensionsBuffer() )
            .ppEnabledLayerNames( createValidationLayerNamesBuffer() );
    }
    
    private PointerBuffer createValidationLayerNamesBuffer() {
        var validationLayerNamesBuffer = MemoryUtil.memAllocPointer( validationLayerNames.size() );
        validationLayerNames.stream().map( MemoryUtil::memUTF8 ).forEach( validationLayerNamesBuffer::put );
        return validationLayerNamesBuffer.flip();
    }
    
    private PointerBuffer createEnabledExtensionsBuffer() {
        var enabledExtensionsBuffer = MemoryUtil.memAllocPointer( enabledExtensions.size() );
        enabledExtensions.values().forEach( enabledExtensionsBuffer::put );
        return enabledExtensionsBuffer.flip();
    }
    
    private VkApplicationInfo createApplicationInfo() {
        var applicationName = PropertiesUtil.getProperty( "ApplicationName",
            "Application powered by " + FirstOracleConstants.FIRST_ORACLE
        );
        var applicationVersion = VK10.VK_MAKE_VERSION( PropertiesUtil.getIntegerProperty( "ApplicationMajorVersion",
            1 ),
            PropertiesUtil.getIntegerProperty( "ApplicationMinorVersion", 0 ),
            PropertiesUtil.getIntegerProperty( "ApplicationPatchVersion", 0 )
        );
        var engineName = FirstOracleConstants.FIRST_ORACLE;
        var engineVersion = VK10.VK_MAKE_VERSION( FirstOracleConstants.FIRST_ORACLE_VERSION_MAJOR,
            FirstOracleConstants.FIRST_ORACLE_VERSION_MINOR,
            FirstOracleConstants.FIRST_ORACLE_VERSION_PATCH
        );
        return createApplicationInfo( applicationName, applicationVersion, engineName, engineVersion);
    }
    
    private VkApplicationInfo createApplicationInfo(
        String applicationName,
        int applicationVersion,
        String engineName,
        int engineVersion
    ) {
        var vulkanInfo = VkApplicationInfo.create();
        vulkanInfo.set( VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO,
            VK10.VK_NULL_HANDLE,
            MemoryStack.stackUTF8( applicationName ),
            applicationVersion,
            MemoryStack.stackUTF8( engineName ),
            engineVersion,
            VK10.VK_MAKE_VERSION( 1, 0, 2 )
            //VK10.VK_API_VERSION_1_0
        );
        return vulkanInfo;
    }
}
