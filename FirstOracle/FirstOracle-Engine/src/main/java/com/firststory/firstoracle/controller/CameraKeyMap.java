/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.controller;

import com.firststory.firstoracle.input.*;

/**
 * @author n1t4chi
 */
public class CameraKeyMap {
    
    private static final CameraKeyMap alphabeticalLayout = new CameraKeyMap(
        Key.prepare( KeyCode.KEY_W ).build(),
        Key.prepare( KeyCode.KEY_S ).build(),
        Key.prepare( KeyCode.KEY_A ).build(),
        Key.prepare( KeyCode.KEY_D ).build(),
        Key.prepare( KeyCode.KEY_SPACE ).build(),
        Key.prepare( KeyCode.KEY_LEFT_CONTROL ).build(),
        Key.prepare( KeyCode.KEY_Q ).build(),
        Key.prepare( KeyCode.KEY_E ).build(),
        Key.prepare( KeyCode.KEY_R ).build(),
        Key.prepare( KeyCode.KEY_F ).build()
    );
    private static final CameraKeyMap functionKeyLayout = new CameraKeyMap(
        Key.prepare( KeyCode.KEY_UP ).build(),
        Key.prepare( KeyCode.KEY_DOWN ).build(),
        Key.prepare( KeyCode.KEY_LEFT ).build(),
        Key.prepare( KeyCode.KEY_RIGHT ).build(),
        Key.prepare( KeyCode.KEY_PAGE_UP ).build(),
        Key.prepare( KeyCode.KEY_PAGE_DOWN ).build(),
        Key.prepare( KeyCode.KEY_LEFT ).setModificators( InputModificator.CONTROL ).build(),
        Key.prepare( KeyCode.KEY_RIGHT ).setModificators( InputModificator.CONTROL ).build(),
        Key.prepare( KeyCode.KEY_UP ).setModificators( InputModificator.CONTROL ).build(),
        Key.prepare( KeyCode.KEY_DOWN ).setModificators( InputModificator.CONTROL ).build()
    );
    
    public static CameraKeyMap getAlphabetKeyLayout() {
        return alphabeticalLayout;
    }
    
    public static CameraKeyMap getFunctionalKeyLayout() {
        return functionKeyLayout;
    }
    
    private final Key moveForwardKey;
    private final Key moveBackwardsKey;
    private final Key moveLeftKey;
    private final Key moveRightKey;
    private final Key moveUpKey;
    private final Key moveDownKey;
    private final Key rotateLeftKey;
    private final Key rotateRightKey;
    private final Key rotateUpKey;
    private final Key rotateDownKey;
    
    public CameraKeyMap(
        Key moveForwardKey,
        Key moveBackwardsKey,
        Key moveLeftKey,
        Key moveRightKey,
        Key moveUpKey,
        Key moveDownKey,
        Key rotateLeftKey,
        Key rotateRightKey,
        Key rotateUpKey,
        Key rotateDownKey
    ) {
        this.moveForwardKey = moveForwardKey;
        this.moveBackwardsKey = moveBackwardsKey;
        this.moveLeftKey = moveLeftKey;
        this.rotateRightKey = rotateRightKey;
        this.moveUpKey = moveUpKey;
        this.moveDownKey = moveDownKey;
        this.rotateLeftKey = rotateLeftKey;
        this.rotateDownKey = rotateDownKey;
        this.rotateUpKey = rotateUpKey;
        this.moveRightKey = moveRightKey;
    }
    
    boolean shouldMoveDown( Key key ) {
        return moveDownKey.equals( key );
    }
    
    boolean shouldMoveUp( Key key ) {
        return moveUpKey.equals( key );
    }
    
    boolean shouldMoveLeft( Key key ) {
        return moveLeftKey.equals( key );
    }
    
    boolean shouldMoveRight( Key key ) {
        return moveRightKey.equals( key );
    }
    
    boolean shouldMoveBackwards( Key key ) {
        return moveBackwardsKey.equals( key );
    }
    
    boolean shouldMoveForward( Key key ) {
        return moveForwardKey.equals( key );
    }
    
    boolean shouldRotateRight( Key key ) {
        return rotateRightKey.equals( key );
    }
    
    boolean shouldRotateLeft( Key key ) {
        return rotateLeftKey.equals( key );
    }
    
    boolean shouldRotateDown( Key key ) {
        return rotateDownKey.equals( key );
    }
    
    boolean shouldRotateUp( Key key ) {
        return rotateUpKey.equals( key );
    }
}