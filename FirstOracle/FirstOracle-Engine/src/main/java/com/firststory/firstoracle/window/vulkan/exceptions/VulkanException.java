/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan.exceptions;

/**
 * @author n1t4chi
 */
public class VulkanException extends RuntimeException {
    
    private static String errorCodeString( int errorCode ) {
        return "Error code: " + errorCode + " ["+VulkanExceptionHelper.parseResultCode( errorCode )+"]. ";
    }
    
    VulkanException( String s, Exception ex ) {
        super( s, ex );
    }
    
    VulkanException( int errorCode, String message, Exception ex ) {
        super( errorCodeString( errorCode ) + message, ex );
    }
    
    VulkanException( int errorCode, String message ) {
        super( errorCodeString( errorCode ) + message );
    }
    
    VulkanException( String message ) {
        super( message );
    }

//    VulkanException() {}
    
    VulkanException( int errorCode ) {
        this( errorCode, "" );
    }
}
