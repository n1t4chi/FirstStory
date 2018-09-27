/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.MovableCamera2D;
import com.firststory.firstoracle.camera3D.IsometricCamera3D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.notyfying.WindowListener;
import com.firststory.firstoracle.notyfying.WindowSizeEvent;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.NonAnimatedRectangle;
import com.firststory.firstoracle.object3D.CubeGrid;
import com.firststory.firstoracle.object3D.NonAnimatedCubeGrid;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.RenderedScene3D;
import com.firststory.firstoracle.scene.RenderedSceneMutable;
import com.firststory.firstoracle.window.Window;
import org.joml.Vector3ic;
import org.joml.Vector4f;

import java.util.Collection;
import java.util.Collections;

/**
 * Main class that initialises whole test application.
 * Also serves as template of usage.
 *
 * @author n1t4chi
 */
public class GlfwApplication3D {
    
    public static void main( String[] args ) throws Exception {
        new GlfwApplication3D().run();
    }
    
    private Window window;
    private WindowRenderer renderer;
    private SceneProvider sceneProvider;
    private CameraController cameraController;
    private RenderedSceneMutable renderedScene;
    private Grid3DRenderer grid3DRenderer;
    private WindowSettings settings;
    private Grid2DRenderer grid2DRenderer;
    
    public void run() throws Exception {
        //Settings for window, you can switch height/width, fullscreen, borderless and other magics.
        //VerticalSync disabled will uncap FPS.
        settings = new WindowSettings.WindowSettingsBuilder()
//            .setWindowMode( WindowMode.FULLSCREEN )
//            .setWindowMode( WindowMode.BORDERLESS )
            .setWindowMode( WindowMode.WINDOWED ).setMonitorIndex( 1 ).setDrawBorder( true ).setResizeable( true )
//            .setPositionX( -1920 )
//            .setWidth( -1 )
//            .setHeight( -1 )
            .build();
        //GridRenderer will be changed so it works as either 2D or 3D. For now leave it as it is so you can see whether the rendering still works.
        grid3DRenderer = new BoundedGrid3DRenderer( 100, 25, 5 );
        grid2DRenderer = new DummyGrid2DRenderer();
        //Rendered scene is what is displayed via OpenGL rendering, it should be most likely moved to SceneProvider
        //Which will provide next scenes to renderObject when something changes.
        renderedScene = new RenderedSceneMutable( settings );
        renderedScene.setIsometricCamera3D( new IsometricCamera3D( settings, 0.5f, 40, 0, 0, 0, 0, 1 ) );
        renderedScene.setCamera2D( new MovableCamera2D( settings, 1, 1, 0, 0 ) );
        renderedScene.setBackgroundColour( new Vector4f( 1f, 1f, 1f, 1 ) );
        
        //it's used for rendering, not necessary here
        var colour = new Vector4f( 0, 0, 0, 0 );
        float maxFloat = 1;
        
        //try is for Texture loading.
        //Does not work ATM but graphic objects can be created like that,
        //Here most likely we will only use Rectangle for objects like bullets and characters and RectangleGrid for terrain
        //RectangleGrid provides nice method for translating array position into rendered space position so they can be shared for same terrains
        //path can be either file in filesystem or within jar
        var texture1 = new Texture( "resources/First Oracle/grid.png" );
        var texture2 = new Texture( "resources/First Oracle/texture3D.png" );
        var overlay = new NonAnimatedRectangle();
        overlay.setTexture( texture1 );
        //overlay is rendered last, good for UI
        renderedScene.setOverlay( () -> Collections.singletonList( overlay ) );
        
        //Example initialisation of map
        var terrain = new NonAnimatedCubeGrid();
        terrain.setTexture( texture2 );
    
        var array = new CubeGrid[ 20 ][ 10 ][ 20 ];
        
        for ( var x = 0; x < 20; x++ ) {
            for ( var y = 0; y < 10; y++ ) {
                for ( var z = 0; z < 20; z++ ) {
                    array[ x ][ y ][ z ] = terrain;
                }
            }
        }
        //setScene2D is very similar but you are providing Objects2D instead of 3D
        //here it's best place to renderObject all game objects
        
        renderedScene.setScene3D( new RenderedScene3D() {
            @Override
            public Terrain3D< ? >[][][] getTerrains() {
                return array;
            }
            
            @Override
            public Collection< PositionableObject3D< ?, ? > > getObjects() {
                return Collections.emptyList();
            }
            
            @Override
            public Vector3ic getTerrainShift() {
                return FirstOracleConstants.VECTOR_ZERO_3I;
            }
        } );
        //SceneProvider is object which provides all next scenes for renderer below
        //Scene creation should be done here, I made it return same scene for now because I don't change content ATM
        //Most likely you would want to create your own SceneProvider that implements this interface
        cameraController = new CameraController( CameraKeyMap.getFunctionalKeyLayout(), 10, 15 );
        sceneProvider = () -> {
            cameraController.updateIsometricCamera3D( renderedScene.getCamera3D() );
            cameraController.updateMovableCamera2D( ( MovableCamera2D ) renderedScene.getCamera2D() );
            return renderedScene;
        };
        //Renderer renders all openGL content in Window, nothing to add much here
        renderer = new WindowRenderer(
            grid2DRenderer,
            grid3DRenderer,
            sceneProvider,
            settings.isUseTexture(),
            settings.isDrawBorder()
        );
        
        //Window is window displayed with OpenGL
        //Also it initialises OpenGL (via init()) content and initialises most of the objects passed via parameters
        //It also contains rendering loop which is done via run() method, best if called as another thread since it will block current thread for ever.
        window = Window.build( settings, renderer ).build();
        window.addTimeListener( cameraController );
        window.init();
        //OpenGL is initialised now. You can use all classes that use it.
        window.addQuitListener( cameraController );
        window.addKeyListener( cameraController );
        window.addMouseListener( cameraController );
        window.addWindowListener( new WindowListener() {
            @Override
            public void notify( WindowSizeEvent event ) {
                renderedScene.getCamera2D().forceUpdate();
                renderedScene.getCamera3D().forceUpdate();
            }
        } );
        
        //Now it's place to spawn all other threads like game thread or controller thread.
        cameraController.start();
        
        //At last the window loop is run in this thread..
        window.run();
    }
}
