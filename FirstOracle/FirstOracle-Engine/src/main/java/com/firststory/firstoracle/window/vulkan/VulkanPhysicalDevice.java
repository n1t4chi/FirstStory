/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.*;
import java.util.function.Predicate;
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
    private final List< VulkanQueueFamily > availableQueueFamilies;
    private final Set< VulkanQueueFamily > usedQueueFamilies = new HashSet<>();
    
    private final VulkanQueueFamily graphicFamily;
    private final VulkanQueueFamily presentationFamily;
    private final VkDevice logicalDevice;
    private final VkQueue presentationQueue;
    private final VulkanWindowSurface windowSurface;
    
    VulkanPhysicalDevice(
        long deviceAddress,
        VkInstance instance,
        PointerBuffer validationLayerNamesBuffer,
        VulkanWindowSurface surface
    ) {
        windowSurface = surface;
        physicalDevice = new VkPhysicalDevice( deviceAddress, instance );
        capabilities = physicalDevice.getCapabilities();
        features = createDeviceFeatures();
        properties = createDeviceProperties();
        availableQueueFamilies = getAvailableQueueFamilies();
        graphicFamily = selectGraphicFamily();
        usedQueueFamilies.add( graphicFamily );
        logger.finer( this+" selected graphic family: "+ graphicFamily );
        presentationFamily = selectPresentationFamily();
        usedQueueFamilies.add( presentationFamily );
        logger.finer( this+" selected presentation family: "+ presentationFamily );
        VkDeviceCreateInfo createInfo = createDeviceCreateInfo();
        if( VulkanFramework.validationLayersAreEnabled() ) {
            createInfo.ppEnabledLayerNames( validationLayerNamesBuffer );
        }
        logicalDevice = createLogicalDevice( createInfo );
        presentationQueue = createPresentationQueue();
        
        logger.finer( "finished creating "+this );
    }
    
    private VkDeviceCreateInfo createDeviceCreateInfo() {
        return VkDeviceCreateInfo.create()
            .sType( VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO )
            .pNext( VK10.VK_NULL_HANDLE )
            .pQueueCreateInfos( createQueueFamilyBuffer() );
    }
    
    private VkQueue createPresentationQueue() {
        PointerBuffer queuePointer = MemoryStack.stackMallocPointer( 1 );
        VK10.vkGetDeviceQueue( logicalDevice, presentationFamily.getIndex(), 0, queuePointer );
        return new VkQueue( queuePointer.get(), logicalDevice );
    }
    
    private VkDeviceQueueCreateInfo.Buffer createQueueFamilyBuffer() {
        VkDeviceQueueCreateInfo.Buffer buffer = createVkDeviceQueueCreateInfoBuffer( usedQueueFamilies.size() );
        for ( VulkanQueueFamily usedQueueFamily : usedQueueFamilies ) {
            VkDeviceQueueCreateInfo queueInfo = VkDeviceQueueCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO )
                .queueFamilyIndex( usedQueueFamily.getIndex() )
                .pQueuePriorities( MemoryStack.stackFloats( 1.0f ) )
            ;
            buffer.put( queueInfo );
        }
        buffer.flip();
        return buffer;
    }
    
    private VkDeviceQueueCreateInfo.Buffer createVkDeviceQueueCreateInfoBuffer( int count ) {
        return new VkDeviceQueueCreateInfo.Buffer(
            BufferUtils.createByteBuffer( count * VkDeviceQueueCreateInfo.SIZEOF )
        );
    }
    
    private VkPhysicalDeviceFeatures createDeviceFeatures() {
        VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.create();
        VK10.vkGetPhysicalDeviceFeatures( physicalDevice, features );
        return features;
    }
    
    private VkPhysicalDeviceProperties createDeviceProperties() {
        VkPhysicalDeviceProperties properties =  VkPhysicalDeviceProperties.create();
        VK10.vkGetPhysicalDeviceProperties( physicalDevice, properties );
        return properties;
    }
    
    private VkDevice createLogicalDevice( VkDeviceCreateInfo createInfo ) {
        PointerBuffer devicePointer = MemoryUtil.memAllocPointer(1);
        if ( VK10.vkCreateDevice( physicalDevice, createInfo, null, devicePointer ) != VK10.VK_SUCCESS ) {
            throw new CannotCreateVulkanLogicDeviceException( physicalDevice );
        }
        return new VkDevice( devicePointer.get(), physicalDevice, createInfo );
    }
    
    @Override
    public void close() {
        VK10.vkDestroyDevice( logicalDevice, null );
    }
    
    private VulkanQueueFamily selectGraphicFamily() {
        return selectSuitableFamily(
            family -> family.isFlagSet( VK10.VK_QUEUE_GRAPHICS_BIT ),
            (first,second) -> VulkanQueueFamily.compare( first,second )
        );
    }
    
    private VulkanQueueFamily selectPresentationFamily() {
        return selectSuitableFamily(
            family -> {
                int[] supports = new int[1];
                KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR( physicalDevice, family.getIndex(), windowSurface.getAddress() , supports);
                return supports[0] == VK10.VK_SUCCESS;
            },
            (first,second) -> VulkanQueueFamily.compare( first,second )
        );
    }
    
    private VulkanQueueFamily selectSuitableFamily(
        Predicate<VulkanQueueFamily> familyChecker,
        Comparator<VulkanQueueFamily> familyComparator
    ) {
        VulkanQueueFamily selectedFamily = null;
        for ( VulkanQueueFamily family : availableQueueFamilies ) {
            if( familyChecker.test( family ) && familyComparator.compare( selectedFamily, family ) < 0 ) {
                selectedFamily = family;
            }
        }
        return selectedFamily;
    }
    
    private List< VulkanQueueFamily > getAvailableQueueFamilies() {
        int[] queueFamilyCount = new int[1];
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, null );
        VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = VkQueueFamilyProperties.create( queueFamilyCount[0] );
        VK10.vkGetPhysicalDeviceQueueFamilyProperties( physicalDevice, queueFamilyCount, queueFamilyPropertiesBuffer );
        List< VulkanQueueFamily > queueFamilyProperties = new ArrayList<>(queueFamilyCount[0]);
        int index =0;
        while( queueFamilyPropertiesBuffer.hasRemaining() ){
            VulkanQueueFamily family = new VulkanQueueFamily( queueFamilyPropertiesBuffer.get(), index++ );
            queueFamilyProperties.add( family );
            logger.finer( this+" -> "+family );
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
    
    @Override
    public String toString() {
        return "VulkanPhysicalDevice@"+hashCode();
    }
}
