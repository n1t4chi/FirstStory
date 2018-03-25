package com.firststory.firstoracle.window.notifying;

import com.firststory.firstoracle.Key;
import com.firststory.firstoracle.KeyAction;
import com.firststory.firstoracle.KeyCode;
import com.firststory.firstoracle.KeyModificators;
import com.firststory.firstoracle.window.GLFW.GlfwWindow;

public class KeyEvent {
    private final GlfwWindow source;
    private Key key;
    public KeyEvent( GlfwWindow source, Key key ) {
        this.source = source;
        this.key = key;
    }
    
    public GlfwWindow getSource() {
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
