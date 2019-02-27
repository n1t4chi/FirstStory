package com.firststory.firsttools;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface FirstToolsConstants {
    Logger logger = FirstToolsConstants.getLogger( FirstToolsConstants.class );

    DecimalFormat FORMATTER = new DecimalFormat("#.##");
    String RESOURCES_FOLDER = "resources/";
    String REFLECT_PROVIDE_METHOD_NAME = "provide";

    static Logger getLogger( Class< ? > classObject ) {
        return Logger.getLogger( classObject.getName() );
    }

    static < Type > void executeOnAllFields(
        Object executingObject,
        Predicate< Field > isCorrectField,
        Class< Type > fieldSuperclass,
        Consumer< Type > execute,
        boolean shouldThrowException
    ) {
        var fields = executingObject.getClass().getDeclaredFields();
        for ( var field : fields ) {
            try {
                if( isCorrectField.test( field ) ) {
                    field.setAccessible( true );
                    var obj = fieldSuperclass.cast( field.get( executingObject ) );
                    execute.accept( obj );
                }
            } catch ( Exception ex ) {
                logger.log( Level.WARNING, "Exception while trying to execute method on fields", ex );
                if( shouldThrowException ) {
                    throw new RuntimeException( "Exception while trying to execute method on fields", ex );
                }
            }
        }
    }
}
