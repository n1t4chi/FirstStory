/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.window.WindowContext;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanImageViewException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanSwapChainException;
import com.firststory.firstoracle.window.vulkan.exceptions.CannotCreateVulkanWindowSurfaceException;
import com.firststory.firstoracle.window.vulkan.exceptions.SwapChainIsNotSupportedException;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.*;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author n1t4chi
 */
public class VulkanWindowSurface {
    
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanWindowSurface.class );
    
    public static VulkanWindowSurface create( VkInstance instance, WindowContext window ) {
        long[] pSurface = new long[1];
        if ( GLFWVulkan.glfwCreateWindowSurface( instance, window.getAddress(), null, pSurface ) != VK10.VK_SUCCESS ) {
            throw new CannotCreateVulkanWindowSurfaceException( instance, window );
        }
        return new VulkanWindowSurface( pSurface[0] );
    }
    
    private final long address;
    
    private VulkanWindowSurface( long address ) {
        this.address = address;
    }
    
    public void close( VkInstance instance ) {
        KHRSurface.vkDestroySurfaceKHR( instance, address, null );
    }
    
    VulkanSwapChain createSwapChain( VulkanPhysicalDevice physicalDevice ) {
        return new VulkanSwapChain( physicalDevice );
    }
    
    long getAddress() {
        return address;
    }
    
    class VulkanSwapChain implements AutoCloseable {
        
        private final VkSurfaceCapabilitiesKHR capabilities;
        private final Set< VkSurfaceFormatKHR > formats;
        private final VkSurfaceFormatKHR usedFormat;
        private final int[] presentModes;
        private final int usedPresentMode;
        private final VulkanPhysicalDevice device;
        private final VkExtent2D extent;
        private final int imageCount;
        private final VkSwapchainCreateInfoKHR createInfo;
        private final long swapChain;
        private final Set< VulkanImage > images;
        private final Set< VulkanImageView > imageViews;
    
        private VulkanSwapChain( VulkanPhysicalDevice device ) {
            this.device = device;
            VkPhysicalDevice physicalDevice = device.getPhysicalDevice();
            
            capabilities = createCapabilities( physicalDevice );
            formats = createFormats( physicalDevice );
            presentModes = createPresentModes( physicalDevice );
            
            if ( !isSupported() ) {
                throw new SwapChainIsNotSupportedException( device );
            }
            
            usedFormat = selectSurfaceFormat();
            usedPresentMode = selectPresentMode();
            extent = selectCurrentExtent();
            imageCount = computeImageCount();
            createInfo = createSwapChainCreateInfo();
            swapChain = createSwapChain();
            images = createVulkanImages();
            imageViews = createImageViews();
            
        }
    
        @Override
        public void close() {
            imageViews.forEach( VulkanImageView::close );
            KHRSwapchain.vkDestroySwapchainKHR( device.getLogicalDevice(), swapChain, null );
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
    
        boolean isSupported() {
            return !( formats.isEmpty() || presentModes.length == 0 );
        }
    
        private Set< VulkanImageView > createImageViews() {
            Set<VulkanImageView > imageViews = new HashSet<>( images.size() );
            images.forEach( image -> {
                imageViews.add( createVulkanImageView( image ) );
                
            } );
            return imageViews;
        }
    
        private VulkanImageView createVulkanImageView( VulkanImage image ) {
            VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.create()
                .sType( VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO )
                .image( image.address )
                .viewType( VK10.VK_IMAGE_VIEW_TYPE_2D )
                .format( usedFormat.format() )
                .components( VkComponentMapping.create()
                    .a( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .r( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .g( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                    .b( VK10.VK_COMPONENT_SWIZZLE_IDENTITY )
                )
                .subresourceRange( VkImageSubresourceRange.create()
                    .aspectMask( VK10.VK_IMAGE_ASPECT_COLOR_BIT )
                    .baseMipLevel( 0 )
                    .levelCount( 1 )
                    .baseArrayLayer( 0 )
                    .layerCount( 1 )
                )
            ;
            long[] address = new long[1];
            if( VK10.vkCreateImageView( this.device.getLogicalDevice(), createInfo, null, address )
                != VK10.VK_SUCCESS
            ) {
                throw new CannotCreateVulkanImageViewException(this.device);
            }
            return new VulkanImageView( address[0] );
        }
    
        private Set< VulkanImage > createVulkanImages() {
            int[] count = new int[1];
            KHRSwapchain.vkGetSwapchainImagesKHR( device.getLogicalDevice(), swapChain, count, null );
            long[] addresses = new long[count[0]];
            Set<VulkanImage > images = new HashSet<>( count[0] );
            KHRSwapchain.vkGetSwapchainImagesKHR( device.getLogicalDevice(), swapChain, count, addresses );
            for ( long address : addresses ) {
                 images.add( new VulkanImage( address ) );
            }
            return images;
        }
    
        private long createSwapChain() {
            long[] swapChainAddress = new long[1];
            if( KHRSwapchain.vkCreateSwapchainKHR( device.getLogicalDevice(), createInfo, null, swapChainAddress ) != VK10.VK_SUCCESS ) {
                throw new CannotCreateVulkanSwapChainException( device );
            }
            return swapChainAddress[0];
        }
    
        private VkSwapchainCreateInfoKHR createSwapChainCreateInfo() {
            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.create()
                .sType( KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR )
                .surface( address )
                .minImageCount( imageCount )
                .imageFormat( usedFormat.format() )
                .imageColorSpace( usedFormat.colorSpace() )
                .imageExtent( extent )
                .imageArrayLayers( 1 )
                //.imageUsage( VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT ) todo: ould be used for post processing
                .imageUsage( VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT )
                .preTransform( capabilities.currentTransform() )
                .compositeAlpha( KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR )
                .presentMode( usedPresentMode )
                .clipped( true )
                .oldSwapchain( VK10.VK_NULL_HANDLE )
            ;
        
            if( device.isSingleQueueFamilyUsed() ){
                createInfo.imageSharingMode( VK10.VK_SHARING_MODE_EXCLUSIVE );
                createInfo.pQueueFamilyIndices( null );
            } else {
                createInfo.imageSharingMode( VK10.VK_SHARING_MODE_CONCURRENT );
                createInfo.pQueueFamilyIndices( device.createQueueFamilyIndicesBuffer() );
            }
            return createInfo;
        }
    
        private int computeImageCount() {
            int imageCount = capabilities.minImageCount() + 1;
            if ( capabilities.maxImageCount() > 0 ) {
                imageCount = capabilities.maxImageCount();
            }
            return imageCount;
        }        @Override
        public String toString() {
            return "VulkanSwapChain@"+hashCode()+"[ " +
                "format:" + usedFormat.format() +
                ", colorSpace: " + usedFormat.colorSpace() +
                ", presentMode:" + usedPresentMode +
            ']';
        }
    
        private VkExtent2D selectCurrentExtent() {
            return capabilities.currentExtent();
        }
    
        private int selectPresentMode() {
            if( isModeAvailable( KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR ) ) {
                logger.finer(device+": Using mailbox presentation mode");
                return KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR;
            }
            if( isModeAvailable( KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR ) ) {
                logger.finer(device+": Using immediate presentation mode");
                return KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
            }
            if( isModeAvailable( KHRSurface.VK_PRESENT_MODE_FIFO_KHR ) ) {
                logger.finer(device+": Using fifo presentation mode");
                return KHRSurface.VK_PRESENT_MODE_FIFO_KHR;
            }
            logger.warning(device+": Could not select preferred mode. Using first available.");
            return presentModes[0];
        }
    
        private boolean isModeAvailable( int preferedMode ) {
            for( int mode : presentModes ) {
                if( mode == preferedMode ) {
                    return true;
                }
            }
            return false;
        }
    
        private VkSurfaceFormatKHR selectSurfaceFormat() {
            VkSurfaceFormatKHR selectedFormat;
            
            Set< VkSurfaceFormatKHR > collect = formats.stream()
                .filter( format -> format.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR )
                .collect( Collectors.toSet() );
            
            selectedFormat = collect.stream()
                .filter( format -> format.format() == VK10.VK_FORMAT_B8G8R8A8_UNORM )
                .findAny()
                .orElse( null );
            
            if( PropertiesUtil.isPropertyTrue( PropertiesUtil.VULKAN_USE_SRGB_PROPERTY ) ) {
                selectedFormat = collect.stream()
                    .filter( format -> format.format() == VK10.VK_FORMAT_B8G8R8A8_SRGB )
                    .findAny()
                    .orElse( selectedFormat );
                
                if ( selectedFormat != null && selectedFormat.format() != VK10.VK_FORMAT_B8G8R8A8_SRGB  ) {
                    logger.warning( device+" No sRGB format detected. Using linear RGB." );
                }
            }
    
            if ( selectedFormat == null ) {
                logger.warning( device+" No suitable surface format detected. Using first available." );
                selectedFormat = formats.iterator().next();
            }
            
            return selectedFormat;
        }
        
        private int[] createPresentModes( VkPhysicalDevice physicalDevice ) {
            int[] presentModeCount = new int[1];
            KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR( physicalDevice, address, presentModeCount, null );
            int[] presentModes = new int[presentModeCount[0]];
            KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR( physicalDevice,
                address,
                presentModeCount,
                presentModes
            );
            return presentModes;
        }
        
        private Set< VkSurfaceFormatKHR > createFormats( VkPhysicalDevice physicalDevice ) {
            int[] formatCount = new int[1];
            KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR( physicalDevice, address, formatCount, null );
            VkSurfaceFormatKHR.Buffer formatsBuffer = VkSurfaceFormatKHR.create( formatCount[0] );
            KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR( physicalDevice, address, formatCount, formatsBuffer );
            Set< VkSurfaceFormatKHR > formats = new HashSet<>( formatCount[0] );
            formatsBuffer.forEach( formats::add );
            return formats;
        }
        
        private VkSurfaceCapabilitiesKHR createCapabilities( VkPhysicalDevice physicalDevice ) {
            VkSurfaceCapabilitiesKHR capabilities = VkSurfaceCapabilitiesKHR.create();
            KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR( physicalDevice, address, capabilities );
            return capabilities;
        }
        
        private class VulkanImage {
            private final long address;
    
            VulkanImage( long address ) {
                this.address = address;
            }
    
            @Override
            public int hashCode() {
                return ( int ) ( address ^ ( address >>> 32 ) );
            }            @Override
            public boolean equals( Object o ) {
                if ( this == o ) { return true; }
                if ( o == null || getClass() != o.getClass() ) { return false; }
        
                VulkanImage that = ( VulkanImage ) o;
    
                return address == that.address;
            }
    

        }
    
        private class VulkanImageView implements AutoCloseable {
            private final long address;
    
            VulkanImageView( long address ) {
                this.address = address;
            }
    
            @Override
            public boolean equals( Object o ) {
                if ( this == o ) { return true; }
                if ( o == null || getClass() != o.getClass() ) { return false; }
        
                VulkanImage that = ( VulkanImage ) o;
        
                return address == that.address;
            }
    
            @Override
            public int hashCode() {
                return ( int ) ( address ^ ( address >>> 32 ) );
            }
    
            @Override
            public void close() {
                VK10.vkDestroyImageView( device.getLogicalDevice(), address, null );
            }
        }
    

    }
}
