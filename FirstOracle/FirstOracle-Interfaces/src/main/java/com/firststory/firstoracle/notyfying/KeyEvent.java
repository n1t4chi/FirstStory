/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class KeyEvent {
    private final WindowContext source;
    private final Key key;
    
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
    
    public InputModificators getModificators() {
        return key.getModificators();
    }
    
    public KeyCode getKeyCode() {
        return key.getKeyCode();
    }
    
    public InputAction getAction() {
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
