/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.controller;

import com.firststory.firstoracle.*;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.key.*;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.window.WindowImpl;
import org.joml.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.lang.Math;

/**
 * @author n1t4chi
 */
public class CameraController extends Thread implements
    CameraNotifier,
    QuitListener,
    KeyListener,
    MouseListener,
    TimeListener
{
    
    private static final Logger logger = FirstOracleConstants.getLogger( CameraController.class );
    private static final AtomicInteger instanceCounter = new AtomicInteger( 0 );
    private final CameraKeyMap cameraKeyMap;
    private final long refreshLatency;
    private final ConcurrentHashMap< KeyCode, Key > keyMap = new ConcurrentHashMap<>( 10 );
    private final Collection< CameraListener > cameraListeners = new ArrayList<>( 3 );
    private final Vector2f direction2D = new Vector2f( 1, 1 );
    private final Vector2f perpendicularDirection2D = new Vector2f( 1, 1 );
    private final Vector2f direction3D = new Vector2f( 1, 1 );
    private final Vector2f perpendicularDirection3D = new Vector2f( 1, 1 );
    private float speed;
    private float rotationY = 0;
    private float rotationX = 0;
    private Vector3f pos3D = new Vector3f( 0, 0, 0 );
    private Vector2f pos2D = new Vector2f( 0, 0 );
    private float cameraSize = 25;
    private volatile boolean keepWorking = true;
    private double currentTimeUpdate;
    
    private final MovableCamera2D camera2D;
    private final IsometricCamera3D camera3D;
    
    public static CameraController createAndStart( WindowImpl window, WindowSettings settings, CameraKeyMap cameraKeyMap, long refreshLatency, float speed ) {
        var cameraController = new CameraController( settings, cameraKeyMap, refreshLatency, speed );
        cameraController.updateIsometricCamera3D( cameraController.camera3D );
        cameraController.updateMovableCamera2D( cameraController.camera2D );
    
        cameraController.addCameraListener( ( event, source ) -> cameraController.updateMovableCamera2D( cameraController.camera2D ) );
        cameraController.addCameraListener( ( event, source ) -> cameraController.updateIsometricCamera3D( cameraController.camera3D ) );
    
        window.addTimeListener( cameraController );
        window.addQuitListener( cameraController );
        window.addKeyListener( cameraController );
        window.addMouseListener( cameraController );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                cameraController.camera2D.forceUpdate();
                cameraController.camera3D.forceUpdate();
            }
        } );
        cameraController.start();
        return cameraController;
    }
    
    public CameraController( WindowSettings settings, CameraKeyMap cameraKeyMap, long refreshLatency, float speed ) {
        super("Camera Controller " + instanceCounter.getAndIncrement() );
        this.cameraKeyMap = cameraKeyMap;
        this.refreshLatency = refreshLatency;
        this.speed = speed;
        rotateVectors();
        camera2D = new MovableCamera2D( settings, 0, 0, 0, 0 );
        camera3D = new IsometricCamera3D( settings, 0, 0, 0, 0, 0, 0, 1 );
    }
    
    public MovableCamera2D getCamera2D() {
        updateMovableCamera2D( camera2D );
        return camera2D;
    }
    
    public IsometricCamera3D getCamera3D() {
        updateIsometricCamera3D( camera3D );
        return camera3D;
    }
    
    @Override
    public void notify( MouseScrollEvent event ) {
        cameraSize -= event.yoffset;
        if ( cameraSize < 1 ) {
            cameraSize = 1;
        } else {
            notifyCameraListeners( new CameraEvent( pos2D, pos3D, rotationY, rotationX ) );
        }
        
    }
    
    @Override
    public void notify( KeyEvent event ) {
        switch(event.getAction()){
            case PRESS:
                logger.finest( "Key action: press "+event.getKey() );
                keyMap.put( event.getKeyCode(), event.getKey() );
                break;
            case RELEASE:
                logger.finest( "Key action: release "+event.getKey() );
                keyMap.remove( event.getKeyCode() );
                break;
        }
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
    
    public void setPos3D( float x, float y, float z ) {
        this.pos3D.set( x, y, z );
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
    
    public void setPos2D( float x, float y ) {
        this.pos2D.set( x, y );
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
        var previousTimeUpdate = currentTimeUpdate;
        while ( keepWorking ) {
            try {
                sleep( refreshLatency );
            } catch ( InterruptedException ex ) {
                System.err.println( "Controller sleep interrupted:" + ex );
                Thread.currentThread().interrupt();
            }
    
            var updated = false;
            if ( !keyMap.isEmpty() ) {
                var timeDelta = ( float ) ( ( currentTimeUpdate - previousTimeUpdate ) * speed );
                for ( var e : keyMap.entrySet() ) {
                    if ( key( e.getValue(), timeDelta ) ) {
                        keyMap.remove( e.getKey() );
                    } else {
                        updated = true;
                    }
                }
            }
            if ( updated ) {
                notifyCameraListeners( new CameraEvent( pos2D, pos3D, rotationY, rotationX ) );
            }
            previousTimeUpdate = currentTimeUpdate;
        }
    }

    @Override
    public Collection< CameraListener > getCameraListeners() {
        return cameraListeners;
    }

    @Override
    public Collection< Thread > notify( QuitEvent event, QuitNotifier source ) {
        keepWorking = false;
        return Collections.singleton( this );
    }
    
    @Override
    public void notify( double newTimeSnapshot, TimeNotifier source ) {
        currentTimeUpdate = newTimeSnapshot;
    }
    
    private void rotateVectors() {
        var angle = rotationY - 45;
        var radians3D = Math.toRadians( angle );
        direction3D.set( ( float ) Math.cos( radians3D ), ( float ) Math.sin( radians3D ) );
        radians3D = Math.toRadians( angle - 90 );
        perpendicularDirection3D.set(
            ( float ) Math.cos( radians3D ),
            ( float ) Math.sin( radians3D )
        );
    
        var radians2D = Math.toRadians( -(rotationY+90) );
        direction2D.set( ( float ) Math.cos( radians2D ), ( float ) Math.sin( radians2D ) );
        radians2D = Math.toRadians( -rotationY );
        perpendicularDirection2D.set(
            ( float ) Math.cos( radians2D ),
            ( float ) Math.sin( radians2D )
        );
    }

    /**
     * @param key       key
     * @param timeDelta repeat action differentiation
     *
     * @return whether to remove keyCode or not.
     */
    private boolean key( Key key, float timeDelta ) {
        if ( cameraKeyMap.shouldRotateUp( key ) ) {
            rotateUp( timeDelta );
        } else if ( cameraKeyMap.shouldRotateDown( key ) ) {
            rotateDown( timeDelta );
        } else if ( cameraKeyMap.shouldRotateLeft( key ) ) {
            rotateLeft( timeDelta );
        } else if ( cameraKeyMap.shouldRotateRight( key ) ) {
            rotateRight( timeDelta );
        } else if ( cameraKeyMap.shouldMoveForward( key ) ) {
            moveForward( timeDelta );
        } else if ( cameraKeyMap.shouldMoveBackwards( key ) ) {
            moveBackwards( timeDelta );
        } else if ( cameraKeyMap.shouldMoveRight( key ) ) {
            moveRight( timeDelta );
        } else if ( cameraKeyMap.shouldMoveLeft( key ) ) {
            moveLeft( timeDelta );
        } else if ( cameraKeyMap.shouldMoveUp( key ) ) {
            moveUp( timeDelta );
        } else if ( cameraKeyMap.shouldMoveDown( key ) ) {
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
        vector.add( timeDelta * direction.x, 0, timeDelta * direction.y );
    }
    
    private void addDirectionToVector( float timeDelta, Vector2f direction, Vector2f vector ) {
        vector.add( timeDelta * direction.x, timeDelta * direction.y );
    }
    
    private void subDirectionToVector( float timeDelta, Vector2f direction, Vector3f vector ) {
        vector.sub( timeDelta * direction.x, 0, timeDelta * direction.y );
    }
    
    private void subDirectionToVector( float timeDelta, Vector2f direction, Vector2f vector ) {
        vector.sub( timeDelta * direction.x, timeDelta * direction.y );
    }
}
