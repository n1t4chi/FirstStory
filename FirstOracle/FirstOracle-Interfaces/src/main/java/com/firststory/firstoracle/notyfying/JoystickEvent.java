/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.notyfying;

import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public class JoystickEvent {
    
    public final WindowContext source;
    public final int jid;
    public final int event;
    
    public JoystickEvent( WindowContext source, int jid, int event ) {
        this.source = source;
        this.jid = jid;
        this.event = event;
    }
}
