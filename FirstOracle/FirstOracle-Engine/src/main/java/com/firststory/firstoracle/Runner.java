/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;


/**
 * Class used to run and initialise graphic engine context amd then run proper program.
 * This class contains main method that must be run in order to properly initialise and destroy graphic engine.
 * After engine initialisation it will try to run proper main method of given class
 * and after that method ends it will try to clear up after engine.
 * In order to run, Runner will try to find class given via property given in
 * {@link PropertiesUtil#APPLICATION_CLASS_NAME_PROPERTY}
 * And then invoke public static void main(String[] args) method of that class.
 * WARNING: Early exit from that method will result in engine termination which might lead to unwanted crashes.
 */
public class Runner {
    private static final ArrayList< FrameworkProvider > FRAMEWORK_PROVIDERS = new ArrayList<>();
    private static final Logger logger = FirstOracleConstants.getLogger( Runner.class );
    
    public static void registerFramework( FrameworkProvider provider ) {
        logger.finer( "Registering framework: " + provider );
        FRAMEWORK_PROVIDERS.add( provider );
    }
    
    public static void main( String[] args ) {
        logger.fine( "Running application from Runner with arguments: " + Arrays.toString( args ) );
        try {
            var className = getApplicationClassName();
            logger.fine( "Loading Application from class: " + className );
            var main = getMainMethod( className );
            invokeMainMethod( args, className, main );
        } catch ( Exception e ) {
            logger.severe( "Error while running application: " + e );
            e.printStackTrace();
        } finally {
            for ( var i = FRAMEWORK_PROVIDERS.size() -1 ; i >= 0 ; i-- ) {
                var provider = FRAMEWORK_PROVIDERS.get( i );
                logger.finer( "Terminating framework: "+provider );
                provider.terminate();
            }
            logger.finer( "All frameworks Terminated." );
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
        var className = System.getProperty( PropertiesUtil.APPLICATION_CLASS_NAME_PROPERTY );
        if ( className == null ) {
            throw new ApplicationPropertyNotSetException();
        }
        return className;
    }
    
    private static Method getMainMethod( String className ) {
        try {
            return Class.forName( className ).getMethod( "main", String[].class );
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
            super("Cannot start Engine. Property not set:" + PropertiesUtil.APPLICATION_CLASS_NAME_PROPERTY + "." );
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