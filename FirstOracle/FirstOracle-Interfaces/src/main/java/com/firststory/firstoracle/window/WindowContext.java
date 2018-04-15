/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.notyfying.JoystickListener;
import com.firststory.firstoracle.notyfying.KeyListener;
import com.firststory.firstoracle.notyfying.MouseListener;
import com.firststory.firstoracle.notyfying.WindowListener;

/**
 * @author n1t4chi
 */
public interface WindowContext {
    
    void setVerticalSync( boolean enabled );
    
    long getAddress();
    
    void show();
    
    void destroy();
    
    boolean shouldClose();
    
    void quit();
    
    void addKeyListener( KeyListener listener );
    
    void addMouseListener( MouseListener listener );
    
    void addWindowListener( WindowListener listener );
    
    void addJoystickListener( JoystickListener listener );
    
    void setUpRenderLoop();
    
    void cleanAfterLoop();
    
    void setupVerticalSync();
    
    void setWindowToCurrentThread();
}
