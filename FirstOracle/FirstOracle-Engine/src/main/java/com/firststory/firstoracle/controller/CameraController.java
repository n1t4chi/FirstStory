/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.window.notifying.QuitEvent;
import com.firststory.firstoracle.window.notifying.QuitListener;
import com.firststory.firstoracle.window.notifying.QuitNotifier;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

/**
 * @author n1t4chi
 */
public class CameraController implements Runnable, CameraNotifier, QuitListener {

    private static final int DIRECTION_BASE_ROTATION = 135;

    private final CameraKeyMap cameraKeyMap;
    private final long refreshLatency;
    private final ConcurrentHashMap< Integer, Integer > keyMap = new ConcurrentHashMap<>( 10 );
    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke( long window, int key, int scancode, int action, int mods ) {
            //        System.err.println(
            //            "w:" + window + ", k:" + key + ", sc" + scancode + ", a:" + action + ", m:" + mods );
            if ( action == GLFW.GLFW_PRESS ) {
                keyMap.put( key, mods );
            } else if ( action == GLFW.GLFW_RELEASE ) {
                keyMap.remove( key );
            }
        }
    };
    private final Collection< CameraListener > cameraListeners = new ArrayList<>( 3 );
    private final Vector2f direction2D = new Vector2f( 1, 1 );
    private final Vector2f perpendicularDirection2D = new Vector2f( 1, 1 );
    private final Vector2f direction3D = new Vector2f( 1, 1 );
    private final Vector2f perpendicularDirection3D = new Vector2f( 1, 1 );
    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke( long l, double deltaX, double deltaY ) {
            cameraSize -= deltaY;
            if ( cameraSize < 1 ) {
                cameraSize = 1;
            } else {
                notifyCameraListeners( new CameraEvent( pos2D, pos3D, rotationY, rotationX ) );
            }
        }
    };
    private float speed;
    private float rotationY = 0;
    private float rotationX = 0;
    private Vector3f pos3D = new Vector3f( 0, 0, 0 );
    private Vector2f pos2D = new Vector2f( 0, 0 );
    private float cameraSize = 25;
    private volatile boolean keepWorking = true;
    
    public CameraController( CameraKeyMap cameraKeyMap, long refreshLatency, float speed ) {
        this.cameraKeyMap = cameraKeyMap;
        this.refreshLatency = refreshLatency;
        this.speed = speed;
        rotateVectors();
    }
    
    public void setSpeed( float speed ) {
        this.speed = speed;
    }

    public Vector2f getDirection2D() {
        return direction2D;
    }

    public Vector2f getPerpendicularDirection2D() {
        return perpendicularDirection2D;
    }

    public Vector2f getDirection3D() {
        return direction3D;
    }

    public Vector2f getPerpendicularDirection3D() {
        return perpendicularDirection3D;
    }

    public float getRotationY() {
        return rotationY;
    }
    
    public void setRotationY( float rotationY ) {
        this.rotationY = rotationY;
    }

    public float getRotationX() {
        return rotationX;
    }
    
    public void setRotationX( float rotationX ) {
        this.rotationX = rotationX;
    }

    public Vector3f getPos3D() {
        return pos3D;
    }
    
    public void setPos3D( Vector3f pos3D ) {
        this.pos3D = pos3D;
    }

    public Vector2f getPos2D() {
        return pos2D;
    }
    
    public void setPos2D( Vector2f pos2D ) {
        this.pos2D = pos2D;
    }
    public void setPos2DX( float X ) {
        this.pos2D.set( X,pos2D.y );
    }
    public void setPos2DY( float Y ) {
        this.pos2D.set( pos2D.x,Y );
    }

    public float getCameraSize() {
        return cameraSize;
    }
    
    public void setCameraSize( float cameraSize ) {
        this.cameraSize = cameraSize;
    }

    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }
    
    public void updateIsometricCamera3D( IsometricCamera3D camera ) {
        setCamera3dSize( camera );
        setCamera3dPosition( camera );
        setCamera3dRotationX( camera );
        setCamera3dRotationY( camera );
    }
    
    public void setCamera3dSize( IsometricCamera3D camera ) {
        camera.setSize( cameraSize );
    }
    
    public void setCamera3dPosition( IsometricCamera3D camera ) {
        camera.setCenterPoint( pos3D.x, pos3D.y, pos3D.z );
    }
    
    public void setCamera3dRotationX( IsometricCamera3D camera ) {
        camera.setRotationX( rotationX );
    }
    
    public void setCamera3dRotationY( IsometricCamera3D camera ) {
        camera.setRotationY( rotationY );
    }
    
    public void updateMovableCamera2D( MovableCamera2D camera ) {
        setCamera2dWidth( camera );
        setCamera2dTranslation( camera );
        setCamera2dRotation( camera );
    }
    
    public void setCamera2dWidth( MovableCamera2D camera ) {
        camera.setWidth( cameraSize );
    }
    
    public void setCamera2dTranslation( MovableCamera2D camera ) {
        camera.setTranslation( pos2D.x, pos2D.y );
    }
    
    public void setCamera2dRotation( MovableCamera2D camera ) {
        camera.setRotation( rotationY );
    }
    
    public void kill() {
        keepWorking = false;
    }

    @Override
    public void run() {
        double lastTime = GLFW.glfwGetTime();
        try {
            sleep( refreshLatency );
        } catch ( InterruptedException ex ) {
            System.err.println( "Controller sleep interrupted:" + ex );
            Thread.currentThread().interrupt();
        }
        while ( keepWorking ) {
            double currentTime = GLFW.glfwGetTime();
            boolean updated = false;
            if ( !keyMap.isEmpty() ) {
                float timeDelta = ( float ) ( ( currentTime - lastTime ) * speed );
                for ( Map.Entry< Integer, Integer > e : keyMap.entrySet() ) {
                    if ( key( e.getKey(), e.getValue(), timeDelta ) ) {
                        keyMap.remove( e.getKey() );
                    } else {
                        updated = true;
                    }
                }
            }
            if ( updated ) {
                notifyCameraListeners( new CameraEvent( pos2D, pos3D, rotationY, rotationX ) );
            }
            lastTime = currentTime;
            try {
                sleep( refreshLatency );
            } catch ( InterruptedException ex ) {
                System.err.println( "Controller sleep interrupted:" + ex );
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public Collection< CameraListener > getCameraListeners() {
        return cameraListeners;
    }

    @Override
    public void notify(
            QuitEvent event, QuitNotifier source
    ) {
        keepWorking = false;
    }

    private void rotateVectors() {
        double radians3D = Math.toRadians( DIRECTION_BASE_ROTATION + rotationY );
        direction3D.set( ( float ) Math.cos( radians3D ), ( float ) Math.sin( radians3D ) );
        radians3D = Math.toRadians( DIRECTION_BASE_ROTATION + rotationY + 90 );
        perpendicularDirection3D.set(
            ( float ) Math.cos( radians3D ),
            ( float ) Math.sin( radians3D )
        );
    
        double radians2D = Math.toRadians( rotationX - 90 );
        direction2D.set( ( float ) Math.cos( radians2D ), ( float ) Math.sin( radians2D ) );
        radians2D = Math.toRadians( rotationX );
        perpendicularDirection2D.set(
            ( float ) Math.cos( radians2D ),
            ( float ) Math.sin( radians2D )
        );
    }

    /**
     * @param key       GLFW key code
     * @param timeDelta repeat action differentiation
     *
     * @return whether to remove key or not.
     */
    private boolean key( int key, int mods, float timeDelta ) {
        if ( cameraKeyMap.shouldRotateUp( key, mods ) ) {
            rotateUp( timeDelta );
        } else if ( cameraKeyMap.shouldRotateDown( key, mods ) ) {
            rotateDown( timeDelta );
        } else if ( cameraKeyMap.shouldRotateLeft( key, mods ) ) {
            rotateLeft( timeDelta );
        } else if ( cameraKeyMap.shouldRotateRight( key, mods ) ) {
            rotateRight( timeDelta );
        } else if ( cameraKeyMap.shouldMoveForward( key, mods ) ) {
            moveForward( timeDelta );
        } else if ( cameraKeyMap.shouldMoveBackwards( key, mods ) ) {
            moveBackwards( timeDelta );
        } else if ( cameraKeyMap.shouldMoveRight( key, mods ) ) {
            moveRight( timeDelta );
        } else if ( cameraKeyMap.shouldMoveLeft( key, mods ) ) {
            moveLeft( timeDelta );
        } else if ( cameraKeyMap.shouldMoveUp( key, mods ) ) {
            moveUp( timeDelta );
        } else if ( cameraKeyMap.shouldMoveDown( key, mods ) ) {
            moveDown( timeDelta );
        } else {
            return true;
        }
        return false;
    }
    
    private void rotateUp( float timeDelta ) {
        rotationX += 5 * timeDelta;
        modRotationX();
        rotateVectors();
    }
    
    private void rotateDown( float timeDelta ) {
        rotationX -= 5 * timeDelta;
        modRotationX();
        rotateVectors();
    }
    
    private void rotateLeft( float timeDelta ) {
        rotationY += 5 * timeDelta;
        modRotationY();
        rotateVectors();
    }
    
    private void rotateRight( float timeDelta ) {
        rotationY -= 5 * timeDelta;
        modRotationY();
        rotateVectors();
    }
    
    private void modRotationX() {
        rotationX %= 360;
        if ( rotationX < 0 ) { rotationX += 360; }
    }
    
    private void modRotationY() {
        rotationY %= 360;
        if ( rotationY < 0 ) { rotationY += 360; }
    }
    
    private void moveForward( float timeDelta ) {
        addDirectionToVector( timeDelta, direction3D, pos3D );
        addDirectionToVector( timeDelta, direction2D, pos2D );
    }
    
    private void moveBackwards( float timeDelta ) {
        subDirectionToVector( timeDelta, direction3D, pos3D );
        subDirectionToVector( timeDelta, direction2D, pos2D );
    }
    
    private void moveRight( float timeDelta ) {
        subDirectionToVector( timeDelta, perpendicularDirection3D, pos3D );
        subDirectionToVector( timeDelta, perpendicularDirection2D, pos2D );
    }
    
    private void moveLeft( float timeDelta ) {
        addDirectionToVector( timeDelta, perpendicularDirection3D, pos3D );
        addDirectionToVector( timeDelta, perpendicularDirection2D, pos2D );
    }
    
    private void moveUp( float timeDelta ) {
        pos3D.add( 0, timeDelta, 0 );
    }
    
    private void moveDown( float timeDelta ) {
        pos3D.sub( 0, timeDelta, 0 );
    }
    
    private void addDirectionToVector( float timeDelta, Vector2f direction, Vector3f vector ) {
        vector.add( timeDelta * direction.x, timeDelta * direction.y, 0 );
    }
    
    private void addDirectionToVector( float timeDelta, Vector2f direction, Vector2f vector ) {
        vector.add( timeDelta * direction.x, timeDelta * direction.y );
    }
    
    private void subDirectionToVector( float timeDelta, Vector2f direction, Vector3f vector ) {
        vector.sub( timeDelta * direction.x, timeDelta * direction.y, 0 );
    }
    
    private void subDirectionToVector( float timeDelta, Vector2f direction, Vector2f vector ) {
        vector.sub( timeDelta * direction.x, timeDelta * direction.y );
    }
}
