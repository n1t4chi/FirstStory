/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.notyfying.FpsListener;
import com.firststory.firstoracle.notyfying.FpsNotifier;
import com.firststory.firstoracle.text.TextObject2D;
import com.firststory.firstoracle.window.Window;

/**
 * @author n1t4chi
 */
public class FpsCounter extends TextObject2D implements FpsListener {
    
    public FpsCounter( Window window ) {
        super( window );
        window.addFpsListener( this );
    }
    
    @Override
    public void notify( int newFpsCount, FpsNotifier source ) {
        setText( "fps:" + newFpsCount );
    }
}
