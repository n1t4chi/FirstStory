/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.rendering.FrameworkCommands;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.window.WindowContext;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanInstanceException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanPhysicalDeviceException;
import com.firststory.firstoracle.window.vulkan.exceptions.NoDeviceSupportingVulkanEnoughException;
import com.firststory.firstoracle.window.vulkan.exceptions.NoDeviceSupportingVulkanException;
import com.firststory.firstoracle.window.vulkan.rendering.VulkanRenderingContext;
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

public class VulkanFramework implements RenderingFramework {
    
    private static Logger logger = FirstOracleConstants.getLogger( VulkanFramework.class );
    
    static boolean validationLayersAreEnabled() {
        return PropertiesUtil.isPropertyTrue( PropertiesUtil.VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY );
    }
    
    private final VkApplicationInfo applicationInfo;
    private final VkInstanceCreateInfo createInfo;
    private final VkInstance instance;
    private final Set< VkExtensionProperties > extensionProperties;
    private final PriorityQueue< VulkanPhysicalDevice > physicalDevices;
    private final VulkanPhysicalDevice mainPhysicalDevice;
    private final Map< String, Long > enabledExtensions;
    private final Set< VkLayerProperties > layerProperties;
    private final List< String > validationLayerNames;
    private final WindowContext window;
    private final VulkanWindowSurface windowSurface;
    private VkDebugReportCallbackEXT callback = null;
    private final VulkanRenderingContext renderingContext;
    
    VulkanFramework( WindowContext window ) {
        this.window = window;
        if ( !GLFWVulkan.glfwVulkanSupported() ) {
            throw new RuntimeException( "GLFW Vulkan not supported!" );
        }
        
        String applicationName = PropertiesUtil.getProperty( "ApplicationName",
            "Application powered by " + FirstOracleConstants.FIRST_ORACLE
        );
        int applicationVersion = VK10.VK_MAKE_VERSION( PropertiesUtil.getIntegerProperty( "ApplicationMajorVersion",
            1 ),
            PropertiesUtil.getIntegerProperty( "ApplicationMinorVersion", 0 ),
            PropertiesUtil.getIntegerProperty( "ApplicationPatchVersion", 0 )
        );
        String engineName = FirstOracleConstants.FIRST_ORACLE;
        int engineVersion = VK10.VK_MAKE_VERSION( FirstOracleConstants.FIRST_ORACLE_VERSION_MAJOR,
            FirstOracleConstants.FIRST_ORACLE_VERSION_MINOR,
            FirstOracleConstants.FIRST_ORACLE_VERSION_PATCH
        );
        
        layerProperties = getLayerProperties();
        enabledExtensions = createEnabledExtensions();
        validationLayerNames = createValidationLayerNames();
        
        applicationInfo = createApplicationInfo( applicationName, applicationVersion, engineName, engineVersion );
        createInfo = createCreateInfo();
        extensionProperties = getExtensionProperties();
        instance = createInstance();
        
        if ( PropertiesUtil.isDebugMode() ) {
            setupDebugCallback();
        }
        
        windowSurface = VulkanWindowSurface.create( instance, window );
        physicalDevices = createPhysicalDevices();
        mainPhysicalDevice = selectMainPhysicalDevice();
        renderingContext = new VulkanRenderingContext( mainPhysicalDevice, mainPhysicalDevice.getDescriptorPool() );
    
        logger.finer( "Finished creating Vulkan Framework: " + this );
    }
    
    @Override
    public RenderingContext getRenderingContext() {
        return renderingContext;
    }
    
    @Override
    public void updateViewPort( int x, int y, int width, int height ) {
        mainPhysicalDevice.updateRenderingContext();
    }
    
    @Override
    public void invoke( FrameworkCommands commands ) throws Exception {
        mainPhysicalDevice.updateBackground( renderingContext.getBackgroundColour() );
        commands.execute( this );
    }
    
    @Override
    public void compileShaders() {}
    
    @Override
    public void close() {
        if ( PropertiesUtil.isDebugMode() ) {
            disposeDebugCallback();
        }
        physicalDevices.forEach( VulkanPhysicalDevice::dispose );
        windowSurface.dispose( instance );
        VK10.vkDestroyInstance( instance, null );
    }
    
    @Override
    public void render( Renderer renderer, double lastFrameUpdate ) {
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
        if ( !validationLayersAreEnabled() ) {
            return new ArrayList<>();
        }
        
        Set< String > layerNames = layerProperties.stream()
            .map( VkLayerProperties::layerNameString )
            .collect( Collectors.toSet() );
        List< String > validationLayers = PropertiesUtil.getListFromPropertySafe( PropertiesUtil.VULKAN_VALIDATION_LAYERS_LIST_PROPERTY );
        
        for ( Iterator< String > iterator = validationLayers.iterator(); iterator.hasNext(); ) {
            String layer = iterator.next();
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
        PointerBuffer glfwInstanceExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        while ( glfwInstanceExtensions.hasRemaining() ) {
            long address = glfwInstanceExtensions.get();
            enabledExtensions.put( MemoryUtil.memUTF8( address ), address );
        }
    }
    
    private Set< VkLayerProperties > getLayerProperties() {
        int[] layerCount = new int[1];
        VK10.vkEnumerateInstanceLayerProperties( layerCount, null );
        VkLayerProperties.Buffer layerBuffer = VkLayerProperties.create( layerCount[0] );
        VK10.vkEnumerateInstanceLayerProperties( layerCount, layerBuffer );
        Set< VkLayerProperties > set = new HashSet<>( layerCount[0] );
        layerBuffer.iterator().forEachRemaining( set::add );
        return set;
    }
    
    private Set< VkExtensionProperties > getExtensionProperties() {
        int[] propertyCount = new int[1];
        VK10.vkEnumerateInstanceExtensionProperties( ( ByteBuffer ) null, propertyCount, null );
        VkExtensionProperties.Buffer extensionsBuffer = VkExtensionProperties.create( propertyCount[0] );
        VK10.vkEnumerateInstanceExtensionProperties( ( ByteBuffer ) null, propertyCount, extensionsBuffer );
        Set< VkExtensionProperties > set = new HashSet<>( propertyCount[0] );
        extensionsBuffer.iterator().forEachRemaining( set::add );
        return set;
    }
    
    private PriorityQueue< VulkanPhysicalDevice > createPhysicalDevices() {
        int[] deviceCount = new int[1];
        VK10.vkEnumeratePhysicalDevices( instance, deviceCount, null );
        if ( deviceCount[0] == 0 ) {
            throw new NoDeviceSupportingVulkanException();
        }
        
        PointerBuffer devicesBuffer = PointerBuffer.allocateDirect( deviceCount[0] );
        VK10.vkEnumeratePhysicalDevices( instance, deviceCount, devicesBuffer );
        PriorityQueue< VulkanPhysicalDevice > queue = new PriorityQueue<>( VulkanPhysicalDevice::compareTo );
        while ( devicesBuffer.hasRemaining() ) {
            try {
                queue.add( new VulkanPhysicalDevice( devicesBuffer.get(),
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
        PointerBuffer instancePointer = MemoryStack.stackCallocPointer( 1 );
        int resultCode;
    
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
        PointerBuffer validationLayerNamesBuffer = MemoryUtil.memAllocPointer( validationLayerNames.size() );
        validationLayerNames.stream().map( MemoryUtil::memUTF8 ).forEach( validationLayerNamesBuffer::put );
        validationLayerNamesBuffer = validationLayerNamesBuffer.flip();
        return validationLayerNamesBuffer;
    }
    
    private PointerBuffer createEnabledExtensionsBuffer() {
        PointerBuffer enabledExtensionsBuffer = MemoryUtil.memAllocPointer( enabledExtensions.size() );
        enabledExtensions.values().forEach( enabledExtensionsBuffer::put );
        enabledExtensionsBuffer = enabledExtensionsBuffer.flip();
        return enabledExtensionsBuffer;
    }
    
    private VkApplicationInfo createApplicationInfo(
        String applicationName, int applicationVersion, String engineName, int engineVersion
    )
    {
        VkApplicationInfo vulkanInfo = VkApplicationInfo.create();
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
    
    private void setupDebugCallback() {
        callback = VkDebugReportCallbackEXT.create( ( flags, objectType, object, location, messageCode, pLayerPrefix, pMessage, pUserData ) -> {
            logger.finest( "Validation layer: " + MemoryUtil.memUTF8( pMessage ) );
            return VK10.VK_FALSE;
        } );
        VkDebugReportCallbackCreateInfoEXT createInfo = VkDebugReportCallbackCreateInfoEXT.create()
            .sType( EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT )
            .flags( EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT | EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT
                //| EXTDebugReport.VK_DEBUG_REPORT_INFORMATION_BIT_EXT
            )
            .pfnCallback( callback )
            .pNext( VK10.VK_NULL_HANDLE )
            .pUserData( VK10.VK_NULL_HANDLE );
        
        if ( instance.getCapabilities().vkCreateDebugReportCallbackEXT != VK10.VK_NULL_HANDLE ) {
            logger.fine( "Method vkCreateDebugReportCallbackEXT is supported." );
            VulkanHelper.assertCall(
                () -> EXTDebugReport.vkCreateDebugReportCallbackEXT( instance,
                    createInfo,
                    null,
                    new long[]{ callback.address() }
                ) ,
                resultCode ->
                    logger.warning( "Cannot set up debug callback. Error code: " + resultCode )
            );
        } else {
            logger.warning( "Method vkCreateDebugReportCallbackEXT is not supported!" );
        }
    }
    
    private void disposeDebugCallback() {
        if ( callback == null ) {
            logger.warning( "Debug Callback is a null pointer when closing it." );
            return;
        }
        if ( instance.getCapabilities().vkDestroyDebugReportCallbackEXT != VK10.VK_NULL_HANDLE ) {
            logger.fine( "Method vkDestroyDebugReportCallbackEXT is supported." );
            EXTDebugReport.vkDestroyDebugReportCallbackEXT( instance, callback.address(), null );
        } else {
            logger.warning( "Method vkDestroyDebugReportCallbackEXT is not supported!" );
        }
    }
    
}
