/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window.opengl;

import com.firststory.firstoracle.camera2D.Camera2D;
import com.firststory.firstoracle.camera3D.Camera3D;
import com.firststory.firstoracle.shader.CannotCreateUniformLocationException;
import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author n1t4chi
 */
class OpenGlUniformLocation {
    
    private final int locationID;
    private final Vector2f VEC_2 = new Vector2f();
    private final Vector3f VEC_3 = new Vector3f();
    private final Vector4f VEC_4 = new Vector4f();
    private final Matrix3f MAT_3 = new Matrix3f();
    private final Matrix4f MAT_4 = new Matrix4f();
    private Object lastBind = null;
    
    OpenGlUniformLocation( int program, String locationName ) throws CannotCreateUniformLocationException {
        int locationID = GL20.glGetUniformLocation( program, locationName );
        if ( locationID < 0 ) {
            throw new CannotCreateUniformLocationException( locationName );
        }
        this.locationID = locationID;
    }
    
    void bindRotation( float rotation ) {
        if ( !( ( ( Float ) rotation ).equals( lastBind ) ) ) {
            GL20.glUniform3f( locationID, 0, 0, rotation );
            lastBind = rotation;
        }
    }
    
    void bindScale( Vector2fc vector ) {
        if ( !vector.equals( lastBind ) ) {
            GL20.glUniform3f( locationID, vector.x(), vector.y(), 1 );
            lastBind = VEC_2.set( vector );
        }
    }
    
    void bindPosition( Vector2fc vector ) {
        if ( !vector.equals( lastBind ) ) {
            GL20.glUniform3f( locationID, vector.x(), vector.y(), 0 );
            lastBind = VEC_2.set( vector );
        }
    }
    
    void bind( Vector2fc vector ) {
        if ( !vector.equals( lastBind ) ) {
            GL20.glUniform2f( locationID, vector.x(), vector.y() );
            lastBind = VEC_2.set( vector );
        }
    }
    
    void bind( Vector3fc vector ) {
        if ( !vector.equals( lastBind ) ) {
            GL20.glUniform3f( locationID, vector.x(), vector.y(), vector.z() );
            lastBind = VEC_3.set( vector );
        }
    }
    
    void bind( Vector4fc vector ) {
        if ( !vector.equals( lastBind ) ) {
            GL20.glUniform4f( locationID, vector.x(), vector.y(), vector.z(), vector.w() );
            lastBind = VEC_4.set( vector );
        }
    }
    
    void bind( float value ) {
        if ( !( ( ( Float ) value ).equals( lastBind ) ) ) {
            GL20.glUniform1f( locationID, value );
            lastBind = value;
        }
    }
    
     void bind( Camera3D camera3D ) {
        bind( camera3D.getMatrixRepresentation() );
    }
    
     void bind( Matrix4fc camera ) {
        if ( !camera.equals( lastBind ) ) {
            try ( MemoryStack stack = stackPush() ) {
                GL20.glUniformMatrix4fv(
                    locationID,
                    false,
                    camera.get( stack.callocFloat( 16 ) )
                );
            }
            lastBind = MAT_4.set( camera );
        }
    }
    
     void bind( Camera2D camera2D ) {
        bind( camera2D.getMatrixRepresentation() );
    }
    
     void bind( Matrix3fc camera ) {
        if ( !camera.equals( lastBind ) ) {
            try ( MemoryStack stack = stackPush() ) {
                GL20.glUniformMatrix3fv(
                    locationID,
                    false,
                    camera.get( stack.callocFloat( 9 ) )
                );
            }
            lastBind = MAT_3.set( camera );
        }
    }
    
}
