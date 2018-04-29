/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window.vulkan;

import com.firststory.firstoracle.window.vulkan.exceptions.VulkanException;
import org.lwjgl.vulkan.VK10;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author n1t4chi
 */
public class VulkanHelper {
    
    static void assertCallAndThrow(
        Supplier< Integer > errorCodeProvider,
        Function< Integer, VulkanException > failureException
    ) {
        assertCallAndThrow( errorCodeProvider, errorCode -> errorCode != VK10.VK_SUCCESS, failureException );
    }
    
    static void assertCall(
        Supplier< Integer > errorCodeProvider,
        Consumer< Integer > failureAction
    ) {
        assertCall( errorCodeProvider, errorCode -> errorCode != VK10.VK_SUCCESS, failureAction );
    }
    
    static void assertCallAndThrow(
        Supplier< Integer > errorCodeProvider,
        Predicate< Integer > failureTest,
        Function< Integer, VulkanException > failureException
    ) {
        assertCall(
            errorCodeProvider,
            failureTest,
            ( Consumer< Integer > ) errorCode -> { throw failureException.apply( errorCode ); }
        );
    }
    
    static void assertCall(
        Supplier< Integer > errorCodeProvider,
        Predicate< Integer > failureTest,
        Consumer< Integer > failureAction
    ) {
        int errorCode;
        if ( failureTest.test( errorCode = errorCodeProvider.get() ) ) {
            failureAction.accept( errorCode );
        }
    }
}
