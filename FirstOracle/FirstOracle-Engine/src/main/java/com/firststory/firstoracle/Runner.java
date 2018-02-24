/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import com.firststory.firstoracle.window.GLFW.GlfwContext;
import com.firststory.firstoracle.window.JFXGL.JfxglContext;
import com.firststory.firstoracle.window.OpenGL.OpenGlContext;
import com.firststory.firstoracle.window.Vulkan.VulkanContext;
import cuchaz.jfxgl.JFXGLLauncher;

import java.lang.reflect.Method;

import static com.firststory.firstoracle.FirstOracleConstants.APPLICATION_CLASS_NAME_PROPERTY;

/**
 * Class used to run and initialise graphic engine context amd then run proper program.
 * This class contains main method that must be run in order to properly initialise and destroy graphic engine.
 * After engine initialisation it will try to run proper main method of given class
 * and after that method ends it will try to clear up after engine.
 * In order to run Runner will try to find class given via property given in
 * {@link FirstOracleConstants#APPLICATION_CLASS_NAME_PROPERTY}
 * And then invoke public static void main(String[] args) method of that class.
 * WARNING: Early exit from that method will result in engine termination which might lead to unwanted crashes.
 */
public class Runner {
    
    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( Runner.class, args );
    }
    
    public static void jfxglmain( String[] args ) {
        try {
            String className = System.getProperty( APPLICATION_CLASS_NAME_PROPERTY  );
            if(className == null){
                System.err.println("Cannot start Engine. Property not set:"+APPLICATION_CLASS_NAME_PROPERTY+".");
                return;
            }
            try {
                Class< ? > c = Class.forName( className );
                Method main = c.getMethod( "main", String[].class );
                main.invoke( null, new Object[]{ args } );
            }catch ( ClassNotFoundException ex ){
                System.err.println("Cannot start Engine. Given application class "+className+" was not found.");
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            OpenGlContext.terminate();
            VulkanContext.terminate();
            JfxglContext.terminate();
            GlfwContext.terminate();
        }
    }
    
}