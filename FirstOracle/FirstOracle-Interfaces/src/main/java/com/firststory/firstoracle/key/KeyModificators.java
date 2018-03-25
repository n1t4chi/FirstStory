/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.key;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author n1t4chi
 */
public class KeyModificators {
    private static HashMap<Integer,KeyModificators> keyModMap = new HashMap<>( 16 );
    private static KeyModificators EMPTY = new KeyModificators( false,false,false,false );

    static {
        keyModMap.put( EMPTY.hashCode(), EMPTY );
    }

    public static KeyModificators empty() {
        return EMPTY;
    }

    public static KeyModificators create(KeyModificator... modificators){
        boolean isAltDown = false;
        boolean isShiftDown = false;
        boolean isControlDown = false;
        boolean isSuperDown = false;
        for(KeyModificator km : modificators ){
            switch(km){
                case ALT: isAltDown = true; break;
                case CONTROL: isControlDown = true; break;
                case SHIFT: isShiftDown = true; break;
                case SUPER: isSuperDown = true; break;
            }
        }
        return create( isAltDown,isShiftDown,isControlDown,isSuperDown );
    }
    public static KeyModificators create( boolean isAltDown, boolean isShiftDown, boolean isControlDown, boolean isSuperDown ) {
        return keyModMap.computeIfAbsent(
            hashParams( isAltDown,isShiftDown,isControlDown,isSuperDown ),
            ignored -> new KeyModificators( isAltDown, isShiftDown, isControlDown, isSuperDown )
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
    private final KeyModificator[] modificators;
    
    private KeyModificators(boolean isAltDown, boolean isShiftDown, boolean isControlDown, boolean isSuperDown){
        this.isAltDown = isAltDown;
        this.isShiftDown = isShiftDown;
        this.isControlDown = isControlDown;
        this.isSuperDown = isSuperDown;
        this.modificators = new KeyModificator[
            ( isShiftDown ? 1 : 0 ) +
            ( isControlDown ? 1 : 0 ) +
            ( isAltDown ? 1 : 0 ) +
            ( isSuperDown ? 1 : 0 )
        ];
        int pos = 0;
        if( this.isShiftDown){ this.modificators[pos++] = KeyModificator.SHIFT; }
        if( this.isControlDown){ this.modificators[pos++] = KeyModificator.CONTROL; }
        if( this.isAltDown){ this.modificators[pos++] = KeyModificator.ALT; }
        if( this.isSuperDown){ this.modificators[pos] = KeyModificator.SUPER; }
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
    
    public KeyModificator[] getModificators() {
        return modificators;
    }
    
    @Override
    public int hashCode() {
        return hashParams( this.isAltDown, this.isShiftDown, this.isControlDown, this.isSuperDown );
    }
    
    @Override
    public boolean equals( Object obj ) {
        if(obj instanceof KeyModificators) {
            KeyModificators mods = ( KeyModificators ) obj;
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
