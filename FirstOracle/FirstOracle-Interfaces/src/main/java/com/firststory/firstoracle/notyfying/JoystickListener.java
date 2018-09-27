/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

/**
 * @author n1t4chi
 */
public interface JoystickListener {
    
    default void notify( JoystickEvent joystickEvent ){}
}
