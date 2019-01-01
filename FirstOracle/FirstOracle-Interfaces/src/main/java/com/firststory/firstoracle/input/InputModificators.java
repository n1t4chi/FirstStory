/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

import java.util.*;

/**
 * @author n1t4chi
 */
public class InputModificators {
    private static final HashMap<Integer, InputModificators > keyModMap = new HashMap<>( 16 );
    private static final InputModificators EMPTY = new InputModificators( false,false,false,false );

    static {
        keyModMap.put( EMPTY.hashCode(), EMPTY );
    }

    public static InputModificators empty() {
        return EMPTY;
    }

    public static InputModificators create( InputModificator... modificators){
        var isAltDown = false;
        var isShiftDown = false;
        var isControlDown = false;
        var isSuperDown = false;
        for( var km : modificators ){
            switch(km){
                case ALT: isAltDown = true; break;
                case CONTROL: isControlDown = true; break;
                case SHIFT: isShiftDown = true; break;
                case SUPER: isSuperDown = true; break;
            }
        }
        return create( isAltDown,isShiftDown,isControlDown,isSuperDown );
    }
    public static InputModificators create( boolean isAltDown, boolean isShiftDown, boolean isControlDown, boolean isSuperDown ) {
        return keyModMap.computeIfAbsent(
            hashParams( isAltDown,isShiftDown,isControlDown,isSuperDown ),
            ignored -> new InputModificators( isAltDown, isShiftDown, isControlDown, isSuperDown )
        );
    }
    
    private static int hashParams( boolean isAltDown, boolean isShiftDown, boolean isControlDown, boolean isSuperDown ) {
        return ( isAltDown ? 1 : 0 )
            +( isShiftDown ? 2 : 0 )
            +( isControlDown ? 4 : 0 )
            +( isSuperDown ? 8 : 0 );
    }
    private final boolean isAltDown;
    private final boolean isShiftDown;
    private final boolean isControlDown;
    private final boolean isSuperDown;
    private final InputModificator[] modificators;
    
    private InputModificators(boolean isAltDown, boolean isShiftDown, boolean isControlDown, boolean isSuperDown){
        this.isAltDown = isAltDown;
        this.isShiftDown = isShiftDown;
        this.isControlDown = isControlDown;
        this.isSuperDown = isSuperDown;
        this.modificators = new InputModificator[
            ( isShiftDown ? 1 : 0 ) +
            ( isControlDown ? 1 : 0 ) +
            ( isAltDown ? 1 : 0 ) +
            ( isSuperDown ? 1 : 0 )
        ];
        var pos = 0;
        if( this.isShiftDown){ this.modificators[pos++] = InputModificator.SHIFT; }
        if( this.isControlDown){ this.modificators[pos++] = InputModificator.CONTROL; }
        if( this.isAltDown){ this.modificators[pos++] = InputModificator.ALT; }
        if( this.isSuperDown){ this.modificators[pos] = InputModificator.SUPER; }
    }
    
    public boolean isShiftDown() {
        return isShiftDown;
    }
    
    public boolean isControlDown() {
        return isControlDown;
    }
    
    public boolean isAltDown() {
        return isAltDown;
    }
    
    public boolean isSuperDown() {
        return isSuperDown;
    }
    
    public InputModificator[] getModificators() {
        return modificators;
    }
    
    @Override
    public int hashCode() {
        return hashParams( this.isAltDown, this.isShiftDown, this.isControlDown, this.isSuperDown );
    }
    
    @Override
    public boolean equals( Object obj ) {
        if(obj instanceof InputModificators ) {
            var mods = ( InputModificators ) obj;
            return
                isAltDown == mods.isAltDown &&
                isControlDown == mods.isControlDown &&
                isShiftDown == mods.isShiftDown &&
                isSuperDown == mods.isSuperDown
            ;
        }
        return false;
    }

    @Override
    public String toString() {
        return "KeyModificators"+Arrays.toString(modificators);
    }
}
