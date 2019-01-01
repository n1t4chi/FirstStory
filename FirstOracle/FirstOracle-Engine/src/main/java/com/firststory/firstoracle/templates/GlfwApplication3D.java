/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.templates;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.WindowMode;
import com.firststory.firstoracle.WindowSettings;
import com.firststory.firstoracle.camera2D.IdentityCamera2D;
import com.firststory.firstoracle.controller.CameraController;
import com.firststory.firstoracle.controller.CameraKeyMap;
import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object2D.NonAnimatedRectangle;
import com.firststory.firstoracle.object3D.CubeGrid;
import com.firststory.firstoracle.object3D.NonAnimatedCubeGrid;
import com.firststory.firstoracle.rendering.*;
import com.firststory.firstoracle.scene.*;
import com.firststory.firstoracle.window.WindowBuilder;
import com.firststory.firstoracle.window.WindowImpl;

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
    
    private WindowImpl window;
    private WindowRenderer renderer;
    private SceneProvider sceneProvider;
    private CameraController cameraController;
    private RenderableSceneMutable renderedScene;
    private Grid3D grid3DRenderer;
    private WindowSettings settings;
    private Grid2D grid2DRenderer;
    
    public void run() throws Exception {
        //Settings for window, you can switch height/width, fullscreen, borderless and other magics.
        //VerticalSync disabled will uncap FPS.
        settings = WindowSettings.builder()
//            .setWindowMode( WindowMode.FULLSCREEN )
//            .setWindowMode( WindowMode.BORDERLESS )
            .setWindowMode( WindowMode.WINDOWED )
            .setMonitorIndex( 1 )
            .setResizeable( true )
//            .setPositionX( -1920 )
//            .setWidth( -1 )
//            .setHeight( -1 )
            .build();
        
        //Here most likely we will only use Rectangle for objects like bullets and characters and RectangleGrid for terrain
        //RectangleGrid provides nice method for translating array position into rendered space position so they can be shared for same terrains
        //Texture path can be either file in filesystem or within jar
        var texture1 = Texture.create( "resources/First Oracle/grid.png" );
        var texture2 = Texture.create( "resources/First Oracle/texture3D.png" );
        var overlayObject = new NonAnimatedRectangle();
        overlayObject.setTexture( texture1 );
        
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
    
        //GridRenderer will be changed so it works as either 2D or 3D. For now leave it as it is so you can see whether the rendering still works.
        grid3DRenderer = new BoundedGrid3D( 100, 25, 5 );
        grid2DRenderer = new DummyGrid2D();
        //Rendered scene is what is displayed via OpenGL rendering, it should be most likely moved to SceneProvider
        //Which will provide next scenes to renderObject when something changes.
        renderedScene = new RenderableSceneMutable( settings );
    
        //Scene provider could be used for providing different scenes. For example one for open credits, one for main menu and one for game.
        //New scenes could be created when other scene is presented and immediately switched to when ready.
        //Now we are providing one scene so this simple lambda can be used instead.
        sceneProvider = () -> renderedScene ;

        //Overlay is rendered on top of anything so it's good for UI elements, although only 2D objects are supported.
        RegistrableOverlay overlay = new RegistrableOverlayImpl();
        overlay.registerOverlay( overlayObject );
        renderedScene.setOverlay( overlay );
        
        //Renderer is class that contains all information like scenes and grids.
        renderer = new WindowRenderer(
            grid2DRenderer,
            grid3DRenderer,
            sceneProvider
        );
        
        //Window is window displayed with OpenGL
        //Also it initialises OpenGL (via init()) content and initialises most of the objects passed via parameters
        //It also contains rendering loop which is done via run() method, best if called as another thread since it will block current thread for ever.
        window = WindowBuilder.simpleWindow( settings, renderer ).build();
        
        //Camera controller provides two cameras: 2D and 3D, which are updated corresponding to used input.
        cameraController = CameraController.createAndStart( window, settings, CameraKeyMap.getFunctionalKeyLayout(), 10, 15f );
    
        //Now the Scene3D is being set up like camera, which objects etc.
        //Scene2D is similar but you are providing 2D Objects instead of 3D
        //Scenes 2D and 3D are designed to be used for in game objects like characters, terrain or items
        renderedScene.setScene3D( new RenderableScene3DImpl( cameraController.getCamera3D(), Collections.emptyList(), array, FirstOracleConstants.INDEX_ZERO_3I ) );
    
        //Background is bunch of 2D objects rendered beneath other elements, so could be used for background images or skybox.
        //It also provides background colour which can be seen when there are no objects displayed in some places.
        renderedScene.setBackground( new RenderableBackgroundImpl( IdentityCamera2D.getCamera(), Collections.emptyList(), FirstOracleConstants.WHITE ) );
    
        overlay.registerOverlay( new FpsCounter( window ) );
        
        //At last the window loop must be run in this thread. ( or thread where init() was called )
        window.run();
    }
}
