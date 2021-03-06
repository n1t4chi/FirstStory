/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.vulkan.VulkanAddress;
import com.firststory.firstoracle.vulkan.VulkanHelper;
import com.firststory.firstoracle.vulkan.VulkanWindowSurface;
import com.firststory.firstoracle.vulkan.allocators.VulkanDeviceAllocator;
import com.firststory.firstoracle.vulkan.exceptions.CannotCreateVulkanSwapChainException;
import com.firststory.firstoracle.vulkan.exceptions.SwapChainIsNotSupportedException;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class VulkanSwapChain {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanSwapChain.class );
    private final VulkanDeviceAllocator allocator;
    private final Set< VkSurfaceFormatKHR > formats = new HashSet<>();
    private final Map< Integer, VulkanSwapChainImage > images = new HashMap<>();
    private final Map< Integer, VulkanImageView > imageViews = new HashMap<>();
    private final VulkanPhysicalDevice device;
    private VkSurfaceCapabilitiesKHR capabilities;
    private VkSurfaceFormatKHR usedFormat;
    private int[] presentModes;
    private int usedPresentMode;
    private VkExtent2D extent;
    private int imageCount;
    private VkSwapchainCreateInfoKHR createInfo;
    private VulkanAddress address = VulkanAddress.createNull();
    
    public VulkanSwapChain(
        VulkanDeviceAllocator allocator,
        VulkanPhysicalDevice device
    ) {
        this.allocator = allocator;
        this.device = device;
    }
    
    public void dispose() {
        allocator.deregisterSwapChain( this );
    }
    
    public void disposeUnsafe() {
        clearImages();
        clearImageViews();
        if ( address.isNotNull() ) {
            KHRSwapchain.vkDestroySwapchainKHR( device.getLogicalDevice(), address.getValue(), null );
            address.setNull();
        }
    }
    
    @Override
    public String toString() {
        return "VulkanSwapChain@" + hashCode() + "[ " + "format:" + usedFormat.format() + ", colorSpace: " +
            usedFormat.colorSpace() + ", presentMode:" + usedPresentMode + ']';
    }
    
    public float getWidth() {
        return extent.width();
    }
    
    public float getHeight() {
        return extent.height();
    }
    
    public VkExtent2D getExtent() {
        return extent;
    }
    
    public int getImageFormat() {
        return usedFormat.format();
    }
    
    public VulkanAddress getAddress() {
        return address;
    }
    
    public void presentQueue( VulkanImageIndex index ) {
        var presentInfo = VkPresentInfoKHR.calloc()
            .sType( KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR )
            .pWaitSemaphores( MemoryUtil.memAllocLong( 1 ).put( 0, index.getRenderFinishedSemaphore().getAddressForWait().getValue() ) )
            .pSwapchains( MemoryUtil.memAllocLong( 1 ).put( 0, address.getValue() ) )
            .swapchainCount( 1 )
            .pImageIndices( MemoryUtil.memAllocInt( 1 ).put( 0, index.getIndex() ) )
            .pResults( null )
        ;
        KHRSwapchain.vkQueuePresentKHR( device.getGraphicFamily().getQueue() , presentInfo );
    }
    
    void update( VulkanWindowSurface windowSurface ) {
        disposeUnsafe();
        var physicalDevice = device.getPhysicalDevice();
        capabilities = createCapabilities( physicalDevice, windowSurface );
        refreshFormats( physicalDevice, windowSurface );
        presentModes = createPresentModes( physicalDevice, windowSurface );
        
        if ( !isSupported() ) {
            throw new SwapChainIsNotSupportedException( device );
        }
        
        usedFormat = selectSurfaceFormat();
        usedPresentMode = selectPresentMode();
        extent = selectCurrentExtent();
        imageCount = computeImageCount();
        createInfo = createSwapChainCreateInfo( windowSurface );
        address = updateSwapChainAddress();
        refreshVulkanImages();
        refreshImageViews();
    }
    
    Map< Integer, VulkanImageView > getImageViews() {
        return imageViews;
    }
    
    private void clearImageViews() {
        imageViews.values().forEach( VulkanImageView::dispose );
        imageViews.clear();
    }
    
    private boolean isSupported() {
        return !( formats.isEmpty() || presentModes.length == 0 );
    }
    
    private void refreshImageViews() {
        clearImageViews();
        images.forEach( ( index, image ) -> imageViews.put(
            index,
            image.createImageView( usedFormat.format(), VK10.VK_IMAGE_ASPECT_COLOR_BIT, 1 )
        ) );
    }
    
    private void refreshVulkanImages() {
        var count = new int[1];
        KHRSwapchain.vkGetSwapchainImagesKHR( device.getLogicalDevice(), address.getValue(), count, null );
        var addresses = new long[count[0]];
        clearImages();
        KHRSwapchain.vkGetSwapchainImagesKHR( device.getLogicalDevice(), address.getValue(), count, addresses );
        for ( var i = 0; i < addresses.length; i++ ) {
            images.put( i, allocator.createSwapChainImage( new VulkanAddress( addresses[i] ), i ) );
        }
    }
    
    private void clearImages() {
        images.forEach( ( integer, vulkanSwapChainImage ) -> vulkanSwapChainImage.dispose() );
        images.clear();
    }
    
    private VulkanAddress updateSwapChainAddress() {
        return VulkanHelper.updateAddress(
            address,
            addressA -> KHRSwapchain.vkCreateSwapchainKHR(
                device.getLogicalDevice(), createInfo, null, addressA ),
            resultCode -> new CannotCreateVulkanSwapChainException( device, resultCode )
        );
    }
    
    private VkSwapchainCreateInfoKHR createSwapChainCreateInfo( VulkanWindowSurface windowSurface ) {
        var createInfo = VkSwapchainCreateInfoKHR.calloc()
            .sType( KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR )
            .surface( windowSurface.getAddress().getValue() )
            .minImageCount( imageCount )
            .imageFormat( usedFormat.format() )
            .imageColorSpace( usedFormat.colorSpace() )
            .imageExtent( extent )
            .imageArrayLayers( 1 )
            //.imageUsage( VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT ) todo: could be used for post processing
            .imageUsage( VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT )
            .preTransform( capabilities.currentTransform() )
            .compositeAlpha( KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR )
            .presentMode( usedPresentMode )
            .clipped( true )
            .oldSwapchain( VK10.VK_NULL_HANDLE );
        
        if ( device.isSingleQueueFamilyUsed() ) {
            createInfo.imageSharingMode( VK10.VK_SHARING_MODE_EXCLUSIVE );
            createInfo.pQueueFamilyIndices( null );
        } else {
            createInfo.imageSharingMode( VK10.VK_SHARING_MODE_CONCURRENT );
            createInfo.pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() );
        }
        return createInfo;
    }
    
    private int computeImageCount() {
        var imageCount = capabilities.minImageCount() + 1;
        if ( capabilities.maxImageCount() > 0 ) {
            imageCount = capabilities.maxImageCount();
        }
        return imageCount;
    }
    
    private VkExtent2D selectCurrentExtent() {
        return capabilities.currentExtent();
    }
    
    private int selectPresentMode() {
        if ( isModeAvailable( KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR ) ) {
//            logger.finest( device + ": Using mailbox presentation mode" );
            return KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
        }
        if ( isModeAvailable( KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR ) ) {
//            logger.finest( device + ": Using immediate presentation mode" );
            return KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
        }
        if ( isModeAvailable( KHRSurface.VK_PRESENT_MODE_FIFO_KHR ) ) {
//            logger.finest( device + ": Using fifo presentation mode" );
            return KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
        }
        logger.warning( device + ": Could not select preferred mode. Using first available." );
        return presentModes[0];
    }
    
    private boolean isModeAvailable( int preferredMode ) {
        for ( var mode : presentModes ) {
            if ( mode == preferredMode ) {
                return true;
            }
        }
        return false;
    }
    
    private VkSurfaceFormatKHR selectSurfaceFormat() {
        VkSurfaceFormatKHR selectedFormat;
    
        var collect = formats.stream()
            .filter( format -> format.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR )
            .collect( Collectors.toSet() );
        
        selectedFormat = collect.stream()
            .filter( format -> format.format() == VK10.VK_FORMAT_B8G8R8A8_UNORM )
            .findAny()
            .orElse( null );
        
        if ( PropertiesUtil.isPropertyTrue( PropertiesUtil.VULKAN_USE_SRGB_PROPERTY ) ) {
            selectedFormat = collect.stream()
                .filter( format -> format.format() == VK10.VK_FORMAT_B8G8R8A8_SRGB )
                .findAny()
                .orElse( selectedFormat );
            
            if ( selectedFormat != null && selectedFormat.format() != VK10.VK_FORMAT_B8G8R8A8_SRGB ) {
                logger.warning( device + " No sRGB format detected. Using linear RGB." );
            }
        }
        
        if ( selectedFormat == null ) {
            logger.warning( device + " No suitable surface format detected. Using first available." );
            selectedFormat = formats.iterator().next();
        }
        
        return selectedFormat;
    }
    
    private int[] createPresentModes( VkPhysicalDevice physicalDevice, VulkanWindowSurface windowSurface ) {
        var presentModeCount = new int[1];
        KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR( physicalDevice,
            windowSurface.getAddress().getValue(),
            presentModeCount,
            null
        );
        var presentModes = new int[presentModeCount[0]];
        KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR( physicalDevice,
            windowSurface.getAddress().getValue(),
            presentModeCount,
            presentModes
        );
        return presentModes;
    }
    
    private void refreshFormats( VkPhysicalDevice physicalDevice, VulkanWindowSurface windowSurface ) {
        var formatCount = new int[1];
        KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR( physicalDevice,
            windowSurface.getAddress().getValue(),
            formatCount,
            null
        );
        var formatsBuffer = VkSurfaceFormatKHR.calloc( formatCount[0] );
        KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR( physicalDevice,
            windowSurface.getAddress().getValue(),
            formatCount,
            formatsBuffer
        );
        formats.clear();
        formatsBuffer.forEach( formats::add );
    }
    
    private VkSurfaceCapabilitiesKHR createCapabilities(
        VkPhysicalDevice physicalDevice,
        VulkanWindowSurface windowSurface
    ) {
        var capabilities = VkSurfaceCapabilitiesKHR.calloc();
        KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR( physicalDevice,
            windowSurface.getAddress().getValue(),
            capabilities
        );
        return capabilities;
    }
}
