/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.key.Key;
import com.firststory.firstoracle.key.KeyAction;
import com.firststory.firstoracle.key.KeyCode;
import com.firststory.firstoracle.key.KeyModificators;
import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class KeyEvent {
    private final WindowContext source;
    private Key key;
    public KeyEvent( WindowContext source, Key key ) {
        this.source = source;
        this.key = key;
    }
    
    public WindowContext getSource() {
        return source;
    }
    
    public Key getKey() {
        return key;
    }
    
    public KeyModificators getModificators() {
        return key.getModificators();
    }
    
    public KeyCode getKeyCode() {
        return key.getKeyCode();
    }
    
    public KeyAction getAction() {
        return key.getAction();
    }
    
    public boolean isReleaseAction() {
        return key.isReleaseAction();
    }
    
    private boolean isPressAction() {
        return key.isPressAction();
    }
    
    private boolean isRepeatedAction() {
        return key.isRepeatedAction();
    }
    
    public boolean isShiftDown() {
        return key.isShiftDown();
    }
    
    public boolean isControlDown() {
        return key.isControlDown();
    }
    
    public boolean isAltDown() {
        return key.isAltDown();
    }
    
    public boolean isSuperDown() {
        return key.isSuperDown();
    }
    
}