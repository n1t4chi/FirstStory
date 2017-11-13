/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.util;

import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.RectangleGrid;
import com.firststory.firstoracle.object3D.CubeGrid;
import com.firststory.firstoracle.rendering.Object2DRenderer;
import com.firststory.firstoracle.rendering.Object3DRenderer;
import com.firststory.firstoracle.rendering.SceneProvider;
import com.firststory.firstoracle.scene.RenderedObjects2D;
import com.firststory.firstoracle.scene.RenderedObjects3D;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.*;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import cuchaz.jfxgl.JFXGLLauncher;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.joml.Vector3i;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class MainTemplate {

    //necessary main, you must run it like that or nothing works.
    //Every instance created before launchMain() will not be compatibile with any object made after
    //Even same classes will be seen as different
    //All JavaFX content must be created in OverlayContentManager due to JavaFX thread being spawned via jfxgl
    //All openGL bindings must be done after creating Window object and calling it's init() function
    //Also ALL THREADS must be created AFTER calling launchMain, Preferably after running Window thread
    public static void main( String[] args ) {
        JFXGLLauncher.showFilterWarnings = false;
        JFXGLLauncher.launchMain( MainTemplate.class, args );
    }

    //it's called by main above though some hack magicks called reflection
    public static void jfxglmain( String[] args ) {
        //Settings for window, you can switch height/widith, fullscreen, borderless and other magics.
        //VerticalSync disabled will uncap FPS.
        WindowSettings settings = new WindowSettings.WindowSettingsBuilder()
            .setVerticalSync( false )
            .setResizeable( true )
            .setWidth( 1000 )
            .setHeight( 800 )
            .build();
        //Those shader programs are necessary. For now I didn't remove anything with 3D so it will need to be left as it is.
        ShaderProgram3D shaderProgram3D = new ShaderProgram3D();
        ShaderProgram2D shaderProgram2D = new ShaderProgram2D();
        //GridRenderer will be changed so it works as either 2D or 3D. For now leave it as it is so you can see whether the rendering still works.
        GridRenderer gridRenderer = new GridRenderer( shaderProgram3D, 100, 25, 5 );

        //Rendered scene is what is displayed via OpenGL rendering, it should be most likely moved to SceneProvider
        //Which will provide next scenes to render when something changes.
        RenderedSceneMutable renderedScene = new RenderedSceneMutable();
        renderedScene.setIsometricCamera3D( new IsometricCamera3D( 40, 0, 0, 0, 0.5f, 0, 0, 1 ) );
        renderedScene.setCamera2D( new MovableCamera2D( 1, 0, 0, 1, 0 ) );
        renderedScene.setBackgroundColour( new Vector4f( 0, 1, 0, 1 ) );

        //it's used for rendering, not necessary here
        Vector4f colour = new Vector4f( 0, 0, 0, 0 );
        float maxFloat = 1;

        //try is for Texture loading.
        try {
            //Does not work ATM but graphic objects can be created like that,
            //Here mostlikely we will only use Rectangle for objects like bullets and characters and RectangleGrid for terrain
            //RectangleGrid provides nice method for translating array position into rendered space position so they can be shared for same terrains
            //path can be either file in filesystem or within jar
            RectangleGrid overlay = new RectangleGrid( new Texture(
                "resources/First Oracle/grid.png" ) );
            //overlay is rendered last, good for UI
            renderedScene.setOverlay( new RenderedObjects2D() {
                @Override
                public void render( Object2DRenderer renderer ) {
                    renderer.render( overlay, colour, maxFloat );
                }
            } );

            //Example initialisation of map
            CubeGrid terrain1 = new CubeGrid( new Texture( "resources/First Oracle/texture3D.png" ) );

            CubeGrid[][][] array = new CubeGrid[10][20][20];

            System.err.println( terrain1.getBBO() );
            System.err.println( terrain1.getVertices() );

            for ( int y = 0; y < 10; y++ ) {
                for ( int x = 0; x < 20; x++ ) {
                    for ( int z = 0; z < 20; z++ ) { array[y][x][z] = terrain1; }
                }
            }
            //setScene2D is very similar but you are providing Objects2D instead of 3D
            //here it's best place to render all game objects
            renderedScene.setScene3D( new RenderedObjects3D() {
                @Override
                public void render( Object3DRenderer renderer ) {
                    Vector3i arrayShift = new Vector3i( 0, 0, 0 );
                    for ( int y = 0; y < 10; y++ ) {
                        for ( int x = 0; x < 20; x++ ) {
                            for ( int z = 0; z < 20; z++ ) {
                                renderer.render( array[y][x][z],
                                    array[y][x][z].computePosition( x, y, z, arrayShift ),
                                    colour,
                                    maxFloat
                                );
                            }
                        }
                    }
                }
            } );
            //SceneProvider is object which provides all next scenes for renderer below
            //Scene creation should be done here, I made it return same scene for now because I don't change content ATM
            //Most likely you would want to create your own SceneProvider that implements this interface
            CameraController cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(),
                10,
                15 );
            SceneProvider sceneProvider = () -> {
                cameraController.updateIsometricCamera3D( renderedScene.getCamera3D() );
                cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
                return renderedScene;
            };
            //Renderer renders all openGL content in Window, nothing to add much here
            SceneRenderer renderer = new SceneRenderer( shaderProgram2D,
                shaderProgram3D,
                gridRenderer,
                sceneProvider,
                true,
                true
            );
            //ContentManager is where everything related to GUI should be made
            //any JavaFX object creation and even access to statics of JavaFX classes should be from within those methods
            //With createOverlayPanel you provide your panel which will be displayed over openGL content,
            //Also save it's declaration so you can use it later in update or init methods
            //Init() is called once soon after createOverlayPanel, here you should initialise most of the initial content and probably most of the object creation.
            //update() is called at each frame update so changes there should be made as few as possible, probably setText in example below
            //should be done when time or fps changes, not every time
            //update() should contain all dynamic changes to javaFX components on runtime.
            //side note for JavaFX modifications:
            //You could use JFXGL.runOnEventsThread( () -> {}) to create and modify JavaFX content but
            //it does not look so good but I'm not stopping you from creating another thread for example
            OverlayContentManager contentManager = new OverlayContentManager() {
                Label fpsLabel;
                Label timeLabel;
                BorderPane overlayPanel;

                @Override
                public Pane createOverlayPanel() {
                    return overlayPanel = new BorderPane();
                }

                @Override
                public void init() {
                    fpsLabel = new Label();
                    timeLabel = new Label();
                    overlayPanel.setTop( fpsLabel );
                    overlayPanel.setBottom( timeLabel );
                }

                @Override
                public void update( double currentTime, int currentFps ) {
                    fpsLabel.setText( "FPS:" + currentFps );
                    timeLabel.setText( "Time:" + currentTime );
                }
            };

            //WindowApplication is JavaFX application that
            WindowApplication application = new WindowApplication( contentManager );
            renderer.addFpsObserver( application );
            //Window is window displayed with OpenGL and contains WindowApplication for JavaFX integration
            //Also it initalises OpenGL (via init()) content and initialises most of the objects passed via parameters
            //It also contains rendering loop which is done via run() method, best if called as another thread since it will block current thread for ever.
            Window window = Window.getInstance( settings,
                application,
                shaderProgram2D,
                shaderProgram3D,
                renderer
            );
            window.init();
            //OpenGL is initialised now. You can use all classes that use it.
            window.addQuitObserver( cameraController );
            window.addKeyCallbackController( cameraController );

            //Now it's place to spawn all other threads like game thread or controller thread.
            Thread cameraControllerThread = new Thread( cameraController );
            cameraControllerThread.start();

            //At last the window loop is run in this thread..
            window.run();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
