package com.firststory.firstoracle.controller;

import org.lwjgl.glfw.GLFW;

public class CameraKeyMap {

    private static CameraKeyMap alpabeticalLayout = new CameraKeyMap( GLFW.GLFW_KEY_W,
        GLFW.GLFW_KEY_S,
        GLFW.GLFW_KEY_A,
        GLFW.GLFW_KEY_D,
        GLFW.GLFW_KEY_SPACE,
        GLFW.GLFW_KEY_LEFT_CONTROL,
        GLFW.GLFW_KEY_Q,
        GLFW.GLFW_KEY_E,
        GLFW.GLFW_KEY_R,
        GLFW.GLFW_KEY_F,
        GLFW.GLFW_MOD_CONTROL,
        GLFW.GLFW_MOD_CONTROL );
    private static CameraKeyMap functionKeyLayout = new CameraKeyMap( GLFW.GLFW_KEY_UP,
        GLFW.GLFW_KEY_DOWN,
        GLFW.GLFW_KEY_LEFT,
        GLFW.GLFW_KEY_RIGHT,
        GLFW.GLFW_KEY_PAGE_UP,
        GLFW.GLFW_KEY_PAGE_DOWN,
        GLFW.GLFW_KEY_LEFT,
        GLFW.GLFW_KEY_RIGHT,
        GLFW.GLFW_KEY_UP,
        GLFW.GLFW_KEY_DOWN,
        0,
        GLFW.GLFW_MOD_CONTROL );

    public static CameraKeyMap getAlphabetKeyLayout() {
        return alpabeticalLayout;
    }

    public static CameraKeyMap getFunctionalKeyLayout() {
        return functionKeyLayout;
    }

    private int moveForwardKey;
    private int moveBackwardsKey;
    private int moveLeftKey;
    private int moveRightKey;
    private int moveUpKey;
    private int moveDownKey;
    private int rotateLeftKey;
    private int rotateRightKey;
    private int rotateUpKey;
    private int rotateDownKey;
    private int movementUnlockKeyMods;
    private int rotationUnlockKeyMods;

    public CameraKeyMap(
        int moveForwardKey,
        int moveBackwardsKey,
        int moveLeftKey,
        int moveRightKey,
        int moveUpKey,
        int moveDownKey,
        int rotateLeftKey,
        int rotateRightKey,
        int rotateUpKey,
        int rotateDownKey,
        int movementUnlockKeyMods,
        int rotationUnlockKeyMods
    )
    {
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
        this.movementUnlockKeyMods = movementUnlockKeyMods;
        this.rotationUnlockKeyMods = rotationUnlockKeyMods;
    }

    boolean shouldMoveDown( int key, int mods ) {
        return correctMovementMods( mods ) && key == moveDownKey;
    }

    boolean shouldMoveUp( int key, int mods ) {
        return correctMovementMods( mods ) && key == moveUpKey;
    }

    boolean shouldMoveLeft( int key, int mods ) {
        return correctMovementMods( mods ) && key == moveLeftKey;
    }

    boolean shouldMoveRight( int key, int mods ) {
        return correctMovementMods( mods ) && key == moveRightKey;
    }

    boolean shouldMoveBackwards( int key, int mods ) {
        return correctMovementMods( mods ) && key == moveBackwardsKey;
    }

    boolean shouldMoveForward( int key, int mods ) {
        return correctMovementMods( mods ) && key == moveForwardKey;
    }

    boolean shouldRotateRight( int key, int mods ) {
        return correctRotationMods( mods ) && key == rotateRightKey;
    }

    boolean shouldRotateLeft( int key, int mods ) {
        return correctRotationMods( mods ) && key == rotateLeftKey;
    }

    boolean shouldRotateDown( int key, int mods ) {
        return correctRotationMods( mods ) && key == rotateDownKey;
    }

    boolean shouldRotateUp( int key, int mods ) {
        return correctRotationMods( mods ) && key == rotateUpKey;
    }

    private boolean correctMovementMods( int mods ) {
        return ( movementUnlockKeyMods & mods ) == movementUnlockKeyMods;
    }

    private boolean correctRotationMods( int mods ) {
        return ( rotationUnlockKeyMods & mods ) == rotationUnlockKeyMods;
    }
}