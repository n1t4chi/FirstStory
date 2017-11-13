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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

/**
 * @author: n1t4chi
 */
public class CameraController extends GLFWKeyCallback implements Runnable,
    CameraNotifier,
    QuitListener
{

    private static final int DIRECTION_BASE_ROTATION = 135;

    private final CameraKeyMap cameraKeyMap;
    private long refreshLatency;
    private float speed = 15f;

    private ConcurrentHashMap< Integer, Integer > KeyMap = new ConcurrentHashMap<>( 10 );
    private Collection< CameraListener > cameraObservers = new ArrayList<>( 3 );

    private float rotationY = 0;
    private float rotationX = 0;
    private float posX = 0;
    private float posY = 0;
    private float posZ = 0;
    private double lastTime;

    private volatile boolean keepWorking = true;

    private Vector2f direction = new Vector2f( 1, 1 );
    private Vector2f perpendicularDirection = new Vector2f( 1, 1 );

    public CameraController( CameraKeyMap cameraKeyMap, long refreshLatency, float speed ) {
        this.cameraKeyMap = cameraKeyMap;
        this.refreshLatency = refreshLatency;
        this.speed = speed;
        rotateVectors();
    }

    public void updateIsometricCamera3D( IsometricCamera3D camera ) {
        camera.setCenterPoint( posX, posY, posZ );
        camera.setRotationX( rotationX );
        camera.setRotationY( rotationY );
    }

    public void updateMovableCamera2D( MovableCamera2D camera ) {
        camera.setCenterPoint( posX, posZ );
        camera.setRotation( rotationX );
    }

    public void kill() {
        keepWorking = false;
    }

    @Override
    public void run() {
        lastTime = GLFW.glfwGetTime();
        try {
            sleep( refreshLatency );
        } catch ( InterruptedException ex ) {
            System.err.println( "Controller sleep interrupted:" + ex );
            Thread.currentThread().interrupt();
        }
        while ( keepWorking ) {
            double currentTime = GLFW.glfwGetTime();
            boolean updated = false;
            if ( !KeyMap.isEmpty() ) {
                float timeDelta = ( float ) ( ( currentTime - lastTime ) * speed );
                for ( Map.Entry< Integer, Integer > e : KeyMap.entrySet() ) {
                    if ( key( e.getKey(), e.getValue(), timeDelta ) ) {
                        KeyMap.remove( e.getKey() );
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
    public void invoke( long window, int key, int scancode, int action, int mods ) {
        System.err.println(
            "w:" + window + ", k:" + key + ", sc" + scancode + ", a:" + action + ", m:" + mods );
        if ( action == GLFW.GLFW_PRESS ) {
            KeyMap.put( key, mods );
        } else if ( action == GLFW.GLFW_RELEASE ) {
            KeyMap.remove( key );
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
        System.err.println( "quit" );
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

    private boolean shouldMoveDown( int key, int mods ) {
        return cameraKeyMap.shouldMoveDown( key, mods );
    }

    private boolean shouldMoveUp( int key, int mods ) {
        return cameraKeyMap.shouldMoveUp( key, mods );
    }

    private boolean shouldMoveLeft( int key, int mods ) {
        return cameraKeyMap.shouldMoveLeft( key, mods );
    }

    private boolean shouldMoveRight( int key, int mods ) {
        return cameraKeyMap.shouldMoveRight( key, mods );
    }

    private boolean shouldMoveBackwards( int key, int mods ) {
        return cameraKeyMap.shouldMoveBackwards( key, mods );
    }

    private boolean shouldMoveForward( int key, int mods ) {
        return cameraKeyMap.shouldMoveForward( key, mods );
    }

    private boolean shouldRotateRight( int key, int mods ) {
        return cameraKeyMap.shouldRotateRight( key, mods );
    }

    private boolean shouldRotateLeft( int key, int mods ) {
        return cameraKeyMap.shouldRotateLeft( key, mods );
    }

    private boolean shouldRotateDown( int key, int mods ) {
        return cameraKeyMap.shouldRotateDown( key, mods );
    }

    private boolean shouldRotateUp( int key, int mods ) {
        return cameraKeyMap.shouldRotateUp( key, mods );
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
        return;
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
        posX += direction.x;
        posZ += direction.y;
    }
}
