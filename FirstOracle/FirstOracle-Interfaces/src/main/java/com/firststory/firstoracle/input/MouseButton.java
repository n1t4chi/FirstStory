/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import java.util.*;

/**
 * @author n1t4chi
 */
public class MouseButton {
    
    private static final HashMap<Integer, MouseButton> mouseButtonHashMap = new HashMap<>( 100 );
    
    public static MouseButtonBuilder prepare(MouseButtonCode mouseButtonCode){
        return new MouseButtonBuilder(mouseButtonCode);
    }
    public static MouseButton create(MouseButtonCode mouseButtonCode, InputAction action, InputModificators modificators ){
        return mouseButtonHashMap.computeIfAbsent(
            Objects.hash( mouseButtonCode, action, modificators ),
            ignored -> new MouseButton(mouseButtonCode, action, modificators )
        );
    }
    
    private final MouseButtonCode mouseButtonCode;
    private final InputAction action;
    private final InputModificators modificators;
    
    private MouseButton( MouseButtonCode mouseButtonCode, InputAction action, InputModificators modificators ) {
        this.mouseButtonCode = mouseButtonCode;
        this.action = action;
        this.modificators = modificators;
    }
    
    //getters
    
    public InputModificators getModificators() {
        return modificators;
    }
    
    public MouseButtonCode getMouseButtonCode() {
        return mouseButtonCode;
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
        return "MouseButton:{code:"+mouseButtonCode+", action:"+action+", mods:"+modificators+"}";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash( this.mouseButtonCode, /*mouseButtonAction,*/ this.modificators );
    }
    
    @Override
    public boolean equals( Object obj ) {
        if(obj instanceof MouseButton) {
            var mouseButton = ( MouseButton ) obj;
            return
                mouseButtonCode.equals( mouseButton.mouseButtonCode ) &&
                    action.isCompatible( mouseButton.action ) &&
                    modificators.equals( mouseButton.modificators )
                ;
        }
        return false;
    }
    
    public static class MouseButtonBuilder{
        private final MouseButtonCode mouseButtonCode;
        private InputAction action = InputAction.ANY;
        private InputModificators modificators = InputModificators.empty();
        
        private MouseButtonBuilder( MouseButtonCode mouseButtonCode ) {
            this.mouseButtonCode = mouseButtonCode;
        }
        
        public MouseButtonBuilder setAction( InputAction action ) {
            this.action = action;
            return this;
        }
        
        public MouseButtonBuilder setModificators( InputModificator... modificators) {
            this.modificators = InputModificators.create(modificators);
            return this;
        }
        
        public MouseButton build(){
            return create( mouseButtonCode, action, modificators );
        }
    }
}
