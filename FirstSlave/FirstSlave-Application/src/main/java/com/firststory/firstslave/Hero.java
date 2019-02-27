/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstslave;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.data.Position3D;
import com.firststory.firstoracle.object.*;
import com.firststory.firstoracle.object3D.FullyAnimatedPlane3D;
import com.firststory.firstoracle.scene.RegistrableScene3D;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author n1t4chi
 */
class Hero {
    
    private static final int PERIOD_IN_MILIS = 10;
    private static final float delta = 4f * PERIOD_IN_MILIS / 1_000;
    private final FullyAnimatedPlane3D hero;
    private final FullyAnimatedPlane3D shadow;
    private final GameScene gameScene;
    private final FourDirectionController heroRotationController;
    private final ControllableFrameController heroFrameController;
    private final Future< ? > walkingThread;
    private WalkingState walkingState = WalkingState.still;
    private WalkingState lastState = walkingState;
    private Position3D movement = FirstOracleConstants.POSITION_ZERO_3F;
    private float walkComplete = 0;
    private float heroRotation = 0;
    
    Hero(
        FullyAnimatedPlane3D hero,
        ScheduledExecutorService executor,
        GameScene gameScene
    ) {
        this.hero = hero;
        shadow = new FullyAnimatedPlane3D();
        
        this.gameScene = gameScene;
        heroRotationController = new FourDirectionController();
        heroFrameController = new ControllableFrameController();
        this.hero.setDirectionController( heroRotationController );
        this.hero.setFrameController( heroFrameController );
        walkingThread = executor.scheduleAtFixedRate( this::tryWalk, 0, PERIOD_IN_MILIS, TimeUnit.MILLISECONDS );
        
        shadow.setTransformations( hero.getTransformations() );
        shadow.setDirectionController( heroRotationController );
        try {
            shadow.setTexture( Texture.createCompound( "resources/First Oracle/textures/hero3D/shadow_#direction#.png", 4, 1 ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
    
    void register( RegistrableScene3D scene3D ) {
        scene3D.registerObject3D( hero );
        scene3D.registerObject3D( shadow );
    }
    
    void dispose() {
        walkingThread.cancel( true );
    }
    
    void pauseGame() {
        walkingState = Hero.WalkingState.pause;
    }
    
    void continueGame() {
        walkingState = lastState;
    }
    
    Position3D getPosition() {
        return hero.getPosition();
    }
    
    void walkUp() {
        walkingState = WalkingState.up;
    }
    
    void walkDown() {
        walkingState = WalkingState.down;
    }
    
    void walkLeft() {
        walkingState = WalkingState.left;
    }
    
    void walkRight() {
        walkingState = WalkingState.right;
    }
    
    void stopWalkUp() {
        if( walkingState.equals( WalkingState.up ) ) {
            walkingState = WalkingState.still;
        }
    }
    
    void stopWalkDown() {
        if( walkingState.equals( WalkingState.down ) ) {
            walkingState = WalkingState.still;
        }
    }
    
    void stopWalkLeft() {
        if( walkingState.equals( WalkingState.left ) ) {
            walkingState = WalkingState.still;
        }
    }
    
    void stopWalkRight() {
        if( walkingState.equals( WalkingState.right ) ) {
            walkingState = WalkingState.still;
        }
    }
    
    private void tryWalk() {
        if( !walkingState.equals( WalkingState.pause ) ) {
            if( !lastState.equals( walkingState ) ) {
                switch ( walkingState ) {
                    case up:
                        heroRotation = 90;
                        setMovement( delta, 0, 0 );
                        break;
                    case down:
                        heroRotation = 270;
                        setMovement( -delta, 0, 0 );
                        break;
                    case left:
                        heroRotation = 180;
                        setMovement( 0, 0, -delta );
                        break;
                    case right:
                        heroRotation = 0;
                        setMovement( 0, 0, delta );
                        break;
                    case still:
                        walkComplete = 0;
                        movement = FirstOracleConstants.POSITION_ZERO_3F;
                        break;
                }
                lastState = walkingState;
            }
            if( !movement.equals( FirstOracleConstants.POSITION_ZERO_3F ) ) {
                walkComplete += delta / 2;
                hero.setPosition( hero.getPosition().add( movement ) );
                heroRotationController.setRotation( heroRotation );
                gameScene.updateCamera();
            }
        }
    }
    
    private void setMovement( float dx, float dy, float dz ) {
        movement = Position3D.pos3( dx, dy, dz );
    }
    
    private enum WalkingState {
        up,
        down,
        left,
        right,
        still,
        pause
    }
    
    private class ControllableFrameController implements FrameController {
        
        @Override
        public int getCurrentFrame( double lastFrameUpdate ) {
            return ( int ) Math.floor( 4 * walkComplete ) % 4;
        }
    }
}
