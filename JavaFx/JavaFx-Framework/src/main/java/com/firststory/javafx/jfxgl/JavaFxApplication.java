/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.javafx.jfxgl;

import com.firststory.firstoracle.window.GuiApplicationData;
import javafx.application.Application;

/**
 * @author n1t4chi
 */
public interface JavaFxApplication extends GuiApplicationData< Application> {
    
    @Override
    Application getData();
}
