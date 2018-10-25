/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan.physicaldevice.exceptions;

import org.lwjgl.vulkan.*;

/**
 * @author n1t4chi
 */
class VulkanExceptionHelper {
    //replace string
    //(VK_\w*) .*
    //case VK10.$1 :\n\t\t\t\treturn "$1";
    static String parseResultCode( int resultCode ){
        switch ( resultCode ) {
            case VK10.VK_SUCCESS :
				return "VK_SUCCESS";
            case VK10.VK_NOT_READY :
				return "VK_NOT_READY";
            case VK10.VK_TIMEOUT :
				return "VK_TIMEOUT";
            case VK10.VK_EVENT_SET :
				return "VK_EVENT_SET";
            case VK10.VK_EVENT_RESET :
				return "VK_EVENT_RESET";
            case VK10.VK_INCOMPLETE :
				return "VK_INCOMPLETE";
            case VK10.VK_ERROR_OUT_OF_HOST_MEMORY :
				return "VK_ERROR_OUT_OF_HOST_MEMORY";
            case VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY :
				return "VK_ERROR_OUT_OF_DEVICE_MEMORY";
            case VK10.VK_ERROR_INITIALIZATION_FAILED :
				return "VK_ERROR_INITIALIZATION_FAILED";
            case VK10.VK_ERROR_DEVICE_LOST :
				return "VK_ERROR_DEVICE_LOST";
            case VK10.VK_ERROR_MEMORY_MAP_FAILED :
				return "VK_ERROR_MEMORY_MAP_FAILED";
            case VK10.VK_ERROR_LAYER_NOT_PRESENT :
				return "VK_ERROR_LAYER_NOT_PRESENT";
            case VK10.VK_ERROR_EXTENSION_NOT_PRESENT :
				return "VK_ERROR_EXTENSION_NOT_PRESENT";
            case VK10.VK_ERROR_FEATURE_NOT_PRESENT :
				return "VK_ERROR_FEATURE_NOT_PRESENT";
            case VK10.VK_ERROR_INCOMPATIBLE_DRIVER :
				return "VK_ERROR_INCOMPATIBLE_DRIVER";
            case VK10.VK_ERROR_TOO_MANY_OBJECTS :
				return "VK_ERROR_TOO_MANY_OBJECTS";
            case VK10.VK_ERROR_FORMAT_NOT_SUPPORTED :
				return "VK_ERROR_FORMAT_NOT_SUPPORTED";
            case VK10.VK_ERROR_FRAGMENTED_POOL :
				return "VK_ERROR_FRAGMENTED_POOL";
            case -1000069000 : //VK_ERROR_OUT_OF_POOL_MEMORY :
				return "VK_ERROR_OUT_OF_POOL_MEMORY";
            case -1000072003 : //VK_ERROR_INVALID_EXTERNAL_HANDLE :
				return "VK_ERROR_INVALID_EXTERNAL_HANDLE";
            case KHRSurface.VK_ERROR_SURFACE_LOST_KHR :
				return "VK_ERROR_SURFACE_LOST_KHR";
            case KHRSurface.VK_ERROR_NATIVE_WINDOW_IN_USE_KHR :
				return "VK_ERROR_NATIVE_WINDOW_IN_USE_KHR";
            case KHRSwapchain.VK_SUBOPTIMAL_KHR :
				return "VK_SUBOPTIMAL_KHR";
            case KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR :
				return "VK_ERROR_OUT_OF_DATE_KHR";
            case KHRDisplaySwapchain.VK_ERROR_INCOMPATIBLE_DISPLAY_KHR :
				return "VK_ERROR_INCOMPATIBLE_DISPLAY_KHR";
            case EXTDebugReport.VK_ERROR_VALIDATION_FAILED_EXT :
				return "VK_ERROR_VALIDATION_FAILED_EXT";
            case NVGLSLShader.VK_ERROR_INVALID_SHADER_NV :
				return "VK_ERROR_INVALID_SHADER_NV";
        }
        return "Unknown Code";
    }
}
