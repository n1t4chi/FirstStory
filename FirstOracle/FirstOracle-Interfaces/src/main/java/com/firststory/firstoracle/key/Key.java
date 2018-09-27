/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.key;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author n1t4chi
 */
public class Key {
    private static final HashMap<Integer, Key> keyHashMap = new HashMap<>( 100 );
    
    public static KeyBuilder prepare(KeyCode keyCode){
        return new KeyBuilder(keyCode);
    }
    public static Key create(KeyCode keyCode, KeyAction action, KeyModificators modificators ){
        return keyHashMap.computeIfAbsent(
            Objects.hash( keyCode, action, modificators ),
            ignored -> new Key(keyCode, action, modificators )
        );
    }
    
    private final KeyCode keyCode;
    private final KeyAction action;
    private final KeyModificators modificators;
    
    private Key( KeyCode keyCode, KeyAction action, KeyModificators modificators ) {
        this.keyCode = keyCode;
        this.action = action;
        this.modificators = modificators;
    }
    
    //getters
    
    public KeyModificators getModificators() {
        return modificators;
    }
    
    public KeyCode getKeyCode() {
        return keyCode;
    }
    
    public KeyAction getAction() {
        return action;
    }
    
    //useful methods
    
    public boolean isReleaseAction() {
        return action == KeyAction.RELEASE;
    }
    
    public boolean isPressAction() {
        return action == KeyAction.PRESS;
    }
    
    public boolean isRepeatedAction() {
        return action == KeyAction.REPEAT;
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
        private KeyAction action = KeyAction.ANY;
        private KeyModificators modificators = KeyModificators.empty();
    
        private KeyBuilder( KeyCode keyCode ) {
            this.keyCode = keyCode;
        }
    
        public KeyBuilder setAction( KeyAction action ) {
            this.action = action;
            return this;
        }
        
        public KeyBuilder setModificators( KeyModificator... modificators) {
            this.modificators = KeyModificators.create(modificators);
            return this;
        }
        
        public Key build(){
            return create( keyCode, action, modificators );
        }
    }
}
