/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import com.firststory.freshStart.camera.Camera;
import com.firststory.freshStart.camera.IdentityCamera;
import com.firststory.freshStart.camera.IsometricCamera;
import com.firststory.firstoracle.scene.MainScene;
import com.firststory.freshStart.window.shader.ShaderProgram;
import com.firststory.freshStart.window.WindowSettings;
import com.firststory.objects3D.HexPrismUtil;
import com.firststory.freshStart.object.Texture;
import com.firststory.objects3D.Positionable.PositionableObject3D;
import com.firststory.objects3D.Positionable.Plane3D;
import com.firststory.objects3D.Terrain.Terrain3D;
import com.firststory.objects3D.AbsolutePostionable.Text.Text3D;
import com.firststory.objects3D.AbsolutePostionable.Text.Text3DFactory;


import com.firststory.freshStart.object.BoundingBox;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import org.joml.*;

import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author n1t4chi
 */
public class WindowOld implements Runnable {
    
    private static void renderGridArray(
                                           int gridID,
                                           int dataLength,
                                           int uniformObjectDataID,
                                           float width,
                                           float red,
                                           float green,
                                           float blue
    )
    {
        float[] data = {
            0, 0, 0,
            1, 1, 1,
            0, 0, 0,
            red, green, blue, 1, //color
            0.75f
        };
        GL20.glUniform1fv( uniformObjectDataID, data );
        GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, gridID );
        GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
        GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
        GL11.glLineWidth( width );
        GL11.glDrawArrays( GL11.GL_LINES, 0, dataLength );
    }
    
    // The window handle
    //private long window;
    //Vertex Array handle
    //private int VertexArrayID;
    private final WindowSettings windowSettings;
    GLFWScrollCallback scrollCallback;
    GLFWKeyCallback keyCallback;
    GLFWCursorPosCallback cursorCallback;
    Vector3f cameraCenterPoint = new Vector3f();
    NumberFormat nf = new DecimalFormat( "#,##0.00" );
    ConcurrentHashMap< Integer, Integer > KeyMap = new ConcurrentHashMap<>( 10 );
    float cameraRotationX;
    double mouse_dy = 0;
    int lastTextureID = 0;
    int lastVertexBufferID = 0;
    int lastUVBufferID = 0;
    Text3D text = null;
    float ratio = 1;
    float rotateX = 1;
    private float cameraRotation = 0;
    private float cameraSize = 25;
    private double lastTime = 0;
    
    /**
     * Initialises window parameters.
     *
     * @param windowMode   window mode
     * @param title        title of window
     * @param width        -1 for default width
     * @param height       -1 for default height
     * @param pos_x        position of a window, does not count window border!. -1 for default.
     * @param pos_y        position of a window, does not count window border!. -1 for default.
     * @param verticalSync verticalSync on/off
     * @param antiAliasing anti aliasing level
     */
    public WindowOld(
                     WINDOW_MODE windowMode,
                     String title,
                     int width,
                     int height,
                     int pos_x,
                     int pos_y,
                     boolean verticalSync,
                     int antiAliasing
    )
    {
        windowSettings = new WindowSettings( windowMode,
                                           title,
                                           width,
                                           height,
                                           pos_x,
                                           pos_y,
                                           verticalSync,
                                           antiAliasing );
    }
    
    public WindowOld(
                     WINDOW_MODE windowMode,
                     String title,
                     boolean verticalSync,
                     int antiAliasing
    )
    {
        this( windowMode, title, -1, -1, -1, -1, verticalSync, antiAliasing );
    }
    
    public void run()
    {
        KeyController controller = null;
        try {
            initOpenGL( windowSettings );
            controller = new KeyController();
            controller.start();
            setVisible( windowSettings.window );
            loop( windowSettings/*, vbo*/ );
        }
        catch ( Exception ex ) {
            System.err.println( "Exception: ex" + ex );
            ex.printStackTrace();
        }
        finally {
            try {
                if ( controller != null ) {
                    controller.kill();
                }
                if ( windowSettings != null ) {
                    // Free the window callbacks and destroy the window
                    Callbacks.glfwFreeCallbacks( windowSettings.window );
                    GLFW.glfwDestroyWindow( windowSettings.window );
                    
                }
            }
            finally {
                // Terminate GLFW and free the error callback
                GLFW.glfwTerminate();
                if ( windowSettings != null && windowSettings.errorCallback != null ) {
                    windowSettings.errorCallback.free();
                }
            }
        }
    }
    
    private void initWindow( WindowSettings windowSettings )
    {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode mode = GLFW.glfwGetVideoMode( monitor );
        // Create the window
        int width = windowSettings.width;
        int height = windowSettings.height;
        width = ( width > 0 ) ? width : mode.width();
        height = ( height > 0 ) ? height : mode.height();
        windowSettings.width = width;
        windowSettings.height = height;
        long window;
        
        switch ( windowSettings.windowMode ) {
            case WINDOWED_FULLSCREEN:
                GLFW.glfwWindowHint( GLFW.GLFW_RED_BITS, mode.redBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_GREEN_BITS, mode.greenBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_BLUE_BITS, mode.blueBits() );
                GLFW.glfwWindowHint( GLFW.GLFW_REFRESH_RATE,
                    mode.refreshRate() );
            case FULLSCREEN:
                window = GLFW.glfwCreateWindow( width,
                    height,
                    windowSettings.title,
                    monitor,
                    NULL );
                break;
            case BORDERLESS:
                GLFW.glfwWindowHint( GLFW.GLFW_DECORATED, GL11.GL_FALSE );
            case WINDOWED:
            default:
                window = GLFW.glfwCreateWindow( width,
                    height,
                    windowSettings.title,
                    NULL,
                    NULL );
                break;
        }
        if ( window == NULL ) {
            throw new RuntimeException( "Failed to create the GLFW window" );
        }
        windowSettings.window = window;
        int[] left = new int[1];
        int[] top = new int[1];
        int[] right = new int[1];
        int[] bottom = new int[1];
        
        GLFW.glfwGetWindowFrameSize( window, left, top, right, bottom );
        if ( windowSettings.posX >= 0 && windowSettings.posY >= 0 ) {
            GLFW.glfwSetWindowPos( window,
                windowSettings.posX + left[0],
                windowSettings.posX + top[0] );
        }
    }
    
    private void enableDepth()
    {
        GL11.glDepthMask( true );
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        GL11.glDepthFunc( GL11.GL_LEQUAL );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    private void disableDepth()
    {
        GL11.glDepthMask( false );
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glClear( GL11.GL_DEPTH_BUFFER_BIT );
    }
    
    // Terrain3D[][][] terrainArray;
    // Vector3i terrainVecMin;
    //Vector3i terrainVecMax;
    //Vector3i terrainSize;
    
    /**
     * Enables various functionality: vsync, depth test, alpha channels,
     * culling, counterclockwise order of vertex.
     */
    private void enableFunctionality()
    {
        // Enable v-sync
        if ( windowSettings.verticalSync ) {
            GLFW.glfwSwapInterval( 1 );
        } else {
            GLFW.glfwSwapInterval( 0 );
        }
        // Enable depth test
        
        //alpha channels
        GL11.glEnable( GL11.GL_CULL_FACE );
        GL11.glCullFace( GL11.GL_BACK );
        GL11.glEnable( GL11.GL_BLEND );
        GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
        GL11.glFrontFace( GL11.GL_CCW );
    }
    
    private void setVisible( long window )
    {
        GLFW.glfwShowWindow( window );
    }
    
    /**
     * Initialises window
     *
     * @return true (width,height)
     */
    private void initOpenGL( WindowSettings windowSettings )
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        windowSettings.errorCallback = GLFWErrorCallback.createPrint( System.err )
                                         .set();
        
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !GLFW.glfwInit() ) {
            throw new IllegalStateException( "Unable to initialize GLFW" );
        }
        
        // Configure GLFW
        GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
        GLFW.glfwWindowHint( GLFW.GLFW_VISIBLE,
            GLFW.GLFW_FALSE ); // the window will stay hidden after creation
        GLFW.glfwWindowHint( GLFW.GLFW_RESIZABLE,
            GLFW.GLFW_FALSE ); // the window will be resizable
        GLFW.glfwWindowHint( GLFW.GLFW_SAMPLES, windowSettings.antiAliasing );
        initWindow( windowSettings );
        
        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent( windowSettings.window );
        GL.createCapabilities();
        
        if ( !checkSupport() ) {
            throw new UnsupportedOperationException( "Lack of openGL support." );
        }
        enableFunctionality();
        GLFW.glfwSetScrollCallback( windowSettings.window,
            scrollCallback = new GLFWScrollCallback() {
                @Override
                public void invoke( long l, double d, double d1 )
                {
                    //System.out.println("mouse sscroll: l:"+l+" d:"+d+" d1:"+d1);
                    //mouse_dx = d;
                    mouse_dy = d1;
                    cameraSize -= d1;
                    if ( cameraSize < 1 ) {
                        cameraSize = 1;
                    }
                }
            } );
        
        GLFW.glfwSetKeyCallback( windowSettings.window,
            keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(
                                      long window,
                                      int key,
                                      int scancode,
                                      int action,
                                      int mods
                )
                {
                    if ( action == GLFW.GLFW_PRESS ) {
                        KeyMap.put( key, action );
                    } else if ( action == GLFW.GLFW_RELEASE ) {
                        KeyMap.remove( key );
                    }
                }
            } );
        
    }
    
    /**
     * @param key GLFW key code
     * @param del repeat action differentiation
     * @return whether to remove key or not.
     */
    private boolean key( int key, float del )
    {
        //System.out.println(del);
        switch ( key ) {
            case GLFW.GLFW_KEY_R:
                //rot -= 45;
                cameraRotationX += del;
                if ( cameraRotationX >= 360 ) {
                    cameraRotationX = cameraRotationX - 360;
                }
                //return true;
                break;
            case GLFW.GLFW_KEY_F:
                //rot += 45;
                cameraRotationX -= del;
                if ( cameraRotationX <= 0 ) {
                    cameraRotationX = 360 - cameraRotationX;
                }
                //return true;
                break;
            case GLFW.GLFW_KEY_Q:
                //rot -= 45;
                cameraRotation += del;
                if ( cameraRotation >= 360 ) {
                    cameraRotation = cameraRotation - 360;
                }
                //return true;
                break;
            case GLFW.GLFW_KEY_E:
                //rot += 45;
                cameraRotation -= del;
                if ( cameraRotation <= 0 ) {
                    cameraRotation = 360 - cameraRotation;
                }
                //return true;
                break;
            case GLFW.GLFW_KEY_W:
                cameraCenterPoint.add( 0, 0, del );
                break;
            case GLFW.GLFW_KEY_S:
                cameraCenterPoint.add( 0, 0, -del );
                break;
            case GLFW.GLFW_KEY_A:
                cameraCenterPoint.add( del, 0, 0 );
                break;
            case GLFW.GLFW_KEY_D:
                cameraCenterPoint.add( -del, 0, 0 );
                break;
            case GLFW.GLFW_KEY_SPACE:
                cameraCenterPoint.add( 0, del, 0 );
                break;
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                cameraCenterPoint.add( 0, -del, 0 );
                break;
            default:
                return true;
        }
        return false;
    }
    
    
    /* *
     * Map(level,list of objects).
     */
    //HashMap<Integer,ArrayList<PositionableObject3D>> objectYMap = new HashMap<>(20);
    
    private void renderObject(
                                 int frame,
                                 int direction,
                                 Object3D obj,
                                 Vector3fc objPos,
                                 Vector4fc objCol,
                                 int uniformDataID,
                                 float alpha
    )
    {
        int buff_size = obj.getVertexCount();
        Vector3fc objScale = obj.getScale();
        Vector3fc objRot = obj.getRotation();
        float[] data = {
            objPos.x(), objPos.y(), objPos.z(),
            objScale.x(), objScale.y(), objScale.z(),
            objRot.x(), objRot.y(), objRot.z(),
            objCol.x(), objCol.y(), objCol.z(), objCol.w()
            , alpha//Alpha Override
        };
        GL20.glUniform1fv( uniformDataID, data );
        int vertexBufferID = obj.getVertexBufferID();
        if ( vertexBufferID >= 0 || vertexBufferID != lastVertexBufferID ) {
            //bind vertices
            obj.bindVertexBuffer();
            GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0 );
        }
        int UVBufferID = obj.getUVBufferID( frame, direction );
        if ( UVBufferID >= 0 || UVBufferID != lastUVBufferID ) {
            //bind uv map
            obj.bindUVBuffer( frame, direction );
            GL20.glVertexAttribPointer( 1, 2, GL11.GL_FLOAT, false, 0, 0 );
        }
        if ( windowSettings.useTexture ) {
            Texture tex = obj.getTexture();
            int textureID = tex.getTextureID();
            if ( textureID >= 0 || textureID != lastTextureID ) {
                tex.bind();
            }
            lastTextureID = textureID;
        }
        lastVertexBufferID = vertexBufferID;
        lastUVBufferID = UVBufferID;
        GL11.glDrawArrays( GL11.GL_TRIANGLES, 0, buff_size );
        
        if ( windowSettings.drawBorder ) {
            float[] data2 = {
                objPos.x(), objPos.y(), objPos.z(),
                objScale.x(), objScale.y(), objScale.z(),
                objRot.x(), objRot.y(), objRot.z(),
                1, 0, 0, 1,
                0.75f
            };
            GL20.glUniform1fv( uniformDataID, data2 );
            GL11.glLineWidth( 1 );
            GL11.glDrawArrays( GL11.GL_LINE_LOOP, 0, buff_size );
        }
        
    }
    
    private synchronized void loop( WindowSettings wt )
    {
        //GL.createCapabilities();
        
        Texture tex1, tex2, tex3;
        
        Font font = new Font( "Times New Roman", Font.PLAIN, 20 );
        Text3DFactory textFactory = new Text3DFactory( font );
        
        try {
            text = textFactory.getText3D( "", 0, 0, wt.width, wt.height );
        }
        catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        Plane3D grid;
        
        MainScene ms = new MainScene() {
            @Override
            public Vector3ic translateSpaceToArray( float X, float Y, float Z )
            {
                return HexPrismUtil.convertSpaceToArrayPosition( X,
                    Y,
                    Z,
                    this.terrainVecMin );
            }
            
            @Override
            public int translateSpaceToArrayX( float X, float Y, float Z )
            {
                return HexPrismUtil.convertSpaceToArrayX( X,
                    this.terrainVecMin );
            }
            
            @Override
            public int translateSpaceToArrayY( float X, float Y, float Z )
            {
                return HexPrismUtil.convertSpaceToArrayY( Y,
                    this.terrainVecMin );
            }
            
            @Override
            public int translateSpaceToArrayZ( float X, float Y, float Z )
            {
                return HexPrismUtil.convertSpaceToArrayZ( Z,
                    X,
                    this.terrainVecMin );
            }
            
            @Override
            public float translateArrayToSpaceX( float X, float Y, float Z )
            {
                return HexPrismUtil.convertArrayToSpaceX( X, terrainVecMin );
            }
            
            @Override
            public float translateArrayToSpaceY( float X, float Y, float Z )
            {
                return HexPrismUtil.convertArrayToSpaceY( Y, terrainVecMin );
            }
            
            @Override
            public float translateArrayToSpaceZ( float X, float Y, float Z )
            {
                return HexPrismUtil.convertArrayToSpaceZ( Z, X, terrainVecMin );
            }
            
        };
        
        try {
            grid = new Plane3D( new Texture( "EXT/grid.png", 1, 1, 1, 1 ) );
            Terrain3D ter;
            tex1 = new Texture( "EXT/texture3DHEX2.png", 1, 1, 1, 1 );
            ms.terrainVecMin = new Vector3i( -50, 0, -50 );
            ms.terrainVecMax = new Vector3i( 50, 8, 50 );
            ms.terrainSize = new Vector3i(
                                             ms.terrainVecMax.x - ms.terrainVecMin.x + (
                                                                                           (
                                                                                               java.lang.Math
                                                                                                   .signum(
                                                                                                       ms.terrainVecMin.x ) !=
                                                                                               java.lang.Math
                                                                                                   .signum(
                                                                                                       ms.terrainVecMax.y )
                                                                                           ) ? 1 : 0
                                             ),
                                             ms.terrainVecMax.y - ms.terrainVecMin.y + (
                                                                                           (
                                                                                               java.lang.Math
                                                                                                   .signum(
                                                                                                       ms.terrainVecMin.y ) !=
                                                                                               java.lang.Math
                                                                                                   .signum(
                                                                                                       ms.terrainVecMax.z )
                                                                                           ) ? 1 : 0
                                             ),
                                             ms.terrainVecMax.z - ms.terrainVecMin.z + (
                                                                                           (
                                                                                               java.lang.Math
                                                                                                   .signum(
                                                                                                       ms.terrainVecMin.y ) !=
                                                                                               java.lang.Math
                                                                                                   .signum(
                                                                                                       ms.terrainVecMax.z )
                                                                                           ) ? 1 : 0
                                             )
            );
            ms.terrainArray = new Terrain3D[ms.terrainSize.x][ms.terrainSize.y][ms.terrainSize.z];
            //ter = new com.firststory.objects3D.Terrain.HexPrismTerr3D(tex1);
            ter = new com.firststory.objects3D.Terrain.HexPrismTerr3D( tex1 );
            for ( int x = 0; x < ms.terrainSize.x; x++ ) {
                for ( int z = 0; z < ms.terrainSize.z; z++ ) {
                    //int y=0;
                    for ( int y = 0; y < ms.terrainSize.y - 1; y++ ) {
                        ms.terrainArray[x][y][z] = ter;
                    }
                }
            }
            PositionableObject3D obj;
            tex1 = new Texture( "EXT/texture3D.png", 1, 1, 1, 1 );
            float angle = ( float ) java.lang.Math.toRadians( 270 );
            ArrayList< PositionableObject3D > l = new ArrayList<>();
            for ( int x = 0; x < ms.terrainSize.x; x++ ) {
                for ( int z = 0; z < ms.terrainSize.z; z++ ) {
                    obj = new Plane3D( tex1 );
                    //obj = new Plane3D(text.getTexture());
                    Vector3fc v = HexPrismUtil.convertArrayToSpacePosition( x,
                        ms.terrainSize.y - 1,
                        z,
                        ms.terrainVecMin );
                    obj.setPosition( v.x(), v.y() - 0.5f, v.z() );
                    obj.setScale( 0.5f, 0.5f, 0.5f );
                    if ( java.lang.Math.abs( x ) % 4 == 1 ) {
                        obj.setRotationX( angle );
                    }
                    if ( java.lang.Math.abs( x ) % 4 == 2 ) {
                        obj.setRotationY( angle );
                    }
                    if ( java.lang.Math.abs( x ) % 4 == 3 ) {
                        obj.setRotationZ( angle );
                    }
                    l.add( obj );
                }
            }
            ms.addObjectList( l, ms.terrainSize.y - 1 );
        }
        catch ( IOException ex ) {
            System.err.println( "Can't load texture:" + ex );
            return;
        }
        
        ShaderProgram shaderTexture;
        Texture emptyTexture;
        try {
            shaderTexture = new ShaderProgram(
                "EXT/shader.vert",
                                                 "./EXT/shader.frag" );
        }
        catch ( IOException ex ) {
            System.err.println( "Error on shader loading: " + ex );
            return;
        }
        int matrixID = GL20.glGetUniformLocation( shaderTexture.getProgramID(),
            "camera" );
        int uniformObjectDataID = GL20.glGetUniformLocation( shaderTexture.getProgramID(),
            "objectData" );
        
        try {
            emptyTexture = new Texture( new BufferedImage( 1,
                                                             1,
                                                             BufferedImage.TYPE_INT_ARGB ) );
            emptyTexture.load();
        }
        catch ( IOException ex ) {
            System.err.println( "Can't load texture:" + ex );
            return;
        }
        
        Matrix4f idMatrix = new Matrix4f();
        Vector4f idVec4 = new Vector4f( 0, 0, 0, 0 );
        
        int frameCount = 0;
        double lastFrameUpdate = GLFW.glfwGetTime();
        double lastFpsUpdate = lastFrameUpdate;
        int lastFps = 0;
        
        GridMaker gridTouple = null;
        if ( wt.drawGrid ) {
            gridTouple = new GridMaker( 100, 10 );
        }
        GL11.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
        GL20.glUseProgram( shaderTexture.getProgramID() );
        
        GL20.glEnableVertexAttribArray( 0 );
        GL20.glEnableVertexAttribArray( 1 );
        
        ratio = ( float ) windowSettings.height / windowSettings.width;
        
        IsometricCamera isometric = new IsometricCamera( cameraSize,
                                                           cameraCenterPoint.x,
                                                           cameraCenterPoint.y,
                                                           cameraCenterPoint.z,
                                                           ratio,
                                                           0,
                                                           cameraRotation,
                                                           0f );
        IsometricCamera isometric2 = new IsometricCamera( cameraSize,
                                                            cameraCenterPoint.x,
                                                            cameraCenterPoint.y,
                                                            cameraCenterPoint.z,
                                                            ratio,
                                                            0,
                                                            cameraRotation,
                                                            0f );
        
        Camera identity = new IdentityCamera();

        
        while ( !GLFW.glfwWindowShouldClose( wt.window ) ) {
            GL11.glClear( GL11.GL_COLOR_BUFFER_BIT ); // clear the framebuffer
            frameCount++;
            //draw 3D stuff
            enableDepth();
            
            isometric.setRotationY( cameraRotation );
            isometric.setRotationX( cameraRotationX );
            isometric.setSize( cameraSize );
            isometric.setCenterPoint( cameraCenterPoint.x,
                cameraCenterPoint.y,
                cameraCenterPoint.z );
            
            //isometric2.setRotationY(cameraRotation);
            isometric2.setRotationY( cameraRotation );
            isometric2.setRotationX( cameraRotationX );
            isometric2.setSize( cameraSize - 15 );
            isometric2.setCenterPoint( cameraCenterPoint.x,
                cameraCenterPoint.y,
                cameraCenterPoint.z );
            
            isometric.bindCamera( matrixID );
            if ( gridTouple != null ) {
                renderGridArray( gridTouple.mainAxesID,
                    gridTouple.mainAxesLength,
                    uniformObjectDataID,
                    3,
                    1,
                    1,
                    1 );
                renderGridArray( gridTouple.interAxesID,
                    gridTouple.interAxesLength,
                    uniformObjectDataID,
                    1,
                    0.75f,
                    0.75f,
                    0.75f );
                renderGridArray( gridTouple.smallNegativeAxesID,
                    gridTouple.smallNegativeAxesID,
                    uniformObjectDataID,
                    0.3f,
                    0.5f,
                    1f,
                    0.25f );
                renderGridArray( gridTouple.smallPositiveAxesID,
                    gridTouple.smallPositiveAxesID,
                    uniformObjectDataID,
                    0.3f,
                    1f,
                    0.5f,
                    0.25f );
            }
            
            DiscreteHeightCameraBox dhcb = isometric2.getBox();
            
            float maxY = isometric.getCenterPoint().Y;
            int maxArrY = ms.translateSpaceToArrayY( 0, maxY, 0 );
            float angle = cameraRotation - 30;
            
            // System.err.println("\n\n");
            //for (int y = ms.terrainSize.y-1; y >= 0 ; y--) {
            float aboveMaxYAlpha = isometric.getAboveMaxYAlphaChannel();
            int maxArrYIterator = (
                                      aboveMaxYAlpha > 0 || maxArrY > ms.terrainSize.y - 1
            ) ? ( ms.terrainSize.y - 1 ) : maxArrY;
            for ( int arrY = 0; arrY <= maxArrYIterator; arrY++ ) {
                
                float Y = ms.translateArrayToSpaceY( 0, arrY, 0 );
                //System.err.println(arrY+" "+Y);
                CameraBoxXZDimension cbs = dhcb.getSlice( Y );
                
                {
                    PointXZ[] pts = cbs.getPoints();
                    Vector4f[] vec = cbs.getVec();
                    
                    float[] arrBnds = {
                        pts[3].X, Y, pts[3].Z,
                        pts[0].X, Y, pts[0].Z,
                        pts[0].X, Y, pts[0].Z,
                        pts[1].X, Y, pts[1].Z,
                        pts[1].X, Y, pts[1].Z,
                        pts[2].X, Y, pts[2].Z,
                        pts[2].X, Y, pts[2].Z,
                        pts[3].X, Y, pts[3].Z
                    };
                    float[] viewBnds = {
                        vec[3].x, vec[3].y, vec[3].z,
                        vec[0].x, vec[0].y, vec[0].z,
                        vec[0].x, vec[0].y, vec[0].z,
                        vec[1].x, vec[1].y, vec[1].z,
                        vec[1].x, vec[1].y, vec[1].z,
                        vec[2].x, vec[2].y, vec[2].z,
                        vec[2].x, vec[2].y, vec[2].z,
                        vec[3].x, vec[3].y, vec[3].z
                    };
                    int arrBndsID = Object3D.createBuffer();
                    Object3D.loadArrayBuffer( arrBndsID, arrBnds );
                    renderGridArray( arrBndsID,
                        arrBnds.length / 2,
                        uniformObjectDataID,
                        5,
                        0,
                        1,
                        1 );
                    Object3D.deleteBuffer( arrBndsID );
                    
                    int viewBndsID = Object3D.createBuffer();
                    Object3D.loadArrayBuffer( viewBndsID, viewBnds );
                    renderGridArray( viewBndsID,
                        viewBnds.length / 2,
                        uniformObjectDataID,
                        2,
                        1,
                        1,
                        0 );
                    Object3D.deleteBuffer( viewBndsID );
                    // System.err.println(Arrays.toString(arrBnds));
                    // System.err.println(Arrays.toString(viewBnds)+"\n");
                }
                
                float alpha = ( Y > maxY ) ? aboveMaxYAlpha : 1;
                float minX = cbs.getMinX();
                float maxX = cbs.getMaxX();
                
                int minArrX = ms.translateSpaceToArrayX( minX, Y, 0 );
                int maxArrX = ms.translateSpaceToArrayX( maxX, Y, 0 );
                //System.err.println("Y:"+Y+" minX:"+minX+" maxX"+maxX+" minArrX:"+minArrX+" maxArrX"+maxArrX);
                if ( maxArrX < 0 || minArrX >= ms.terrainSize.x ) {
                    continue;
                }
                if ( minArrX < 0 ) {
                    minArrX = 0;
                }
                if ( maxArrX > ms.terrainSize.x - 1 ) {
                    maxArrX = ms.terrainSize.x - 1;
                }
                //System.err.println("Y:"+Y+"("+arrY"+") minX:"+minX+"("+minArrX+") maxX:"+maxX+"("+maxArrX+")");
                
                {
                    float[] xBnd = {
                        minX, Y, 0,
                        maxX, Y, 0
                    };
                    int xBndID = Object3D.createBuffer();
                    Object3D.loadArrayBuffer( xBndID, xBnd );
                    renderGridArray( xBndID,
                        xBnd.length / 2,
                        uniformObjectDataID,
                        2,
                        0,
                        0,
                        1 );
                    Object3D.deleteBuffer( xBndID );
                }
                
                for ( int arrX = minArrX; arrX <= maxArrX; arrX++ ) {
                    float X = ms.translateArrayToSpaceX( arrX, arrY, 0 );
                    CameraBoxZDimension cbsX = cbs.getZDimension( X );
                    float minZ = cbsX.getMinZ();
                    float maxZ = cbsX.getMaxZ();
                    if ( minZ > maxZ ) {
                        continue;
                    }
                    
                    int minArrZ = ms.translateSpaceToArrayZ( X, Y, minZ );
                    int maxArrZ = ms.translateSpaceToArrayZ( X, Y, maxZ );
                    //System.err.println("Y:"+Y+"("+arrY+") X:"+X+"("+arrX+") minZ:"+minZ+"("+minArrZ+") maxZ:"+maxZ+"("+maxArrZ+")");
                    
                    if ( maxArrZ < 0 || minArrZ >= ms.terrainSize.z ) {
                        continue;
                    }
                    if ( minArrZ < 0 ) {
                        minArrZ = 0;
                    }
                    if ( maxArrZ > ms.terrainSize.z - 1 ) {
                        maxArrZ = ms.terrainSize.z - 1;
                    }
                    
                    {
                        float[] zBnd = {
                            X, Y, minZ,
                            X, Y, maxZ
                        };
                        int zBndID = Object3D.createBuffer();
                        Object3D.loadArrayBuffer( zBndID, zBnd );
                        renderGridArray( zBndID,
                            zBnd.length / 2,
                            uniformObjectDataID,
                            1,
                            1,
                            0,
                            0 );
                        Object3D.deleteBuffer( zBndID );
                    }
                    
                    for ( int arrZ = minArrZ; arrZ <= maxArrZ; arrZ++ ) {
                        Terrain3D ter = ms.terrainArray[arrX][arrY][arrZ];
                        if ( ter !=
                             null /* && ter.isVisibleArrayPos(arrX, arrY, arrZ, ms.terrainArray, ms.terrainSize)*/ )
                        {
                            //System.err.println(arrX+","+arrY+","+arrZ);
                            renderObject( 0,
                                0,
                                ter,
                                ter.convertArrayToSpacePosition( arrX,
                                    arrY,
                                    arrZ,
                                    ms.terrainVecMin ),
                                idVec4,
                                uniformObjectDataID,
                                alpha );
                        }
                    }
                    ArrayList< PositionableObject3D > al = ms.getObjectList( ( int ) java.lang.Math
                                                                                         .ceil( Y ) );
                    if ( al != null ) {
                        for ( PositionableObject3D obj :
                            ( List< PositionableObject3D > ) al.clone() ) {
                            BoundingBox bounds = obj.getBoundaries();
                            if ( bounds.getMinZ() <= maxZ && bounds.getMaxZ() >= minZ ) {
                                renderObject( 0,
                                    0,
                                    obj,
                                    obj.getPosition(),
                                    idVec4,
                                    uniformObjectDataID,
                                    alpha );
                            }
                        }
                    }
                }
            }
            //draw 2D Overlay
            
            //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            disableDepth();
            identity.bindCamera( matrixID );
            //renderObject(0,0,grid, grid.getPosition(), idVec4,uniformObjectDataID);
            double currentFrameTime = GLFW.glfwGetTime();
            if ( currentFrameTime > lastFrameUpdate + 0.016f ) {
                try {
                    double dx = currentFrameTime - lastFrameUpdate;
                    if ( text != null ) {
                        text.getTexture().releaseTexture();
                    }
                    if ( currentFrameTime > lastFpsUpdate + 1f ) {
                        lastFps = ( int ) ( frameCount / dx );
                        frameCount = 0;
                        lastFpsUpdate = currentFrameTime;
                    }
                    text = textFactory.getText3D( (
                                                      "FPS:" + lastFps
                                                      + "\ncamera Position:" +
                                                      ( int ) cameraCenterPoint.x + "," +
                                                      ( int ) cameraCenterPoint.y + "," +
                                                      ( int ) cameraCenterPoint.z
                                                      + "\ncamera size:" + cameraSize
                                                      + "\ncamera rotationY:" +
                                                      ( int ) cameraRotation
                                                      + "\ncamera rotationX:" +
                                                      ( int ) cameraRotationX
                        )
                        , 0, 0, wt.width, wt.height
                    );
                    // Plane3D obj = new Plane3D(text.getTexture());
                    
                    //System.out.println((1/dx));
                    // l.setIcon(new ImageIcon(text.getImage()));
                    // l.repaint();
                    //renderObject(obj,idVec, positionID, scaleID, textureID, vertexBuffer, textureBuffer);
                }
                catch ( IOException ex ) {
                    System.err.println( "Error while creating Text3D object: " + ex );
                }
                lastFrameUpdate = currentFrameTime;
            }
            renderObject( 0,
                0,
                text,
                text.getPosition(),
                idVec4,
                uniformObjectDataID,
                1 );
            
            GLFW.glfwSwapBuffers( wt.window ); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();
            /*try{
                this.wait();
            }catch(InterruptedException ex){}*/
            
        }
    }
    
    private boolean checkSupport()
    {
        boolean proceed = true;
        GLCapabilities capabilities = getCapabilities();
        if ( !capabilities.OpenGL11 ) {
            System.err.println( "opengl 1.1 not supported" );
            proceed = false;
        }
        if ( !capabilities.OpenGL15 ) {
            System.err.println( "opengl 1.5 not supported" );
            proceed = false;
        }
        if ( !capabilities.OpenGL20 ) {
            System.err.println( "opengl 2.0 not supported" );
            proceed = false;
        }
        if ( !capabilities.OpenGL30 ) {
            System.err.println( "opengl 3.0 not supported" );
            proceed = false;
        }
        if ( !capabilities.GL_ARB_shader_objects ) {
            System.err.println( "Shader objects not supported" );
            proceed = false;
        }
        if ( !capabilities.GL_ARB_vertex_shader ) {
            System.err.println( "Vertex shader not supported" );
            proceed = false;
        }
        if ( !capabilities.GL_ARB_fragment_shader ) {
            System.err.println( "Fragment shader not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glCreateShader ) {
            System.err.println( "Function glCreateShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glShaderSource ) {
            System.err.println( "Function glShaderSource not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glCompileShader ) {
            System.err.println( "Function glCompileShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glGetShaderiv ) {
            System.err.println( "Function glGetShaderiv not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glCreateProgram ) {
            System.err.println( "Function glCreateProgram not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glAttachShader ) {
            System.err.println( "Function glAttachShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glLinkProgram ) {
            System.err.println( "Function glLinkProgram not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glGetProgramInfoLog ) {
            System.err.println( "Function glGetProgramInfoLog not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glLinkProgram ) {
            System.err.println( "Function glLinkProgram not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glDetachShader ) {
            System.err.println( "Function glDetachShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glDeleteShader ) {
            System.err.println( "Function glDeleteShader not supported" );
            proceed = false;
        }
        if ( 0 == capabilities.glDeleteProgram ) {
            System.err.println( "Function glDeleteProgram not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDepthMask ) {
            System.err.println( "Function glDepthMask not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glEnable ) {
            System.err.println( "Function glEnable not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDepthFunc ) {
            System.err.println( "Function glDepthFunc not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glClear ) {
            System.err.println( "Function glClear not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDisable ) {
            System.err.println( "Function glDisable not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glCullFace ) {
            System.err.println( "Function glCullFace not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBlendFunc ) {
            System.err.println( "Function glBlendFunc not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glFrontFace ) {
            System.err.println( "Function glFrontFace not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glClearColor ) {
            System.err.println( "Function glClearColor not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glUseProgram ) {
            System.err.println( "Function glUseProgram not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glEnableVertexAttribArray ) {
            System.err.println(
                "Function glEnableVertexAttribArray not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBindBuffer ) {
            System.err.println( "Function glBindBuffer not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glUniform1fv ) {
            System.err.println( "Function glUniform1fv not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glVertexAttribPointer ) {
            System.err.println( "Function glVertexAttribPointer not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDrawArrays ) {
            System.err.println( "Function glDrawArrays not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glLineWidth ) {
            System.err.println( "Function glLineWidth not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glGetUniformLocation ) {
            System.err.println( "Function glGetUniformLocation not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glUniformMatrix4fv ) {
            System.err.println( "Function glUniformMatrix4fv not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBindBuffer ) {
            System.err.println( "Function glBindBuffer not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBufferData ) {
            System.err.println( "Function glBufferData not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glGenBuffers ) {
            System.err.println( "Function glGenBuffers not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDeleteBuffers ) {
            System.err.println( "Function glDeleteBuffers not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glDeleteTextures ) {
            System.err.println( "Function glDeleteTextures not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glGenTextures ) {
            System.err.println( "Function glGenTextures not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glBindTexture ) {
            System.err.println( "Function glBindTexture not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glTexImage2D ) {
            System.err.println( "Function glTexImage2D not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glTexParameteri ) {
            System.err.println( "Function glTexParameteri not supported" );
            proceed = false;
        }

        if ( 0 == capabilities.glGenerateMipmap ) {
            System.err.println( "Function glGenerateMipmap not supported" );
            proceed = false;
        }

        return proceed;
    }
    
    public enum WINDOW_MODE {
        /**
         * Windowed mode.
         */
        WINDOWED,
        /**
         * Fullscreen mode.
         */
        FULLSCREEN,
        /**
         * Fullscreen with fast task switch, minimalism on focus lost.
         */
        WINDOWED_FULLSCREEN,
        /**
         * Windowed without border.
         */
        BORDERLESS
    }

    
    private class KeyController extends Thread {
        
        boolean work = true;
        float speed1 = 15f;
        
        public void kill()
        {
            work = false;
        }
        
        @Override
        public void run()
        {
            while ( work ) {
                double currentTime = GLFW.glfwGetTime();
                if ( !KeyMap.isEmpty() ) {
                    float del = ( float ) ( ( currentTime - lastTime ) * speed1 );
                    for ( Entry< Integer, Integer > e : KeyMap.entrySet() ) {
                        if ( key( e.getKey(), del ) ) {
                            KeyMap.remove( e.getKey() );
                        }
                    }
                }
                lastTime = currentTime;
                try {
                    sleep( 1 );
                }
                catch ( InterruptedException ex ) {
                    System.err.println( "Controller sleep interrupted:" + ex );
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

}
