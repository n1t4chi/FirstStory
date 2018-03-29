/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import cuchaz.jfxgl.JFXGLLauncher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.firststory.firstoracle.FirstOracleConstants.APPLICATION_CLASS_NAME_PROPERTY;

/**
 * Class used to run and initialise graphic engine context amd then run proper program.
 * This class contains main method that must be run in order to properly initialise and destroy graphic engine.
 * After engine initialisation it will try to run proper main method of given class
 * and after that method ends it will try to clear up after engine.
 * In order to run, Runner will try to find class given via property given in
 * {@link FirstOracleConstants#APPLICATION_CLASS_NAME_PROPERTY}
 * And then invoke public static void main(String[] args) method of that class.
 * WARNING: Early exit from that method will result in engine termination which might lead to unwanted crashes.
 */
public class Runner {
    
    private static final ArrayList<FrameworkProvider > FRAMEWORK_PROVIDERS = new ArrayList<>();
    public static void registerFramework( FrameworkProvider frameworkProvider ) {
        FRAMEWORK_PROVIDERS.add( frameworkProvider );
    }
    
    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( Runner.class, args );
    }
    
    public static void jfxglmain( String[] args ) {
        try {
            String className = getApplicationClassName();
            Method main = getMainMethod( className );
            invokeMainMethod( args, className, main );
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            for ( int i = FRAMEWORK_PROVIDERS.size() -1 ; i >= 0 ; i-- ) {
                FRAMEWORK_PROVIDERS.get( i ).terminate();
            }
            FRAMEWORK_PROVIDERS.clear();
        }
    }
    
    private static void invokeMainMethod( String[] args, String className, Method main ) throws
        IllegalAccessException,
        InvocationTargetException
    {
        try {
            main.invoke( null, new Object[]{ args } );
        } catch ( NullPointerException e ){
            throw new ApplicationClassHasNoMainMethodException( className, e );
        }
    }
    
    private static String getApplicationClassName() {
        String className = System.getProperty( APPLICATION_CLASS_NAME_PROPERTY );
        if ( className == null ) {
            throw new ApplicationPropertyNotSetException();
        }
        return className;
    }
    
    private static Method getMainMethod( String className ) {
        try {
            Class< ? > c = null;
            c = Class.forName( className );
            return c.getMethod( "main", String[].class );
        } catch ( ClassNotFoundException e ) {
            throw new ApplicationClassNotFoundException( className, e );
        } catch ( NoSuchMethodException e ) {
            throw new ApplicationClassHasNoMainMethodException( className, e );
        }
    }
    
    private static class RunnerException extends RuntimeException{
        
        RunnerException( String message ) {
            super( message );
        }
        
        RunnerException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    private static class ApplicationPropertyNotSetException extends RunnerException {
        ApplicationPropertyNotSetException() {
            super("Cannot start Engine. Property not set:" + APPLICATION_CLASS_NAME_PROPERTY + "." );
        }
    }
    
    private static class ApplicationClassNotFoundException extends RunnerException {
        
        ApplicationClassNotFoundException( String className, Exception e ) {
            super( "Cannot find application Class: "+className, e );
        }
    }
    
    private static class ApplicationClassHasNoMainMethodException extends RunnerException {
        
        ApplicationClassHasNoMainMethodException( String className, Exception e ) {
            super( "Application Class: "+className+" has no static main(String[]) method", e );
        }
    }
}