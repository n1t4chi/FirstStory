/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author n1t4chi
 */
public class ReflectionUtils {
    
    public static < T > Class< ? extends T > extractClassForName( String className, Class< T > baseClass ) {
        Class< ? > aClass;
        try {
            aClass = baseClass.getClassLoader().loadClass( className );
        } catch ( ClassNotFoundException ex ) {
            throw new ClassDoesNotExistException( className, ex );
        }
        return aClass.asSubclass( baseClass );
    }
    
    public static < T > T createInstanceViaMethod( Class< T > aClass, Object... parameters ) {
        return createInstance( aClass, parameters, new MethodProvider<>() );
    }
    
    public static < T > T createInstanceViaConstructor( Class< T > aClass, Object... parameters ) {
        return createInstance( aClass, parameters, new ConstructorProvider<>() );
    }
    
    private static < T > T createInstance(
        Class< T > aClass,
        Object[] parameters,
        CreatorProvider< T > creatorProvider
    ) {
        var parameterClasses = new Class[ parameters.length ];
        for ( int i = 0, length = parameters.length; i < length; i++ ) {
            var parameter = parameters[ i ];
            parameterClasses[ i ] = parameter == null ? Object.class : parameter.getClass();
        }
        InstanceCreator< T > creator;
        try {
            creator = creatorProvider.provide( aClass, parameterClasses );
        } catch ( NoSuchMethodException ex ) {
            throw new CannotExtractProviderException( aClass, parameterClasses, creatorProvider.type(), ex );
        } catch ( Exception ex ) {
            throw new ReflectiveCreateException( aClass, parameterClasses, " Unknown error", true, ex );
        }
        
        if ( creator.isStatic() ) {
            throw new NonStaticException( aClass, parameterClasses, creatorProvider.type() );
        }
        if ( creator.isPublic() ) {
            throw new NonPublicException( aClass, parameterClasses, creatorProvider.type() );
        }
        try {
            return creator.create( aClass, parameters );
        } catch ( Exception ex ) {
            throw new ReflectiveCreateException(
                aClass,
                parameterClasses,
                "Exception during method invokation.",
                true,
                ex
            );
        }
    }
    
    private interface InstanceCreator< T > {
        
        default T create( Class< T > aClass, Object[] parameters ) throws Exception {
            return aClass.cast( create( parameters ) );
        }
        
        Object create( Object[] parameters ) throws Exception;
        
        boolean isStatic();
        
        boolean isPublic();
    }
    
    private interface CreatorProvider< T > {
        
        InstanceCreator< T > provide( Class< T > aClass, Class< ? >[] paremeterClasses ) throws Exception;
        
        String type();
    }
    
    private static class MethodInstanceCreator< T > implements InstanceCreator< T > {
        
        private final Method method;
        
        private MethodInstanceCreator( Method method ) {
            this.method = method;
        }
        
        @Override
        public Object create( Object[] parameters ) throws Exception {
            return method.invoke( null, parameters );
        }
        
        @Override
        public boolean isStatic() {
            return Modifier.isStatic( method.getModifiers() );
        }
        
        @Override
        public boolean isPublic() {
            return Modifier.isPublic( method.getModifiers() );
        }
    }
    
    private static class ConstructorInstanceCreator< T > implements InstanceCreator< T > {
        
        private final Constructor< T > constructor;
        
        public ConstructorInstanceCreator( Constructor< T > constructor ) {
            this.constructor = constructor;
        }
        
        @Override
        public Object create( Object[] parameters ) throws Exception {
            return constructor.newInstance( null, parameters );
        }
        
        @Override
        public boolean isStatic() {
            return true;
        }
        
        @Override
        public boolean isPublic() {
            return Modifier.isPublic( constructor.getModifiers() );
        }
    }
    
    private static class MethodProvider< T > implements CreatorProvider< T > {
        
        @Override
        public MethodInstanceCreator< T > provide( Class< T > aClass, Class< ? >[] paremeterClasses ) throws Exception {
            return new MethodInstanceCreator<>( aClass.getMethod(
                FirstOracleConstants.REFLECT_PROVIDE_METHOD_NAME,
                paremeterClasses
            ) );
        }
        
        @Override
        public String type() {
            return "method";
        }
    }
    
    private static class ConstructorProvider< T > implements CreatorProvider< T > {
        
        @Override
        public ConstructorInstanceCreator< T > provide( Class< T > aClass, Class< ? >[] paremeterClasses ) throws
            Exception {
            return new ConstructorInstanceCreator<>( aClass.getConstructor( paremeterClasses ) );
        }
        
        @Override
        public String type() {
            return "constructor";
        }
    }
    
    private static class CannotExtractProviderException extends ReflectiveCreateException {
        
        private CannotExtractProviderException(
            Class< ? > aClass,
            Class< ? >[] parameterClasses,
            String type,
            NoSuchMethodException ex
        ) {
            super( aClass, parameterClasses, type + " does not exist.", false, ex );
        }
    }
    
    private static class NonStaticException extends ReflectiveCreateException {
        
        private NonStaticException( Class< ? > aClass, Class< ? >[] parameterClasses, String type ) {
            super( aClass, parameterClasses, "Existing " + type + " is not static.", false );
        }
        
    }
    
    private static class NonPublicException extends ReflectiveCreateException {
        
        private NonPublicException( Class< ? > aClass, Class< ? >[] parameterClasses, String type ) {
            super( aClass, parameterClasses, "Existing " + type + " is not public.", false );
        }
    }
    
    private static class ReflectiveCreateException extends RuntimeException {
        
        private static String exceptionMessage(
            Class< ? > aClass,
            Class< ? >[] parameterClasses,
            String afterMessage,
            boolean constructor
        ) {
            return (
                constructor ? constructorMessage( aClass, parameterClasses ) : methodMessage( aClass, parameterClasses )
            ) + ". " + afterMessage;
        }
        
        private static String methodMessage( Class< ? > aClass, Class< ? >[] parameterClasses ) {
            return "Class " +
                aClass +
                " does not have public constructor with parameters: " +
                Arrays.toString( parameterClasses );
        }
        
        private static String constructorMessage( Class< ? > aClass, Class< ? >[] parameterClasses ) {
            return "Class " +
                aClass +
                " does not have public static method " +
                FirstOracleConstants.REFLECT_PROVIDE_METHOD_NAME +
                " with parameters: " +
                Arrays.toString( parameterClasses );
        }
        
        private ReflectiveCreateException(
            Class< ? > aClass,
            Class< ? >[] parameterClasses,
            String afterMessage,
            boolean constructor,
            Exception ex
        ) {
            super( exceptionMessage( aClass, parameterClasses, afterMessage, constructor ), ex );
        }
        
        private ReflectiveCreateException(
            Class< ? > aClass,
            Class< ? >[] parameterClasses,
            String afterMessage,
            boolean constructor
        ) {
            super( exceptionMessage( aClass, parameterClasses, afterMessage, constructor ) );
        }
    }
    
    private static class ClassDoesNotExistException extends RuntimeException {
        
        private ClassDoesNotExistException( String className, ClassNotFoundException ex ) {
            super( " Could not find class " + className , ex);
        }
    }
}
