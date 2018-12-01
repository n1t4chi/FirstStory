/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.window;

import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.gui.GuiFramework;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.rendering.*;

/**
 * @author n1t4chi
 */
public interface Window extends TimeNotifier, WindowListener, QuitNotifier, FpsNotifier {
    
    RenderingFramework getRenderingFramework();
    
    GuiFramework getGuiFramework();
    
    WindowFramework getWindowFramework();
    
    void init();
    
    RenderLoop getRenderLoop();
    
    WindowContext getContext();
    
    WindowSettings getSettings();
    
    Renderer getRenderer();
    
    double getLastFrameUpdate();
    
    double getTime();
    
    void setupRenderCycleVariables();
    
    void setUpRunInsideRenderingFramework();
    
    void setUpRun();
    
    void tearDownRun();
    
    void close();
    
    default void run() {
        setUpRun();
        try {
            getRenderingFramework().invoke( instance -> setUpRunInsideRenderingFramework() );
            
            getRenderLoop().loop( this, getRenderingFramework() );
            notifyQuitListeners();
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        } finally {
            close();
        }
        tearDownRun();
    }
    
    /**
     * Returns whether the window should close before next rendering cycle.
     * <p>
     * If this method is overridden then also {@link #quit()} needs to be overridden
     * or there will be inconsistencies.
     *
     * @return true when window should close
     */
    default boolean shouldWindowClose() {
        return getContext().shouldClose();
    }
    
    /**
     * Notifies window that it should stop rendering and close itself.
     * <p>
     * If this method is overridden then also {@link #shouldWindowClose()} needs to be overridden
     * or there will be inconsistencies.
     */
    default void quit() {
        getContext().quit();
    }
    
    default void addKeyListener( KeyListener listener ) {
        getContext().addKeyListener( listener );
    }
    
    default void addMouseListener( MouseListener listener ) {
        getContext().addMouseListener( listener );
    }
    
    default void addWindowListener( WindowListener listener ) {
        getContext().addWindowListener( listener );
    }
    
    default void addJoystickListener( JoystickListener listener ) {
        getContext().addJoystickListener( listener );
    }
    
    @Override
    default void notify( WindowSizeEvent event ) {
        getRenderingFramework().updateViewPort( 0, 0, event.getWidth(), event.getHeight() );
        getSettings().setWidth( event.getWidth() );
        getSettings().setHeight( event.getHeight() );
    }
    
    @Override
    default void notify( WindowCloseEvent event ) {
    }
    
    default void invokedRender( RenderingFramework instance ) {
        notifyTimeListener( getTime() );
        getContext().setUpSingleRender();
        
        instance.render( getRenderer(), getLastFrameUpdate() );
        
        getGuiFramework().render();
        getContext().tearDownSingleRender();
    }
    
    default void setupCallbacks() {
        getContext().addWindowListener( this );
    }
}
