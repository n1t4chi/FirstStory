/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.NonAnimatedRectangle;
import com.firststory.firstoracle.object3D.CubeGrid;
import com.firststory.firstoracle.object3D.NonAnimatedCubeGrid;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.OverlayContentManager;
import com.firststory.firstoracle.window.Window;
import com.firststory.firstoracle.window.WindowApplication;
import com.firststory.firstoracle.window.shader.ShaderProgram2D;
import com.firststory.firstoracle.window.shader.ShaderProgram3D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.joml.Vector3fc;
import org.joml.Vector4f;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class MainTemplate3D {
    
    private static Window window;
    private static MyOverlayContentManager contentManager;
    private static WindowRenderingContext renderer;
    private static SceneProvider sceneProvider;
    private static CameraController cameraController;
    private static RenderedSceneMutable renderedScene;
    private static Grid3DRenderer grid3DRenderer;
    private static ShaderProgram2D shaderProgram2D;
    private static ShaderProgram3D shaderProgram3D;
    private static WindowSettings settings;
    private static Grid2DRenderer grid2DRenderer;
    private static WindowApplication application;
    
    public static void main( String[] args ) throws Exception {
        //new Thread(() -> { try { MainTemplate2D.main( args ); } catch ( Exception e ) {} } ).start();
        //Settings for window, you can switch height/widith, fullscreen, borderless and other magics.
        //VerticalSync disabled will uncap FPS.
        settings = new WindowSettings.WindowSettingsBuilder()
//            .setWindowMode( WindowMode.FULLSCREEN )
//            .setWindowMode( WindowMode.BORDERLESS )
            .setWindowMode( WindowMode.WINDOWED )
            .setMonitorIndex( 1 )
            .setDrawBorder( true )
            .setResizeable( true )
//            .setPositionX( -1920 )
//            .setWidth( -1 )
//            .setHeight( -1 )
            .build();
        //Those shader programs are necessary. For now I didn't remove anything with 3D so it will need to be left as it is.
        shaderProgram3D = new ShaderProgram3D();
        shaderProgram2D = new ShaderProgram2D();
        //GridRenderer will be changed so it works as either 2D or 3D. For now leave it as it is so you can see whether the rendering still works.
        grid3DRenderer = new BoundedGrid3DRenderer( shaderProgram3D, 100, 25, 5 );
        grid2DRenderer = new DummyGrid2DRenderer();
        //Rendered scene is what is displayed via OpenGL rendering, it should be most likely moved to SceneProvider
        //Which will provide next scenes to renderObject when something changes.
        renderedScene = new RenderedSceneMutable( settings );
        renderedScene.setIsometricCamera3D( new IsometricCamera3D( settings, 0.5f, 40, 0, 0, 0, 0, 1 ) );
        renderedScene.setCamera2D( new MovableCamera2D( settings, 1, 1, 0, 0 ) );
        renderedScene.setBackgroundColour( new Vector4f( 1f, 1f, 1f, 1 ) );
    
        //it's used for rendering, not necessary here
        Vector4f colour = new Vector4f( 0, 0, 0, 0 );
        float maxFloat = 1;
    
        //try is for Texture loading.
        //Does not work ATM but graphic objects can be created like that,
        //Here mostlikely we will only use Rectangle for objects like bullets and characters and RectangleGrid for terrain
        //RectangleGrid provides nice method for translating array position into rendered space position so they can be shared for same terrains
        //path can be either file in filesystem or within jar
        Texture texture1 = new Texture( "resources/First Oracle/grid.png" );
        Texture texture2 = new Texture( "resources/First Oracle/texture3D.png" );
        NonAnimatedRectangle overlay = new NonAnimatedRectangle();
        overlay.setTexture( texture1 );
        //overlay is rendered last, good for UI
        renderedScene.setOverlay( overlay::render );
        
        //Example initialisation of map
        NonAnimatedCubeGrid terrain = new NonAnimatedCubeGrid();
        terrain.setTexture( texture2 );

        CubeGrid[][][] array = new CubeGrid[20][10][20];

        for ( int x = 0; x < 20; x++ ) {
            for ( int y = 0; y < 10; y++ ) {
                for ( int z = 0; z < 20; z++ ) {
                    array[x][y][z] = terrain;
                }
            }
        }
        //setScene2D is very similar but you are providing Objects2D instead of 3D
        //here it's best place to renderObject all game objects
        renderedScene.setScene3D( ( Multi3DRenderer renderer ) -> renderer.renderAll( array ) );
        //SceneProvider is object which provides all next scenes for renderer below
        //Scene creation should be done here, I made it return same scene for now because I don't change content ATM
        //Most likely you would want to create your own SceneProvider that implements this interface
        cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(),
            10, 15
        );
        sceneProvider = () -> {
            cameraController.updateIsometricCamera3D( renderedScene.getCamera3D() );
            cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
            contentManager.updatePositionLabel( cameraController.getPos3D() );
            return renderedScene;
        };
        //Renderer renders all openGL content in Window, nothing to add much here
        renderer = new WindowRenderingContext( shaderProgram2D, shaderProgram3D, grid2DRenderer, grid3DRenderer,
            sceneProvider,
            settings.isUseTexture(),
            settings.isDrawBorder()
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
        //You could use JfxglContext.runOnEventsThread( () -> {}) to create and modify JavaFX content but
        //it does not look so good but I'm not stopping you from creating another thread for example
        contentManager = new MyOverlayContentManager();

        //WindowApplication is JavaFX application that
        application = new WindowApplication( contentManager );
        //Window is window displayed with OpenGL and contains WindowApplication for JavaFX integration
        //Also it initalises OpenGL (via init()) content and initialises most of the objects passed via parameters
        //It also contains rendering loop which is done via run() method, best if called as another thread since it will block current thread for ever.
        window = new Window( settings, application,
            shaderProgram2D,
            shaderProgram3D,
            renderer
        );
        renderer.addFpsListener( application );
        window.addTimeListener( application );
        window.init();
        //OpenGL is initialised now. You can use all classes that use it.
        window.addQuitListener( cameraController );
        window.addKeyListener( cameraController.getKeyCallback() );
        window.addMouseScrollListener( cameraController.getScrollCallback() );
        window.addSizeListener( ( newWidth, newHeight, source ) -> {
            renderedScene.getCamera2D().forceUpdate();
            renderedScene.getCamera3D().forceUpdate();
        } );
        
        //Now it's place to spawn all other threads like game thread or controller thread.
        cameraController.start();

        //At last the window loop is run in this thread..
        window.run();
    }
    
    private static class MyOverlayContentManager implements OverlayContentManager {
        
        Label fpsLabel;
        Label timeLabel;
        Label positionLabel;
        BorderPane overlayPanel;
        
        @Override
        public Pane createOverlayPanel() {
            return overlayPanel = new BorderPane();
        }
        
        @Override
        public void init( Stage stage, Scene scene ) {
            fpsLabel = new Label();
            timeLabel = new Label();
            positionLabel = new Label();
            overlayPanel.setTop( fpsLabel );
            overlayPanel.setBottom( timeLabel );
            overlayPanel.setLeft( positionLabel );
        }
    
        @Override
        public void update( double currentTime, int currentFps ) {
            fpsLabel.setText( "FPS:" + currentFps );
            timeLabel.setText( String.format( "current time: %.1fs", currentTime ) );
            if ( update ) {
                update = false;
                positionLabel.setText( positionText );
            }
        }
        
        private String positionText = "";
        private volatile boolean update = false;
        
        public synchronized void updatePositionLabel(Vector3fc position){
            positionText = "Position: ("+position.x()+","+position.y()+","+position.z()+")";
            update=true;
        }
    }
}
