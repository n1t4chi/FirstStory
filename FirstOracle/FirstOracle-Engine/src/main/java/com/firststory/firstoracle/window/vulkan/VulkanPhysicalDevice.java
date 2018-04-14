/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
class VulkanPhysicalDevice implements Comparable<VulkanPhysicalDevice>, AutoCloseable {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanPhysicalDevice.class );
    
    private final VkPhysicalDevice physicalDevice;
    private final VkPhysicalDeviceFeatures features;
    private final VkPhysicalDeviceProperties properties;
    private final VKCapabilitiesInstance capabilities;
    private final List< VulkanQueueFamily > queueFamilies;
    
    private final VulkanQueueFamily graphicFamily;
    private final VkDevice logicalDevice;
    
    VulkanPhysicalDevice( long deviceAddress, VkInstance instance, PointerBuffer validationLayerNamesBuffer ) {
        physicalDevice = new VkPhysicalDevice( deviceAddress, instance );
        capabilities = physicalDevice.getCapabilities();
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.create();
        VK10.vkGetPhysicalDeviceFeatures( physicalDevice, features );
        this.features = features;
    
        VkPhysicalDeviceProperties properties =  VkPhysicalDeviceProperties.create();
        VK10.vkGetPhysicalDeviceProperties( physicalDevice, properties );
        this.properties = properties;
        queueFamilies = getQueueFamilies();
        graphicFamily = getGraphicFamily();
        logger.finer( this+" selected graphic family: "+ getGraphicFamily() );
    
        //VkDevice logicalDevice = new VkDevice(  );
        
        VkDeviceQueueCreateInfo.Buffer queueCreateInfo = VkDeviceQueueCreateInfo.calloc( 1 )
            .sType( VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO )
            .queueFamilyIndex( graphicFamily.getIndex() )
            .pQueuePriorities( MemoryStack.stackFloats( 1.0f ) )
        ;
        VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .pQueueCreateInfos( queueCreateInfo )
        ;
            
        if( VulkanFramework.validationLayersAreEnabled() ) {
            createInfo.ppEnabledLayerNames( validationLayerNamesBuffer );
        }
    
        PointerBuffer pDevice = null;
        try {
            pDevice = MemoryUtil.memAllocPointer(1);
            if ( VK10.vkCreateDevice( physicalDevice, createInfo, null, pDevice ) != VK10.VK_SUCCESS ) {
                throw new CannotCreateVulkanLogicDeviceException( physicalDevice );
            }
            logicalDevice = new VkDevice( pDevice.get(), physicalDevice, createInfo );
        }finally {
            if(pDevice != null){
                pDevice.free();
            }
        }
    }
    
    @Override
    public void close() {
        VK10.vkDestroyDevice( logicalDevice, null );
    }
    
    private VulkanQueueFamily getGraphicFamily() {
        VulkanQueueFamily graphicFamily = null;
        for ( VulkanQueueFamily family : queueFamilies ) {
            
            int flag = VK10.VK_QUEUE_GRAPHICS_BIT;
            logger.finer( this+" -> "+family );
            if( family.isFlagSet( flag ) && family.isBetterThan( graphicFamily ) ) {
                graphicFamily = family;
            }
        }
        return graphicFamily;
    }
    
    private List< VulkanQueueFamily > getQueueFamilies() {
        int[] queueFamilyCount = new int[1];
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, null );
        VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = VkQueueFamilyProperties.create( queueFamilyCount[0] );
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, queueFamilyPropertiesBuffer );
        List< VulkanQueueFamily > queueFamilyProperties = new ArrayList<>(queueFamilyCount[0]);
        int index =0;
        while( queueFamilyPropertiesBuffer.hasRemaining() ){
            queueFamilyProperties.add( new VulkanQueueFamily( queueFamilyPropertiesBuffer.get(), index++ ) );
        }
        return queueFamilyProperties;
    }
    
    @Override
    public int compareTo( VulkanPhysicalDevice o ) {
        return Long.compare( getScore(), o.getScore() );
    }
    
    boolean isSuitable() {
        return properties.limits().maxMemoryAllocationCount() > 32 ;
    }
    
    long getScore(){
        return (
            properties.limits().maxMemoryAllocationCount() +
            properties.limits().maxImageDimension2D()
        ) * typeMultiplier();
    }
    
    private long typeMultiplier() {
        switch(properties.deviceType()){
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
