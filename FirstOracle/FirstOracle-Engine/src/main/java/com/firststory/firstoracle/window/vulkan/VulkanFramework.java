/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.ArrayBufferLoader;
import com.firststory.firstoracle.data.TextureBufferLoader;
import com.firststory.firstoracle.object.VertexAttributeLoader;
import com.firststory.firstoracle.rendering.RenderingCommands;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VulkanFramework implements RenderingFramework {
    private static Logger logger = FirstOracleConstants.getLogger( VulkanFramework.class );
    
    private final VkApplicationInfo applicationInfo;
    private final VkInstanceCreateInfo createInfo;
    private final VkInstance instance;
    
    static {
        if( !GLFWVulkan.glfwVulkanSupported() ){
            throw new RuntimeException( "GLFW Vulkan not supported!" );
        }
    }
    
    private final Set< VkExtensionProperties > extensionProperties;
    private final PriorityQueue< VulkanPhysicalDevice > physicalDevices;
    private final Map< String, Long > enabledExtensions;
    private final Set< VkLayerProperties > layerProperties;
    private final Set< String > validationLayerNames;
    
    VulkanFramework() {
        String applicationName = "ApplicationName";
        int applicationVersion = VK10.VK_MAKE_VERSION(
            FirstOracleConstants.FIRST_STORY_VERSION_MAJOR,
            FirstOracleConstants.FIRST_STORY_VERSION_MINOR,
            FirstOracleConstants.FIRST_STORY_VERSION_PATCH
        );
        String engineName = "EngineName";
        int engineVersion = VK10.VK_MAKE_VERSION(
            FirstOracleConstants.FIRST_ORACLE_VERSION_MAJOR,
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
        physicalDevices = createPhysicalDevices();
    
        long address = 0;
        VkDebugReportCallbackEXT callback = VkDebugReportCallbackEXT.create( ( flags, objectType, object, location, messageCode, pLayerPrefix, pMessage, pUserData ) -> {
            System.err.println( "Validation layer: "+MemoryUtil.memUTF8( pMessage ) );
            return VK10.VK_FALSE;
        } );
        VkDebugReportCallbackCreateInfoEXT createInfo = VkDebugReportCallbackCreateInfoEXT.create()
            .sType( EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT )
            .flags( EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT | EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT )
            .pfnCallback( callback );
        if( instance.getCapabilities().vkCreateDebugReportCallbackEXT != VK10.VK_NULL_HANDLE ){
            EXTDebugReport.vkCreateDebugReportCallbackEXT( instance, createInfo, null, new long[]{callback.address()} );
        } else {
            logger.warning( "Method vkCreateDebugReportCallbackEXT is not supported!" );
        }
    }
    
    private Map<String,Long> createEnabledExtensions() {
        Map<String,Long> extensions = new HashMap<>(  );
        addGlfwInstanceExtensions( extensions );
        if( FirstOracleConstants.isPropertyTrue( FirstOracleConstants.VULKAN_VALIDATION_LAYERS_ENABLED_PROPERTY ) ) {
            addExtension( extensions, EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME );
        }
        return extensions;
    }
    
    private Set< String > createValidationLayerNames( ) {
        Set< String > layerNames = layerProperties.stream()
            .map( VkLayerProperties::layerNameString )
            .collect(Collectors.toSet());
        List< String > validationLayers = FirstOracleConstants.getListFromProperty(
            FirstOracleConstants.VULKAN_VALIDATION_LAYERS_LIST_PROPERTY );
        
        for ( Iterator< String > iterator = validationLayers.iterator(); iterator.hasNext(); ) {
            String layer = iterator.next();
            if ( !layerNames.contains( layer ) ) {
                iterator.remove();
                logger.warning( "Validation layer: "+layer+" is not available." );
            }else{
            }
        }
        return layerNames;
    }
    
    private void addExtension( Map< String, Long > enabledExtensions, String extensionName ) {
        enabledExtensions.put( extensionName,
            MemoryUtil.memAddress( MemoryStack.stackUTF8( extensionName ) ) );
    }
    
    private void addGlfwInstanceExtensions( Map< String, Long > enabledExtensions ) {
        PointerBuffer glfwInstanceExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
        while( glfwInstanceExtensions.hasRemaining()){
            long address = glfwInstanceExtensions.get();
            enabledExtensions.put( MemoryUtil.memUTF8( address ), address );
        }
    }
    
    private Set< VkLayerProperties > getLayerProperties() {
        int[] layerCount = new int[1];
        VK10.vkEnumerateInstanceLayerProperties( layerCount, null );
        VkLayerProperties.Buffer layerBuffer = VkLayerProperties.create( layerCount[0] );
        VK10.vkEnumerateInstanceLayerProperties( layerCount, layerBuffer );
        Set<VkLayerProperties> set = new HashSet<>( layerCount[0] );
        layerBuffer.iterator().forEachRemaining( set::add );
        return set;
    }
    
    private Set<VkExtensionProperties> getExtensionProperties() {
        int[] propertyCount = new int[1];
        VK10.vkEnumerateInstanceExtensionProperties(
            ( ByteBuffer ) null, propertyCount, null );
        VkExtensionProperties.Buffer extensionsBuffer = VkExtensionProperties.create( propertyCount[0] );
        VK10.vkEnumerateInstanceExtensionProperties(
            ( ByteBuffer ) null, propertyCount, extensionsBuffer );
        Set<VkExtensionProperties> set = new HashSet<>( propertyCount[0] );
        extensionsBuffer.iterator().forEachRemaining( set::add );
        return set;
    }
    
    private PriorityQueue< VulkanPhysicalDevice > createPhysicalDevices() {
        int[] deviceCount = new int[1];
        VK10.vkEnumeratePhysicalDevices( instance, deviceCount, null);
        if( deviceCount[0] == 0 ) {
            throw new NoGpuSupportingVulkanException();
        }
        PointerBuffer devicesBuffer = PointerBuffer.allocateDirect( deviceCount[0] );
        VK10.vkEnumeratePhysicalDevices( instance, deviceCount, devicesBuffer );
        PriorityQueue<VulkanPhysicalDevice> queue = new PriorityQueue<>( VulkanPhysicalDevice::compareTo );
        while( devicesBuffer.hasRemaining() ){
            VulkanPhysicalDevice device = new VulkanPhysicalDevice( devicesBuffer.get(), instance );
            if( device.isSuitable() ) {
                queue.add( device );
            }
        }
        if(queue.isEmpty()){
            throw new NoGpuSupportingVulkanEnoughException();
        }
        return queue;
    }
    
    private VkInstance createInstance() {
        PointerBuffer instancePointer = MemoryStack.stackCallocPointer( 1 );
        if ( VK10.vkCreateInstance( createInfo, null, instancePointer ) != VK10.VK_SUCCESS ) {
            throw new CannotCreateVulkanInstanceException();
        }
        return new VkInstance( instancePointer.get(), createInfo );
    }
    
    private VkInstanceCreateInfo createCreateInfo() {
        VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.create();
        
        PointerBuffer enabledExtensionsBuffer = MemoryUtil.memAllocPointer( enabledExtensions.size() );
        enabledExtensions.values().forEach( enabledExtensionsBuffer::put );
        enabledExtensionsBuffer = enabledExtensionsBuffer.flip();
    
        PointerBuffer validationLayerNamesBuffer = MemoryUtil.memAllocPointer( validationLayerNames.size() );
        validationLayerNames.stream().map( MemoryUtil::memUTF8 ).forEach( validationLayerNamesBuffer::put );
        validationLayerNamesBuffer = validationLayerNamesBuffer.flip();
        
        createInfo.set( VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO,
            VK10.VK_NULL_HANDLE,
            FirstOracleConstants.NO_FLAGS,
            applicationInfo,
            validationLayerNamesBuffer,
            enabledExtensionsBuffer
        );
        return createInfo;
    }
    
    private VkApplicationInfo createApplicationInfo(
        String applicationName, int applicationVersion, String engineName, int engineVersion
    ) {
        VkApplicationInfo vulkanInfo = VkApplicationInfo.create();
        vulkanInfo.set( VK10.VK_STRUCTURE_TYPE_APPLICATION_INFO,
            VK10.VK_NULL_HANDLE,
            MemoryStack.stackASCII( applicationName ),
            applicationVersion,
            MemoryStack.stackASCII( engineName ),
            engineVersion,
            VK10.VK_API_VERSION_1_0
        );
        return vulkanInfo;
    }
    
    @Override
    public RenderingContext getRenderingContext() {
        return null;
    }
    
    @Override
    public ShaderProgram2D getShader2D() {
        return null;
    }
    
    @Override
    public ShaderProgram3D getShader3D() {
        return null;
    }
    
    @Override
    public TextureBufferLoader getTextureLoader() {
        return null;
    }
    
    @Override
    public VertexAttributeLoader getAttributeLoader() {
        return null;
    }
    
    @Override
    public ArrayBufferLoader getBufferLoader() {
        return null;
    }
    
    @Override
    public void clearScreen() {
    
    }
    
    @Override
    public void updateViewPort( int x, int y, int width, int height ) {
    
    }
    
    @Override
    public void setCurrentCapabilitesToThisThread() {
    
    }
    
    @Override
    public void invoke( RenderingCommands commands ) throws Exception {
    
    }
    
    @Override
    public void compileShaders() throws IOException {
    
    }
    
    @Override
    public void close() {
        VK10.vkDestroyInstance( instance, null );
    }
    
}
