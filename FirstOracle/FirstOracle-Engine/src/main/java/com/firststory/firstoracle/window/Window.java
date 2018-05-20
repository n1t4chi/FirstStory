/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.window;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.FrameworkProviderContext;
import com.firststory.firstoracle.PropertiesUtil;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.notyfying.*;
import com.firststory.firstoracle.rendering.Renderer;
import com.firststory.firstoracle.rendering.RenderingFramework;
import com.firststory.firstoracle.rendering.RenderingFrameworkProvider;
import com.firststory.firstoracle.window.jfxgl.JfxglContext;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author n1t4chi
 */
public class Window implements Runnable, TimeNotifier, WindowListener, QuitNotifier,
    FpsNotifier
{
    
    private static Logger logger = FirstOracleConstants.getLogger( Window.class );
    private static Logger performanceLogger = Logger.getLogger( Window.class.getName()+"#performance" );
    
    private static final AtomicInteger instanceCounter = new AtomicInteger( 0 );
    private final WindowSettings settings;
    private final ArrayList< TimeListener > timeListeners = new ArrayList<>( 3 );
    private final ArrayList< QuitListener > quitListeners = new ArrayList<>( 3 );
    private final ArrayList< FpsListener > fpsListeners = new ArrayList<>( 5 );
    private final Application application;
    private final Renderer renderer;
    private WindowContext window;
    private final WindowFrameworkProvider windowFrameworkProvider;
    private final RenderingFrameworkProvider renderingFrameworkProvider;
    private WindowFramework windowFramework;
    private RenderingFramework renderingFramework;
    private JfxglContext jfxgl;
    private int frameCount;
    private double lastFrameUpdate;
    private double lastFpsUpdate;
    private int lastFps;
    
    public static Window createWindow(
        WindowSettings windowSettings,
        Application application,
        Renderer renderer
    ) {
        return new Window( windowSettings, application, renderer,
            FrameworkProviderContext.getWindowFrameworkProvider(),
            FrameworkProviderContext.getRenderingFrameworkProvider() );
    }
    
    public static Window createWindow(
        WindowSettings windowSettings,
        Renderer renderer
    ) {
        return new Window( windowSettings, null, renderer,
            FrameworkProviderContext.getWindowFrameworkProvider(),
            FrameworkProviderContext.getRenderingFrameworkProvider() );
    }
    
    public static Window createWindow(
        WindowSettings windowSettings,
        Application application,
        Renderer renderer,
        WindowFrameworkProvider windowFrameworkProvider,
        RenderingFrameworkProvider renderingFrameworkProvider
    ) {
        return new Window( windowSettings, application, renderer, windowFrameworkProvider,renderingFrameworkProvider );
    }
    
    public static Window createWindow(
        WindowSettings windowSettings,
        Renderer renderer,
        WindowFrameworkProvider windowFrameworkProvider,
        RenderingFrameworkProvider renderingFrameworkProvider
    ) {
        return new Window( windowSettings, null, renderer, windowFrameworkProvider,renderingFrameworkProvider );
    }
    
    private Window(
        WindowSettings windowSettings,
        Application application,
        Renderer renderer,
        WindowFrameworkProvider windowFrameworkProvider,
        RenderingFrameworkProvider renderingFrameworkProvider
    ) {
        this.settings = windowSettings;
        this.application = application;
        this.renderer = renderer;
        this.windowFrameworkProvider = windowFrameworkProvider;
        this.renderingFrameworkProvider = renderingFrameworkProvider;
    }
    
    public void init() {
        try {
            windowFramework = windowFrameworkProvider.getWindowFramework();
            logger.finest( this+": Window context: "+ windowFramework );
            window = windowFramework.createWindowContext( settings, renderingFrameworkProvider.isOpenGL() );
            renderingFramework = renderingFrameworkProvider.getRenderingFramework( window );
            logger.finest( this+": Rendering context: "+renderingFramework );
            renderingFramework.invoke( instance -> {
                setupCallbacks();
                instance.compileShaders();
                renderer.init();
            });
    
            frameCount = 0;
            lastFps = 0;
            lastFrameUpdate = windowFramework.getTime();
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
                jfxgl = JfxglContext.createInstance( window.getAddress(), new String[]{}, application );
                logger.finest( this+": GUI context: "+jfxgl );
            });
            
            createRenderLoop().loop();
            notifyQuitListeners();
        } catch ( Exception ex ) {
            ex.printStackTrace();
            throw new RuntimeException( ex );
        } finally {
            close();
        }
    }
    
    private RenderLoopInterface createRenderLoop() {
        RenderLoopInterface renderLoop = new RenderLoop();
        if( PropertiesUtil.isPropertyTrue( PropertiesUtil.RENDER_LOOP_PERFORMANCE_LOG_PROPERTY ) )
            renderLoop = new RenderLoopPerformanceTester( renderLoop );
        if( PropertiesUtil.isPropertyTrue( PropertiesUtil.FORCE_ONE_LOOP_CYCLE_PROPERTY ))
            renderLoop = new RenderLoopEarlyExit( renderLoop );
        return renderLoop;
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
    
    private class RenderLoopEarlyExit extends RenderLoopDelegate {
    
        RenderLoopEarlyExit( RenderLoopInterface delegate ) {
            super( delegate );
        }
    
        @Override
        public void loopInside() throws Exception {
            super.loopInside();
            quit();
        }
    }
    
    private class RenderLoopPerformanceTester extends RenderLoopDelegate {
        RenderLoopPerformanceTester( RenderLoopInterface delegate ) {
            super( delegate );
        }
    
        @Override
        public void loopInside() throws Exception {
            performanceLogger.finest( this+": loop cycle start" );
            super.loopInside();
            performanceLogger.finest( this+": loop cycle end" );
        }
    
        @Override
        public void render() throws Exception {
            performanceLogger.finest( Window.this+": loop render start" );
            super.render();
            performanceLogger.finest( Window.this+": loop render start" );
        }
    }
    
    private class RenderLoopDelegate extends RenderLoopInterface {
        private final RenderLoopInterface delegate;
    
        RenderLoopDelegate( RenderLoopInterface delegate ) {
            this.delegate = delegate;
        }
    
        @Override
        public void loopInside() throws Exception {
            delegate.loopInside();
        }
    
        @Override
        public void render() throws Exception {
            delegate.render();
        }
    
        @Override
        public void setupCycle() {
            delegate.setupCycle();
        }
    }
    
    private class RenderLoop extends RenderLoopInterface {
        @Override
        public void loopInside() throws Exception {
            setupCycle();
            render();
        }
    
        @Override
        public void render() throws Exception {
            renderingFramework.invoke( instance -> {
                notifyTimeListener( windowFramework.getTime() );
                window.setUpSingleRender();
    
                renderingFramework.setUpSingleRender();
                renderer.render( instance.getRenderingContext(), lastFrameUpdate );
                renderingFramework.tearDownSingleRender();
    
                jfxgl.render();
                window.tearDownSingleRender();
            } );
        }
    
        @Override
        public void setupCycle() {
            lastFrameUpdate = windowFramework.getTime();
            if ( frameCount % 100 == 0 ) {
                lastFps = ( int ) ( ( float ) frameCount / ( lastFrameUpdate - lastFpsUpdate ) );
                lastFpsUpdate = lastFrameUpdate;
                frameCount = 0;
                notifyFpsListeners( lastFps );
            }
            frameCount++;
        }
    }
    
    private abstract class RenderLoopInterface {
        void loop() throws Exception{
            while ( !shouldWindowClose() ) {
                loopInside();
            }
        }
        abstract void loopInside() throws Exception;
        abstract void render() throws Exception;
        abstract void setupCycle();
    }
    
    
    @Override
    public String toString() {
        return "FirstOracle Window@"+hashCode();
    }
}
