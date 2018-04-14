/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.window.opengl.OpenGlFrameworkProvider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FrameworkProviderContext {
    
    public static RenderingFrameworkProvider getRenderingFrameworkProvider(){
        String renderingFrameworkClassName = System.getProperty(
            PropertiesUtil.RENDERING_FRAMEWORK_CLASS_NAME_PROPERTY,
            OpenGlFrameworkProvider.class.getName()
        );
        Class<?> renderingFrameworkClass =
            getRenderingFrameworkProviderClass( renderingFrameworkClassName );
    
        try {
            Method getter = renderingFrameworkClass.getMethod( FirstOracleConstants.GET_FRAMEWORK_METHOD_NAME );
            Object instance = getter.invoke( null );
            if( instance instanceof RenderingFrameworkProvider ){
                return ( RenderingFrameworkProvider ) instance;
            }
            throw new ClassNotRenderingFrameworkProvider( renderingFrameworkClassName );
        } catch ( IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
            throw new CannotCreateRenderingFrameworkInstance( renderingFrameworkClassName, e );
        }
    }
    
    
    private static Class<?> getRenderingFrameworkProviderClass( String className ) {
        try {
            return Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            throw new RenderingFrameworkClassNotFoundException( className, e );
        }
    }
    
    private static class RenderingFrameworkClassNotFoundException extends RuntimeException {
        
        RenderingFrameworkClassNotFoundException( String className, ClassNotFoundException e ) {
            super( "Cannot find rendering framework: "+className, e );
        }
    }
    
    private static class CannotCreateRenderingFrameworkInstance extends RuntimeException {
        
        CannotCreateRenderingFrameworkInstance( String className, ReflectiveOperationException e ) {
            super( "Cannot create rendering framework via "+FirstOracleConstants.GET_FRAMEWORK_METHOD_NAME+"() method of "+className + " class", e );
        }
    }
    
    private static class ClassNotRenderingFrameworkProvider extends RuntimeException {
        
        ClassNotRenderingFrameworkProvider( String className ) {
            super( "Given class: "+className + " is not a valid instance of "+RenderingFrameworkProvider.class.getName() );
        }
    }
}
