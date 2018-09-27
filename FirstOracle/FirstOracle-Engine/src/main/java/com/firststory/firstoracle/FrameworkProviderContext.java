/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.window.GuiApplicationData;
import com.firststory.firstoracle.window.GuiFrameworkProvider;
import com.firststory.firstoracle.window.WindowFrameworkProvider;
import com.firststory.firstoracle.window.glfw.GlfwFrameworkProvider;
import com.firststory.firstoracle.window.opengl.OpenGlFrameworkProvider;

import java.lang.reflect.InvocationTargetException;

public class FrameworkProviderContext {
    
    public static RenderingFrameworkProvider createRenderingFrameworkProvider() {
        return createRenderingFrameworkProvider( System.getProperty( 
            PropertiesUtil.RENDERING_FRAMEWORK_CLASS_NAME_PROPERTY,
            OpenGlFrameworkProvider.class.getName()
        ) );
    }
    
    public static WindowFrameworkProvider createWindowFrameworkProvider() {
        return createWindowFrameworkProvider( System.getProperty(
            PropertiesUtil.WINDOW_FRAMEWORK_CLASS_NAME_PROPERTY,
            GlfwFrameworkProvider.class.getName()
        ) );
    }
    
    public static GuiFrameworkProvider< GuiApplicationData< ? > > createGuiFrameworkProvider() {
        return createGuiFrameworkProvider( System.getProperty(
            PropertiesUtil.GUI_FRAMEWORK_CLASS_NAME_PROPERTY,
            DummyGuiFrameworkProvider.class.getName()
        ) );
    }
    
    public static WindowFrameworkProvider createWindowFrameworkProvider( String windowFrameworkClassName ) {
        return createFrameworkInstance( windowFrameworkClassName, WindowFrameworkProvider.class );
    }
    
    @SuppressWarnings( "unchecked" )
    public static GuiFrameworkProvider< GuiApplicationData< ? > > createGuiFrameworkProvider( String guiFrameworkClassName ) {
        return createFrameworkInstance( guiFrameworkClassName, GuiFrameworkProvider.class );
    }
    
    public static RenderingFrameworkProvider createRenderingFrameworkProvider( String renderingFrameworkClassName ) {
        return createFrameworkInstance( renderingFrameworkClassName, RenderingFrameworkProvider.class );
    }
    
    private static < T extends FrameworkProvider > T createFrameworkInstance(
        String frameworkClassName, 
        Class< T > frameworkProviderClassType
    ) {
        try {
            var frameworkProviderClass = createFrameworkProviderClass( frameworkClassName );
            var getter = frameworkProviderClass.getMethod( FirstOracleConstants.GET_FRAMEWORK_PROVIDER_METHOD_NAME );
            var instance = getter.invoke( null );
            
            if ( frameworkProviderClassType.isInstance( instance ) ) {
                return frameworkProviderClassType.cast( instance );
            }
            throw new ClassNotRenderingFrameworkProvider( frameworkClassName, frameworkProviderClassType );
        } catch ( IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
            throw new CannotCreateRenderingFrameworkInstance( frameworkClassName, e );
        }
    }
    
    private static Class< ? > createFrameworkProviderClass( String className ) {
        try {
            return Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            throw new FrameworkClassNotFoundException( className, e );
        }
    }
    
    private static class FrameworkClassNotFoundException extends RuntimeException {
        
        FrameworkClassNotFoundException( String className, ClassNotFoundException e ) {
            super( "Cannot find framework: " + className, e );
        }
    }
    
    private static class CannotCreateRenderingFrameworkInstance extends RuntimeException {
        
        CannotCreateRenderingFrameworkInstance( String className, ReflectiveOperationException e ) {
            super(
                "Cannot create rendering framework via static " +
                    FirstOracleConstants.GET_FRAMEWORK_PROVIDER_METHOD_NAME + " method of " + className + " class",
                e
            );
        }
    }
    
    private static class ClassNotRenderingFrameworkProvider extends RuntimeException {
        
        ClassNotRenderingFrameworkProvider( String className, Class< ? > frameworkProviderClass ) {
            super( "Given class: " + className + " is not a valid instance of " + frameworkProviderClass.getName() );
        }
    }
}
