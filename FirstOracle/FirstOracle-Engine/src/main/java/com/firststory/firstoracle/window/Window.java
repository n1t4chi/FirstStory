/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FrameworkProviderContext;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.window.glfw.GlfwContext;
import com.firststory.firstoracle.window.glfw.GlfwWindow;
import com.firststory.firstoracle.window.jfxgl.JfxglContext;
import com.firststory.firstoracle.window.notifying.*;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author n1t4chi
 */
public class Window implements Runnable,
    TimeNotifier,
    WindowListener,
    QuitNotifier,
    FpsNotifier
{
    
    private static final AtomicInteger instanceCounter = new AtomicInteger( 0 );
    private final WindowSettings settings;
    private final ArrayList< TimeListener > timeListeners = new ArrayList<>( 3 );
    private final ArrayList< QuitListener > quitListeners = new ArrayList<>( 3 );
    private final ArrayList< FpsListener > fpsListeners = new ArrayList<>( 5 );
    private final Application application;
    private final Renderer renderer;
    private GlfwWindow window;
    private final RenderingFrameworkProvider renderingFrameworkProvider;
    private GlfwContext glfw;
    private RenderingFramework renderingFramework;
    private JfxglContext jfxgl;
    private int frameCount;
    private double lastFrameUpdate;
    private double lastFpsUpdate;
    private int lastFps;
    
    public static Window getOpenGlWithJavaFxInstance(
        WindowSettings windowSettings,
        Application application,
        Renderer renderer
    ){
        return new Window( windowSettings, application, renderer,
            FrameworkProviderContext.getRenderingFrameworkProvider() );
    }
    
    public static Window getOpenGlInstance(
        WindowSettings windowSettings,
        Renderer renderer
    ){
        return new Window( windowSettings, null, renderer,
            FrameworkProviderContext.getRenderingFrameworkProvider() );
    }
    
    public static Window getOpenGlWithJavaFxInstance(
        WindowSettings windowSettings,
        Application application,
        Renderer renderer,
        RenderingFrameworkProvider renderingFrameworkProvider
    ){
        return new Window( windowSettings, application, renderer, renderingFrameworkProvider );
    }
    
    public static Window getOpenGlInstance(
        WindowSettings windowSettings,
        Renderer renderer,
        RenderingFrameworkProvider renderingFrameworkProvider
    ){
        return new Window( windowSettings, null, renderer, renderingFrameworkProvider );
    }
    
    private Window(
        WindowSettings windowSettings,
        Application application,
        Renderer renderer,
        RenderingFrameworkProvider renderingFrameworkProvider
    ) {
        this.settings = windowSettings;
        this.application = application;
        this.renderer = renderer;
        this.renderingFrameworkProvider = renderingFrameworkProvider;
    }
    
    public void init() {
        try {
            glfw = GlfwContext.createInstance();
            window = glfw.createWindow( settings, renderingFrameworkProvider.isOpenGL() );
            renderingFramework = renderingFrameworkProvider.getRenderingContext();
            renderingFramework.invoke( instance -> {
                setupCallbacks();
                instance.compileShaders();
                renderer.init();
            });
    
            frameCount = 0;
            lastFps = 0;
            lastFrameUpdate = glfw.getTime();
            lastFpsUpdate = lastFrameUpdate;
        } catch ( Exception ex ) {
            close(); //do not place in finally!
            throw new RuntimeException( ex );
        }
    }
    
    @Override
    public void run() {
        Thread.currentThread().setName( "Window" + instanceCounter.getAndIncrement()  );
        try {
            renderingFramework.invoke( instance -> {
                window.show();
                jfxgl = JfxglContext.createInstance( window.getID(), new String[]{}, application );
            });
            loop();
            notifyQuitListeners();
        } catch ( Exception ex ) {
            ex.printStackTrace();
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
    
    private void setupCallbacks() {
        window.addWindowListener( this );
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
    
    private void close() {
        if(window != null)
            window.destroy();
    }
    
    private void loop() throws Exception {
        while ( !shouldWindowClose() ) {
    
            lastFrameUpdate = glfw.getTime();
            if ( frameCount % 100 == 0 ) {
                lastFps = ( int ) ( ( float ) frameCount / ( lastFrameUpdate - lastFpsUpdate ) );
                lastFpsUpdate = lastFrameUpdate;
                frameCount = 0;
                notifyFpsListeners( lastFps );
            }
            frameCount++;
            
            renderingFramework.invoke( instance -> {
                window.setUpRenderLoop();
                renderingFramework.clearScreen();
                notifyTimeListener( glfw.getTime() );
                renderer.render( instance.getRenderingContext(), lastFrameUpdate );
                jfxgl.render();
                window.cleanAfterLoop();
            } );
            
        }
    }
}
