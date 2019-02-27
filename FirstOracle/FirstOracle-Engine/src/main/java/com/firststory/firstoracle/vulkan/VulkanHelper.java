/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.vulkan;

import com.firststory.firstoracle.vulkan.exceptions.VulkanException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;

import java.util.function.*;

/**
 * @author n1t4chi
 */
public interface VulkanHelper {
    
    FailTest VK_SUCCESS_TEST = resultCode -> resultCode != VK10.VK_SUCCESS;
    
    static < CreateInfo > VulkanAddress createAddress(
        CreateInfoSupplier< CreateInfo > supplier,
        CreateInfoArrayCreator< CreateInfo > creator,
        ExceptionThrower thrower
    ) {
        return createAddress( supplier, creator, VK_SUCCESS_TEST, thrower );
    }
    
    static < CreateInfo > VulkanAddress createAddress(
        CreateInfoSupplier< CreateInfo > supplier,
        CreateInfoArrayCreator< CreateInfo > creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        return createAddress( address -> creator.create( supplier.get(), address ), test, thrower );
    }
    
    static VulkanAddress createAddress(
        ArrayCreator creator,
        ExceptionThrower thrower
    ) {
        return createAddress( creator, VK_SUCCESS_TEST, thrower );
    }
    
    static VulkanAddress createAddress(
        ArrayCreator creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        return updateAddress( new VulkanAddress(), creator, test, thrower );
    }
    
    static < CreateInfo > VulkanAddress updateAddress(
        VulkanAddress address,
        CreateInfoSupplier< CreateInfo > supplier,
        CreateInfoArrayCreator< CreateInfo > creator,
        ExceptionThrower thrower
    ) {
        return updateAddress( address, supplier, creator, VK_SUCCESS_TEST, thrower );
    }
    
    static < CreateInfo > VulkanAddress updateAddress(
        VulkanAddress address,
        CreateInfoSupplier< CreateInfo > supplier,
        CreateInfoArrayCreator< CreateInfo > creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        return updateAddress( address, addressA -> creator.create( supplier.get(), addressA ), test, thrower );
    }
    
    static VulkanAddress updateAddress(
        VulkanAddress address,
        ArrayCreator creator,
        ExceptionThrower thrower
    ) {
        return updateAddress( address, creator, VK_SUCCESS_TEST, thrower );
    }
    
    static VulkanAddress updateAddress(
        VulkanAddress address,
        ArrayCreator creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        var addressA = new long[1];
        VulkanHelper.assertCallOrThrow( () -> creator.create( addressA ), test, thrower );
        return address.setAddress( addressA[0] );
    }
    
    static < CreateInfo > VulkanAddress createAddressViaBuffer(
        CreateInfoSupplier< CreateInfo > supplier,
        CreateInfoBufferCreator< CreateInfo > creator,
        ExceptionThrower thrower
    ) {
        return createAddressViaBuffer( supplier, creator, VK_SUCCESS_TEST, thrower );
    }
    
    static < CreateInfo > VulkanAddress createAddressViaBuffer(
        CreateInfoSupplier< CreateInfo > supplier,
        CreateInfoBufferCreator< CreateInfo > creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        return createAddressViaBuffer( address -> creator.create( supplier.get(), address ), test, thrower );
    }
    
    static VulkanAddress createAddressViaBuffer(
        PointerBufferCreator creator,
        ExceptionThrower thrower
    ) {
        return createAddressViaBuffer( creator, VK_SUCCESS_TEST, thrower );
    }
    
    static VulkanAddress createAddressViaBuffer(
        PointerBufferCreator creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        return updateAddressViaBuffer( new VulkanAddress(), creator, test, thrower );
    }
    
    static VulkanAddress updateAddressViaBuffer(
        VulkanAddress address,
        PointerBufferCreator creator,
        ExceptionThrower thrower
    ) {
        return updateAddressViaBuffer( address, creator, VK_SUCCESS_TEST, thrower );
    }
    
    static VulkanAddress updateAddressViaBuffer(
        VulkanAddress address,
        PointerBufferCreator creator,
        FailTest test,
        ExceptionThrower thrower
    ) {
        var pointerBuffer = MemoryUtil.memAllocPointer( 1 );
        VulkanHelper.assertCallOrThrow( () -> creator.create( pointerBuffer ), test, thrower );
        var value = pointerBuffer.get();
        pointerBuffer.free();
        return address.setAddress( value );
    }
    
    static void assertCallOrThrow(
        ResultCodeSupplier supplier,
        ExceptionThrower thrower
    ) {
        assertCallOrThrow( supplier, VK_SUCCESS_TEST, thrower );
    }
    
    static void assertCall( ResultCodeSupplier supplier, FailAction action ) {
        assertCall( supplier, VK_SUCCESS_TEST, action );
    }
    
    static void assertCallOrThrow(
        ResultCodeSupplier supplier,
        FailTest test,
        ExceptionThrower thrower
    ) {
        assertCall( supplier, test, resultCode -> { throw thrower.create( resultCode ); } );
    }
    
    static void assertCall(
        ResultCodeSupplier supplier,
        FailTest test,
        FailAction action
    ) {
        int resultCode;
        if ( test.test( resultCode = supplier.get() ) ) {
            action.accept( resultCode );
        }
    }
    
    static < Product > void iterate(
        Iterable< Product > products,
        IterationAction< Product > action
    ) {
        var index = 0;
        for ( var product : products ) {
            action.accept( index++, product );
        }
    }
    
    static void iterate(
        PointerBuffer products,
        IterationAction< Long > action
    ) {
        var index = 0;
        while ( products.hasRemaining() ) {
            action.accept( index++, products.get() );
        }
    }
    
    static int flagsToInt( int... flags ) {
        var returnedFlag = 0;
        for ( var flag : flags ) {
            returnedFlag |= flag;
        }
        return returnedFlag;
    }
    
    interface IterationAction< Product > extends BiConsumer< Integer, Product > {
        @Override
        void accept( Integer index, Product product );
    }
    
    interface FailTest extends Predicate< Integer > {
        
        boolean isFailure( Integer resultCode );
        
        @Override
        default boolean test( Integer resultCode ) {
            return isFailure( resultCode );
        }
    }
    
    interface PointerBufferCreator extends Function< PointerBuffer, Integer > {

        Integer create( PointerBuffer address );

        @Override
        default Integer apply( PointerBuffer address ) {
            return create( address );
        }
    }
    
    interface ArrayCreator extends Function< long[], Integer > {
        
        Integer create( long[] address );
        
        @Override
        default Integer apply( long[] address ) {
            return create( address );
        }
    }
    
    interface CreateInfoBufferCreator< CreateInfo > extends BiFunction< CreateInfo, PointerBuffer, Integer > {
        
        Integer create( CreateInfo createInfo, PointerBuffer address );
        
        @Override
        default Integer apply( CreateInfo createInfo, PointerBuffer address ) {
            return create( createInfo, address );
        }
    }
    
    interface CreateInfoArrayCreator< CreateInfo > extends BiFunction< CreateInfo, long[], Integer > {
        
        Integer create( CreateInfo createInfo, long[] address );
        
        @Override
        default Integer apply( CreateInfo createInfo, long[] address ) {
            return create( createInfo, address );
        }
    }
    
    interface ResultCodeSupplier extends Supplier< Integer > {
        
        Integer create();
        
        @Override
        default Integer get() {
            return create();
        }
    }
    
    interface CreateInfoSupplier< CreateInfo > extends Supplier< CreateInfo > {
    
        CreateInfo create();
    
        @Override
        default CreateInfo get() {
            return create();
        }
    }
    
    interface ExceptionThrower extends FailAction {
        
        VulkanException create( Integer resultCode );
        
        @Override
        default void perform( Integer resultCode ) {
            throw create( resultCode );
        }
    }
    
    interface FailAction extends Consumer< Integer > {
        
        void perform( Integer resultCode );
        
        @Override
        default void accept( Integer resultCode ) {
            perform( resultCode );
        }
    }
}
