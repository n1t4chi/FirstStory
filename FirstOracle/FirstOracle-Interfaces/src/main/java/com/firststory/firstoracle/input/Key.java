/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import java.util.*;

/**
 * @author n1t4chi
 */
public class Key {
    private static final HashMap<Integer, Key> keyHashMap = new HashMap<>( 100 );
    
    public static KeyBuilder prepare(KeyCode keyCode){
        return new KeyBuilder(keyCode);
    }
    public static Key create(KeyCode keyCode, InputAction action, InputModificators modificators ){
        return keyHashMap.computeIfAbsent(
            Objects.hash( keyCode, action, modificators ),
            ignored -> new Key(keyCode, action, modificators )
        );
    }
    
    private final KeyCode keyCode;
    private final InputAction action;
    private final InputModificators modificators;
    
    private Key( KeyCode keyCode, InputAction action, InputModificators modificators ) {
        this.keyCode = keyCode;
        this.action = action;
        this.modificators = modificators;
    }
    
    //getters
    
    public InputModificators getModificators() {
        return modificators;
    }
    
    public KeyCode getKeyCode() {
        return keyCode;
    }
    
    public InputAction getAction() {
        return action;
    }
    
    //useful methods
    
    public boolean isReleaseAction() {
        return action == InputAction.RELEASE;
    }
    
    public boolean isPressAction() {
        return action == InputAction.PRESS;
    }
    
    public boolean isRepeatedAction() {
        return action == InputAction.REPEAT;
    }
    
    public boolean isShiftDown() {
        return modificators.isShiftDown();
    }
    
    public boolean isControlDown() {
        return modificators.isControlDown();
    }
    
    public boolean isAltDown() {
        return modificators.isAltDown();
    }
    
    public boolean isSuperDown() {
        return modificators.isSuperDown();
    }
    
    @Override
    public String toString() {
        return "Key:{code:"+keyCode+", action:"+action+", mods:"+modificators+"}";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash( this.keyCode, /*keyAction,*/ this.modificators );
    }
    
    @Override
    public boolean equals( Object obj ) {
        if(obj instanceof Key) {
            var key = ( Key ) obj;
            return
                keyCode.equals( key.keyCode ) &&
                action.isCompatible( key.action ) &&
                modificators.equals( key.modificators )
            ;
        }
        return false;
    }
    
    public static class KeyBuilder{
        private final KeyCode keyCode;
        private InputAction action = InputAction.ANY;
        private InputModificators modificators = InputModificators.empty();
    
        private KeyBuilder( KeyCode keyCode ) {
            this.keyCode = keyCode;
        }
    
        public KeyBuilder setAction( InputAction action ) {
            this.action = action;
            return this;
        }
        
        public KeyBuilder setModificators( InputModificator... modificators) {
            this.modificators = InputModificators.create(modificators);
            return this;
        }
        
        public Key build(){
            return create( keyCode, action, modificators );
        }
    }
}
