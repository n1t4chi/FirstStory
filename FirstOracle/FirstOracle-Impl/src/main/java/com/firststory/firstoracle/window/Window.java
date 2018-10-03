/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.gui.GuiApplicationData;
import com.firststory.firstoracle.gui.GuiFramework;
import com.firststory.firstoracle.gui.GuiFrameworkProvider;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class Window implements TimeNotifier, WindowListener, QuitNotifier, FpsNotifier {
    
    private static final AtomicInteger instanceCounter = new AtomicInteger( 0 );
    private static final Logger logger = FirstOracleConstants.getLogger( Window.class );
    
    private final WindowSettings settings;
    private final ArrayList< TimeListener > timeListeners = new ArrayList<>( 3 );
    private final ArrayList< QuitListener > quitListeners = new ArrayList<>( 3 );
    private final ArrayList< FpsListener > fpsListeners = new ArrayList<>( 5 );
    private final GuiApplicationData< ? > guiApplicationData;
    private final Renderer renderer;
    private final WindowFrameworkProvider windowFrameworkProvider;
    private final RenderingFrameworkProvider renderingFrameworkProvider;
    private final GuiFrameworkProvider< ? > guiFrameworkProvider;
    private final RenderLoop renderLoop;
    private WindowContext window;
    private WindowFramework windowFramework;
    private RenderingFramework renderingFramework;
    private GuiFramework guiFramework;
    private int frameCount;
    private double lastFrameUpdate;
    private double lastFpsUpdate;
    private int lastFps;
    
    public Window(
        WindowSettings windowSettings,
        GuiApplicationData< ? > guiApplicationData,
        Renderer renderer,
        WindowFrameworkProvider windowFrameworkProvider,
        RenderingFrameworkProvider renderingFrameworkProvider,
        GuiFrameworkProvider< ? > guiFrameworkProvider,
        RenderLoop renderLoop
    ) {
        this.settings = windowSettings;
        this.guiApplicationData = guiApplicationData;
        this.renderer = renderer;
        this.windowFrameworkProvider = windowFrameworkProvider;
        this.renderingFrameworkProvider = renderingFrameworkProvider;
        this.guiFrameworkProvider = guiFrameworkProvider;
        this.renderLoop = renderLoop;
    }
    
    public void init() {
        try {
            windowFramework = windowFrameworkProvider.getWindowFramework();
            logger.finest( this + ": Window context: " + windowFramework );
            window = windowFramework.createWindowContext( settings, renderingFrameworkProvider.isOpenGL() );
            renderingFramework = renderingFrameworkProvider.getRenderingFramework( window );
            logger.finest( this + ": Rendering context: " + renderingFramework );
            renderingFramework.invoke( instance -> {
                setupCallbacks();
                instance.compileShaders();
                renderer.init();
            } );
            
            frameCount = 0;
            lastFps = 0;
            lastFrameUpdate = windowFramework.getTime();
            lastFpsUpdate = lastFrameUpdate;
        } catch ( Exception ex ) {
            close(); //do not place in finally!
            throw new RuntimeException( ex );
        }
    }
    
//    @Override
    @SuppressWarnings( "unchecked" )
    public void run() {
        Thread.currentThread().setName( "Window" + instanceCounter.getAndIncrement() );
        try {
            renderingFramework.invoke( instance -> {
                window.show();
                guiFramework = (( GuiFrameworkProvider< GuiApplicationData<?>> ) guiFrameworkProvider).provide( window, guiApplicationData );
                logger.finest( this + ": GUI context: " + guiFramework );
            } );
            
            renderLoop.loop( this, renderingFramework );
            notifyQuitListeners();
        } catch ( Exception ex ) {
//            ex.printStackTrace();
            throw new RuntimeException( ex );
        } finally {
            close();
        }
    }
    
    /**
     * Returns whether the window should close before next rendering cycle.
     * <p>
     * If this method is overridden then also {@link #quit()} needs to be overridden
     * or there will be inconsistencies.
     *
     * @return true when window should close
     */
    public boolean shouldWindowClose() {
        return window.shouldClose();
    }
    
    /**
     * Notifies window that it should stop rendering and close itself.
     * <p>
     * If this method is overridden then also {@link #shouldWindowClose()} needs to be overridden
     * or there will be inconsistencies.
     */
    public void quit() {
        window.quit();
    }
    
    @Override
    public Collection< FpsListener > getFpsListeners() {
        return fpsListeners;
    }
    
    @Override
    public Collection< TimeListener > getTimeListeners() {
        return timeListeners;
    }
    
    @Override
    public Collection< QuitListener > getQuitListeners() {
        return quitListeners;
    }
    
    public void addKeyListener( KeyListener listener ) {
        window.addKeyListener( listener );
    }
    
    public void addMouseListener( MouseListener listener ) {
        window.addMouseListener( listener );
    }
    
    public void addWindowListener( WindowListener listener ) {
        window.addWindowListener( listener );
    }
    
    public void addJoystickListener( JoystickListener listener ) {
        window.addJoystickListener( listener );
    }
    
    @Override
    public void notify( WindowSizeEvent event ) {
        renderingFramework.updateViewPort( 0, 0, event.getWidth(), event.getHeight() );
        settings.setWidth( event.getWidth() );
        settings.setHeight( event.getHeight() );
    }
    
    @Override
    public void notify( WindowCloseEvent event ) {
    
    }
    
    @Override
    public String toString() {
        return "FirstOracle Window@" + hashCode();
    }
    
    public void invokedRender( RenderingFramework instance ) {
        notifyTimeListener( windowFramework.getTime() );
        window.setUpSingleRender();
        
        instance.render( renderer, lastFrameUpdate );
        
        guiFramework.render();
        window.tearDownSingleRender();
    }
    
    public void setupRenderCycleVariables() {
        lastFrameUpdate = windowFramework.getTime();
        if ( frameCount % 100 == 0 ) {
            lastFps = ( int ) ( ( float ) frameCount / ( lastFrameUpdate - lastFpsUpdate ) );
            lastFpsUpdate = lastFrameUpdate;
            frameCount = 0;
            notifyFpsListeners( lastFps );
        }
        frameCount++;
    }
    
    private void setupCallbacks() {
        window.addWindowListener( this );
    }
    
    private void close() {
        if ( window != null ) { window.destroy(); }
    }
    
}
