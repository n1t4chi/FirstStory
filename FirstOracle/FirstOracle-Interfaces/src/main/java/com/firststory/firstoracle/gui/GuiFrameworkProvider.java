/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.gui;

import com.firststory.firstoracle.FrameworkProvider;
import com.firststory.firstoracle.window.WindowContext;

/**
 * @author n1t4chi
 */
public interface GuiFrameworkProvider< GuiData extends GuiApplicationData< ? > > extends FrameworkProvider {
    GuiFramework provide( WindowContext window, GuiData guiApplicationData );
}
