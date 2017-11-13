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
import org.lwjgl.glfw.*;

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
    private float speed = 15f;

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
    private final Collection< CameraListener > cameraObservers = new ArrayList<>( 3 );
    private float rotationY = 0;
    private float rotationX = 0;
    private float posX = 0;
    private float posY = 0;
    private float posZ = 0;
    private float cameraSize = 25;
    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke( long l, double deltaX, double deltaY ) {
            cameraSize -= deltaY;
            if ( cameraSize < 1 ) {
                cameraSize = 1;
            }
        }
    };
    private volatile boolean keepWorking = true;
    private final Vector2f direction = new Vector2f( 1, 1 );
    private final Vector2f perpendicularDirection = new Vector2f( 1, 1 );
    public CameraController( CameraKeyMap cameraKeyMap, long refreshLatency, float speed ) {
        this.cameraKeyMap = cameraKeyMap;
        this.refreshLatency = refreshLatency;
        this.speed = speed;
        rotateVectors();
    }

    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    public void setInitialValues(
        float rotationY, float rotationX, float posX, float posY, float posZ, float cameraSize
    )
    {
        this.rotationY = rotationY;
        this.rotationX = rotationX;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.cameraSize = cameraSize;
    }

    public void updateIsometricCamera3D( IsometricCamera3D camera ) {
        camera.setSize( cameraSize );
        camera.setCenterPoint( posX, posY, posZ );
        camera.setRotationX( rotationX );
        camera.setRotationY( rotationY );
    }

    public void updateMovableCamera2D( MovableCamera2D camera ) {
        camera.setWidth( cameraSize );
        camera.setCenterPoint( posX, posZ );
        camera.setRotation( rotationX );
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
//                System.err.println( "keep working: "+keyMap.size() );
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
                notifyCameraListeners( new CameraEvent( posX, posY, posZ, rotationY, rotationX ) );
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
    public Collection< CameraListener > getCameraObservers() {
        return cameraObservers;
    }

    @Override
    public void notify(
        QuitEvent event, QuitNotifier source
    )
    {
        keepWorking = false;
    }

    private void rotateVectors() {
        double radians = Math.toRadians( DIRECTION_BASE_ROTATION + rotationY );
        direction.set( ( float ) Math.cos( radians ), ( float ) Math.sin( radians ) );
        radians = Math.toRadians( DIRECTION_BASE_ROTATION + rotationY + 90 );
        perpendicularDirection.set( ( float ) Math.cos( radians ), ( float ) Math.sin( radians ) );
    }

    /**
     * @param key       GLFW key code
     * @param timeDelta repeat action differentiation
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
            System.err.println( "forward" );
            moveForward();
        } else if ( cameraKeyMap.shouldMoveBackwards( key, mods ) ) {
            moveBackwards();
        } else if ( cameraKeyMap.shouldMoveRight( key, mods ) ) {
            moveRight();
        } else if ( cameraKeyMap.shouldMoveLeft( key, mods ) ) {
            moveLeft();
        } else if ( cameraKeyMap.shouldMoveUp( key, mods ) ) {
            moveUp( timeDelta );
        } else if ( cameraKeyMap.shouldMoveDown( key, mods ) ) {
            moveDown( timeDelta );
        } else {
            return true;
        }
        return false;
    }

    private void rotateDown( float timeDelta ) {
        rotationX -= timeDelta;
        if ( rotationX <= 0 ) {
            rotationX = 360 - rotationX;
        }
        rotateVectors();
    }

    private void rotateUp( float timeDelta ) {
        rotationX += timeDelta;
        if ( rotationX >= 360 ) {
            rotationX = rotationX - 360;
        }
        rotateVectors();
    }

    private void rotateLeft( float timeDelta ) {
        rotationY += timeDelta;
        if ( rotationY >= 360 ) {
            rotationY = rotationY - 360;
        }
        rotateVectors();
    }

    private void rotateRight( float timeDelta ) {
        rotationY -= timeDelta;
        if ( rotationY <= 0 ) {
            rotationY = 360 - rotationY;
        }
        rotateVectors();
    }

    private void moveDown( float timeDelta ) {
        posZ -= timeDelta;
    }

    private void moveUp( float timeDelta ) {
        posY += timeDelta;
    }

    private void moveLeft() {
        posX -= perpendicularDirection.x;
        posZ -= perpendicularDirection.y;
    }

    private void moveRight() {
        posX += perpendicularDirection.x;
        posZ += perpendicularDirection.y;
    }

    private void moveBackwards() {
        posX -= direction.x;
        posZ -= direction.y;
    }

    private void moveForward() {
        System.err.println( "before pos: " + posX + "," + posZ );
        posX += direction.x;
        posZ += direction.y;
        System.err.println( "after pos: " + posX + "," + posZ );
    }
}
