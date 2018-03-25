/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.key;
/**
 * For compatibility reasons with key mapping use {@link KeyCompatibility#isCompatible(KeyAction) method}
 */
public enum KeyAction implements KeyCompatibility {
    PRESS, REPEAT, UNKNOWN, RELEASE, ANY
}


