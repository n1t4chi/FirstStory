/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.PropertiesUtil;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
class VulkanDebugCallback {
    private static final Logger logger = FirstOracleConstants.getLogger( VulkanDebugCallback.class );
    private final VulkanFrameworkAllocator allocator;
    private final VkInstance instance;
    private final VulkanAddress debugCallbackAddress = new VulkanAddress();
    
    void dispose() {
        allocator.deregisterDebugCallback( this );
    }
    
    VulkanDebugCallback( VulkanFrameworkAllocator allocator, VkInstance instance ) {
        this.allocator = allocator;
        this.instance = instance;
        if( PropertiesUtil.isDebugMode() ) {
            var callback = VkDebugReportCallbackEXT.create( new DebugLogger() );
            var createInfo = VkDebugReportCallbackCreateInfoEXT
                .create()
                .sType( EXTDebugReport.VK_STRUCTURE_TYPE_DEBUG_REPORT_CALLBACK_CREATE_INFO_EXT )
                .flags(
                    EXTDebugReport.VK_DEBUG_REPORT_DEBUG_BIT_EXT |
                    EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT |
                    EXTDebugReport.VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT |
                    EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT |
                    EXTDebugReport.VK_DEBUG_REPORT_INFORMATION_BIT_EXT
                )
                .pfnCallback( callback )
                .pNext( VK10.VK_NULL_HANDLE )
                .pUserData( VK10.VK_NULL_HANDLE );
            
            if ( instance.getCapabilities().vkCreateDebugReportCallbackEXT != VK10.VK_NULL_HANDLE ) {
                logger.fine( "Method vkCreateDebugReportCallbackEXT is supported." );
                var addr = new long[1];
                VulkanHelper.assertCall(
                    () -> EXTDebugReport.vkCreateDebugReportCallbackEXT( instance,
                        createInfo,
                        null,
                        addr
                    ) ,
                    resultCode -> {
                        logger.warning( "Cannot set up debug callback. Error code: " + resultCode );
                        addr[0] = addr[0] = VK10.VK_NULL_HANDLE;
                    }
                );
                debugCallbackAddress.setAddress( addr[0] );
            } else {
                logger.warning( "Method vkCreateDebugReportCallbackEXT is not supported!" );
            }
        }
    }
    
    void disposeUnsafe() {
        if ( debugCallbackAddress.isNull() ) {
            return;
        }
        if ( instance.getCapabilities().vkDestroyDebugReportCallbackEXT != VK10.VK_NULL_HANDLE ) {
            logger.fine( "Method vkDestroyDebugReportCallbackEXT is supported." );
            EXTDebugReport.vkDestroyDebugReportCallbackEXT( instance, debugCallbackAddress.getValue(), null );
        } else {
            logger.warning( "Method vkDestroyDebugReportCallbackEXT is not supported!" );
        }
    }
    
    
    class DebugLogger extends VkDebugReportCallbackEXT {
        
        @Override
        public int invoke(
            int flags,
            int objectType,
            long object,
            long location,
            int messageCode,
            long pLayerPrefix,
            long pMessage,
            long pUserData
        ) {
            logger.log(
                getLoggerLevel( flags ),
                "Validation layer: { " +
                " flags:" + Integer.toBinaryString( flags ) +
                ", objType:" + objectType +
                ", objAddr: " + object +
                ", loc: " + location +
                ", msgCode: " + messageCode +
                ", layer: " + pLayerPrefix +
                "} msg:\n"
                + MemoryUtil.memUTF8( pMessage )
            );
            return VK10.VK_FALSE;
        }
        
        private Level getLoggerLevel( int flags ) {
            var level = Level.FINEST;
            if( ( flags & EXTDebugReport.VK_DEBUG_REPORT_INFORMATION_BIT_EXT ) != 0 ) {
                level = Level.INFO;
            }
            if( ( flags & EXTDebugReport.VK_DEBUG_REPORT_WARNING_BIT_EXT ) != 0 ) {
                level = Level.WARNING;
            }
            if( ( flags & EXTDebugReport.VK_DEBUG_REPORT_PERFORMANCE_WARNING_BIT_EXT ) != 0 ) {
                level = Level.WARNING;
            }
            if( ( flags & EXTDebugReport.VK_DEBUG_REPORT_ERROR_BIT_EXT ) != 0 ) {
                level = Level.SEVERE;
            }
            if( ( flags & EXTDebugReport.VK_DEBUG_REPORT_DEBUG_BIT_EXT ) != 0 ) {
                level = Level.FINEST;
            }
            return level;
        }
    }
}
