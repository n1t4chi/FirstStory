/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.input;

/**
 * For compatibility reasons with input mapping use {@link InputActionCompatibility#isCompatible(InputAction) method}
 */
public enum InputAction implements InputActionCompatibility {
    PRESS, REPEAT, UNKNOWN, RELEASE, ANY;
    
    public boolean isPress() {
        return PRESS.equals( this ) || ANY.equals( this );
    }
    
    public boolean isRelease() {
        return RELEASE.equals( this ) || ANY.equals( this );
    }
    
    public boolean isRepeat() {
        return REPEAT.equals( this ) || ANY.equals( this );
    }
    
    public void doIfPress( Runnable runnable ) {
        if( isPress() ) {
            runnable.run();
        }
    }
    
    public void doIfRelease( Runnable runnable ) {
        if( isRelease() ) {
            runnable.run();
        }
    }
    
    public void doIfRepeat( Runnable runnable ) {
        if( isRepeat() ) {
            runnable.run();
        }
    }
}


