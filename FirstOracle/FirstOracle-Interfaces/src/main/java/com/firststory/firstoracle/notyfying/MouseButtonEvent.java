/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.input.*;
import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class MouseButtonEvent {
    
    private final WindowContext source;
    private final MouseButton mouseButton;
    
    public MouseButtonEvent( WindowContext source, MouseButton mouseButton ) {
        this.source = source;
        this.mouseButton = mouseButton;
    }
    
    public WindowContext getSource() {
        return source;
    }
    
    public MouseButton getMouseButton() {
        return mouseButton;
    }
    
    public InputModificators getModificators() {
        return mouseButton.getModificators();
    }
    
    public MouseButtonCode getMouseButtonCode() {
        return mouseButton.getMouseButtonCode();
    }
    
    public InputAction getAction() {
        return mouseButton.getAction();
    }
    
    public boolean isReleaseAction() {
        return mouseButton.isReleaseAction();
    }
    
    private boolean isPressAction() {
        return mouseButton.isPressAction();
    }
    
    private boolean isRepeatedAction() {
        return mouseButton.isRepeatedAction();
    }
    
    public boolean isShiftDown() {
        return mouseButton.isShiftDown();
    }
    
    public boolean isControlDown() {
        return mouseButton.isControlDown();
    }
    
    public boolean isAltDown() {
        return mouseButton.isAltDown();
    }
    
    public boolean isSuperDown() {
        return mouseButton.isSuperDown();
    }
}
