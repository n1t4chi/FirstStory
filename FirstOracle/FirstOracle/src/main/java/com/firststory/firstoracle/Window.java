/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import com.firststory.objects3D.AbsolutePositionable.AbsPositionableObject3D;
import com.firststory.objects3D.HexPrismUtil;
import com.firststory.objects3D.Object3D;
import com.firststory.objects3D.Texture;
import com.firststory.objects3D.Positionable.PositionableObject3D;
import com.firststory.objects3D.Positionable.Plane3D;
import com.firststory.objects3D.Terrain.Terrain3D;
import com.firststory.objects3D.AbsolutePostionable.Text.Text3D;
import com.firststory.objects3D.AbsolutePostionable.Text.Text3DFactory;
import static com.firststory.objects3D.Object3D.createBuffer;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import org.joml.*;

import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 *
 * @author n1t4chi
 */
public class Window implements Runnable {

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
    // The window handle
    //private long window;
    //Vertex Array handle
    //private int VertexArrayID;

    GLFWScrollCallback scrollCallback;
    GLFWKeyCallback keyCallback;
    GLFWCursorPosCallback cursorCallback;

    private float cameraRotation = 0;
    private float cameraSize = 25;
    Vector3f cameraCenterPoint = new Vector3f();
    private double lastTime = 0;
    //NumberFormat nf = new DecimalFormat("#,##0.00");

    private class WindowTouple {
        
        long window;
        int width;
        int height;
        
        final WINDOW_MODE windowMode;
        final String title;
        final int antiAliasing;
        int posX;
        int posY;
        boolean drawGrid = true;
        boolean drawBorder = true;
        boolean useTexture = true;
        final boolean verticalSync;
        GLFWErrorCallback errorCallback = null;
        public WindowTouple(WINDOW_MODE windowMode, String title, int width, int height, int pos_x, int pos_y,boolean verticalSync, int antiAliasing) {
            this.windowMode = windowMode;
            this.title = title;
            this.posX = pos_x;
            this.posY = pos_y;
            this.width = width;
            this.height = height;
            this.verticalSync = verticalSync;
            this.antiAliasing = antiAliasing;
        }
    }

    private final WindowTouple windowTouple;
    
    /**
     * Initialises window parameters.
     * @param windowMode window mode
     * @param title title of window
     * @param width -1 for default width
     * @param height -1 for default height
     * @param pos_x position of a window, does not count window border!. -1 for default.
     * @param pos_y position of a window, does not count window border!. -1 for default.
     * @param verticalSync verticalSync on/off
     * @param antiAliasing anti aliasing level
     */
    public Window(WINDOW_MODE windowMode, String title, int width, int height, int pos_x, int pos_y,boolean verticalSync, int antiAliasing) {
        windowTouple = new WindowTouple(windowMode, title, width, height, pos_x, pos_y, verticalSync, antiAliasing);
    }
    public Window(WINDOW_MODE windowMode, String title,boolean verticalSync,int antiAliasing) {
        this(windowMode,title,-1,-1,-1,-1,verticalSync,antiAliasing);
    }

    public void run() {
        KeyController controller = null;
        try{
            initOpenGL(windowTouple);
            controller = new KeyController();
            controller.start();
            setVisible(windowTouple.window);
            loop(windowTouple/*, vbo*/);
        }catch(Exception ex){
            System.err.println("Exception: ex"+ex);
            ex.printStackTrace();
        }finally{
            try{
                if(controller!=null){
                    controller.kill();
                }
                if(windowTouple!=null){
                    // Free the window callbacks and destroy the window
                    Callbacks.glfwFreeCallbacks(windowTouple.window);
                    GLFW.glfwDestroyWindow(windowTouple.window);
                    
                    
                }
            }finally{
                // Terminate GLFW and free the error callback
                GLFW.glfwTerminate();
                if(windowTouple !=null && windowTouple.errorCallback != null){
                    windowTouple.errorCallback.free();
                }
            }
        }
    }

    

    private void initWindow(WindowTouple windowTouple) {
        
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
        // Create the window
        int width = windowTouple.width;
        int height = windowTouple.height;
        width = (width > 0) ? width : mode.width();
        height = (height > 0) ? height : mode.height();
        windowTouple.width = width;
        windowTouple.height = height;
        long window;
        

        switch (windowTouple.windowMode) {
            case WINDOWED_FULLSCREEN:
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, mode.redBits());
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, mode.greenBits());
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, mode.blueBits());
                GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, mode.refreshRate());
            case FULLSCREEN:
                window = GLFW.glfwCreateWindow(width, height, windowTouple.title, monitor, NULL);
                break;
            case BORDERLESS:
                GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GL11.GL_FALSE);
            case WINDOWED:
            default:
                window = GLFW.glfwCreateWindow(width, height, windowTouple.title, NULL, NULL);
                break;
        }
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        windowTouple.window = window;
        int[] left = new int[1];
        int[] top = new int[1];
        int[] right = new int[1];
        int[] bottom = new int[1];
        
        GLFW.glfwGetWindowFrameSize(window, left, top, right, bottom);
        if (windowTouple.posX >= 0 && windowTouple.posY >= 0) {
            GLFW.glfwSetWindowPos(window, windowTouple.posX + left[0], windowTouple.posX + top[0]);
        }
    }

    private void enableDepth(){
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }
    private void disableDepth(){
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST); 
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Enables various functionality: vsync, depth test, alpha channels,
     * culling, counterclockwise order of vertex.
     */
    private void enableFunctionality() {
        // Enable v-sync
        if(windowTouple.verticalSync){
            GLFW.glfwSwapInterval(1);
        }else{
            GLFW.glfwSwapInterval(0);
        }
        // Enable depth test
          
        //alpha channels
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glFrontFace(GL11.GL_CCW);
    }
      
    private void setVisible(long window) {
        GLFW.glfwShowWindow(window);
    }

           
    /**
     * Initialises window
     *
     * @param windowMode window mode
     * @param title title of window
     * @param width -1 for default width
     * @param height -1 for default height
     * @param pos_x position of a window, does not count window border!. -1 for
     * default.
     * @param pos_y position of a window, does not count window border!. -1 for
     * default.
     * @param antiAliasing antialiasing
     * @return true (width,height)
     */
    private void initOpenGL(WindowTouple windowTouple) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        windowTouple.errorCallback = GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        
        // Configure GLFW
        GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE); // the window will be resizable
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, windowTouple.antiAliasing);
        initWindow(windowTouple);

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(windowTouple.window);
        GL.createCapabilities();

        if (!checkSupport()) {
            throw new UnsupportedOperationException("Lack of openGL support.");
        }
        enableFunctionality();
        GLFW.glfwSetScrollCallback(windowTouple.window, scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long l, double d, double d1) {
                //System.out.println("mouse sscroll: l:"+l+" d:"+d+" d1:"+d1);
                //mouse_dx = d;
                mouse_dy = d1;
                cameraSize -= d1;
                if (cameraSize < 1) {
                    cameraSize = 1;
                }
            }
        });

        GLFW.glfwSetKeyCallback(windowTouple.window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW.GLFW_PRESS) {
                    KeyMap.put(key, action);
                } else if (action == GLFW.GLFW_RELEASE) {
                    KeyMap.remove(key);
                }
            }
        });

    }
    ConcurrentHashMap<Integer, Integer> KeyMap  = new ConcurrentHashMap<>(10);

    private class KeyController extends Thread {

        boolean work = true;

        public void kill() {
            work = false;
        }
        float speed1 = 15f;

        @Override
        public void run() {
            while (work) {
                double currentTime = GLFW.glfwGetTime();
                if (!KeyMap.isEmpty()) {
                    float del = (float) ((currentTime - lastTime) * speed1);
                    for (Entry<Integer, Integer> e : KeyMap.entrySet()) {
                        if(key(e.getKey(), del)){
                            KeyMap.remove(e.getKey());
                        }
                    }
                }
                lastTime = currentTime;
                try {
                    sleep(1);
                } catch (InterruptedException ex) {
                    System.err.println("Controller sleep interrupted:" + ex);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    /**
     * 
     * @param key GLFW key code
     * @param del repeat action differentiation
     * @return whether to remove key or not.
     */
    private boolean key(int key, float del) {
        //System.out.println(del);
        switch (key) {
            case GLFW.GLFW_KEY_Q:
                //rot -= 45;
                cameraRotation -= 5*del;
                if (cameraRotation <= 0) {
                    cameraRotation = 360 - cameraRotation;
                }
                //return true;
                break;
            case GLFW.GLFW_KEY_E:
                //rot += 45;
                cameraRotation += 5*del;
                if (cameraRotation >= 360) {
                    cameraRotation = cameraRotation - 360;
                }
                //return true;
                break;
            case GLFW.GLFW_KEY_W:
                cameraCenterPoint.add(0, 0, del);
                break;
            case GLFW.GLFW_KEY_S:
                cameraCenterPoint.add(0, 0, -del);
                break;
            case GLFW.GLFW_KEY_A:
                cameraCenterPoint.add(del, 0, 0);
                break;
            case GLFW.GLFW_KEY_D:
                cameraCenterPoint.add(-del, 0, 0);
                break;
            case GLFW.GLFW_KEY_SPACE:
                cameraCenterPoint.add(0, del, 0);
                break;
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                cameraCenterPoint.add(0, -del, 0);
                break;
            default:
                return true;
        }
        return false;
    }

    double mouse_dy = 0;


    Terrain3D[][][] terrainArray;
    Vector3i terrainVecMin;
    Vector3i terrainVecMax;
    Vector3i terrainSize;

    int lastTextureID = 0;
    int lastVertexBufferID = 0;
    int lastUVBufferID = 0;
    private void renderObject(int frame,int direction,Object3D obj,Vector3fc objPos,Vector4fc objCol, int uniformDataID) {
        int buff_size = obj.getVertexCount();
        Vector3fc objScale = obj.getScale();
        Vector3fc objRot = obj.getRotation();
        float[] data = {
            objPos.x(),objPos.y(),objPos.z(),
            objScale.x(),objScale.y(),objScale.z(),
            objRot.x(),objRot.y(),objRot.z(),
            objCol.x(),objCol.y(),objCol.z(),objCol.w()
            ,-1//Alpha Override
        };
        GL20.glUniform1fv(uniformDataID,data);
        int vertexBufferID = obj.getVertexBufferID();
        if ( vertexBufferID >= 0 || vertexBufferID != lastVertexBufferID ) {
            //bind vertices
            obj.bindVertexBuffer();
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        }
        int UVBufferID = obj.getUVBufferID(frame, direction);
        if ( UVBufferID >= 0 || UVBufferID != lastUVBufferID ) {
            //bind uv map
            obj.bindUVBuffer(frame, direction);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        }
        if(windowTouple.useTexture){
            Texture tex = obj.getTexture();
            int textureID = tex.getTextureID();
            if (textureID >= 0 || textureID != lastTextureID) {
                tex.bindTexture();
            }
            lastTextureID = textureID;
        }
        lastVertexBufferID = vertexBufferID;
        lastUVBufferID = UVBufferID;
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, buff_size);
        

        if(windowTouple.drawBorder){
            float[] data2 = {
                objPos.x(),objPos.y(),objPos.z(),
                objScale.x(),objScale.y(),objScale.z(),
                objRot.x(),objRot.y(),objRot.z(),
                1,0,0,1,
                0.75f
            };
            GL20.glUniform1fv(uniformDataID,data2);
            GL11.glLineWidth(1);
            GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, buff_size);
        }

    }
    
    
    public class BackgroundScene{
        Iterator<Object3D> background2DObjectIterator;
        Iterator<Object3D> background3DObjectIterator;
    }
    public class MainScene{    
        Terrain3D[][][] terrainArray;
        Vector3ic terrainVecMin;
        Vector3ic terrainVecMax;
        Vector3ic terrainSize;
        
        
        
        
        
    }
    public class HUDScene{
        Iterator<AbsPositionableObject3D> background2DObjectIterator;
    }
    
    public class Camera{
        float rotationZ;
        float rotationY;
        float ratio;
        
        float positionX;
        float positionY;
        float positionZ;
        
        
        int renderMinX;
        int renderMaxX;
        int renderMinY;
        int renderMaxY;
        int renderMinZ;
        int renderMaxZ;
        float aboveYAlphaOverride;
    }
    
    public abstract class RenderedScene{
        
        abstract Camera get3DCamera();
        
        abstract BackgroundScene getBackgroundScene();
        
        abstract void get3DScene();
        
        abstract void getHUD();
        
        
        
        
        
        
        
        
        
        
    }
    
    
    /**
     * Map(level,list of objects).
     */
    HashMap<Integer,ArrayList<PositionableObject3D>> objectYMap = new HashMap<>(20);


    Text3D text = null;
        
  
    private static void renderGridArray(int gridID,int dataLength,int uniformObjectDataID,  float width,float red,float green,float blue){      
        float[] data = {
            0,0,0,
            1,1,1,
            0,0,0,
            red,green,blue,1, //color
            0.75f
        };
        GL20.glUniform1fv(uniformObjectDataID,data);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,gridID);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL11.glLineWidth(width);
        GL11.glDrawArrays(GL11.GL_LINES, 0, dataLength);
    }          
    private void loop(WindowTouple wt) {
        //GL.createCapabilities();

        
        Texture tex1, tex2, tex3;
        
        Font font = new Font("Times New Roman",Font.PLAIN,20);
        Text3DFactory textFactory = new Text3DFactory(font);
        
        try {
            text = textFactory.getText3D("FPS:",0,0,wt.width,wt.height);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Plane3D grid;
        try {
            grid = new Plane3D(new Texture("./EXT/grid.png",1,1,1,1));
            Terrain3D ter;
            tex1 = new Texture("./EXT/texture3DHEX2.png", 1, 1,1,1);
            terrainVecMin = new Vector3i(-50,0,-50);
            terrainVecMax = new Vector3i(50,8,50);
            terrainSize = new Vector3i(
                    terrainVecMax.x - terrainVecMin.x+( (java.lang.Math.signum(terrainVecMin.x)!=java.lang.Math.signum(terrainVecMax.y))?1:0 ) ,
                    terrainVecMax.y - terrainVecMin.y+( (java.lang.Math.signum(terrainVecMin.y)!=java.lang.Math.signum(terrainVecMax.z))?1:0 ),
                    terrainVecMax.z - terrainVecMin.z+( (java.lang.Math.signum(terrainVecMin.y)!=java.lang.Math.signum(terrainVecMax.z))?1:0 ) 
            );
            terrainArray = new Terrain3D[terrainSize.x][terrainSize.y][terrainSize.z];
            //ter = new com.firststory.objects3D.Terrain.HexPrismTerr3D(tex1);
            ter = new com.firststory.objects3D.Terrain.HexPrismTerr3D(tex1);
            for (int x = 0; x < terrainSize.x; x++) {
                for (int z = 0; z < terrainSize.z; z++) {
                        //int y=0;
                    for (int y = 0 ; y < terrainSize.y-1; y++) {
                        terrainArray[x][y][z]=ter;
                    }
                }
            }
            PositionableObject3D obj;
            tex1 = new Texture("./EXT/texture3D.png", 1, 1,1,1);
            ArrayList<PositionableObject3D> l = new ArrayList<>();
            float angle = (float) java.lang.Math.toRadians(270);
            for (int x = 0; x < terrainSize.x; x++) {
                for (int z = 0; z < terrainSize.z; z++) {
                    obj = new Plane3D(tex1);
                    //obj = new Plane3D(text.getTexture());
                    Vector3fc v = HexPrismUtil.convertArrayToSpacePosition(x, terrainSize.y-1, z,terrainVecMin);
                    obj.setPosition(v.x(),v.y()-0.5f,v.z());
                    obj.setScale(0.5f, 0.5f, 0.5f);
                    if(java.lang.Math.abs(x)%4 == 1){
                        obj.setRotationX(angle);
                    }
                    if(java.lang.Math.abs(x)%4 == 2){
                        obj.setRotationY(angle);
                    }
                    if(java.lang.Math.abs(x)%4 == 3){
                        obj.setRotationZ(angle);
                    }
                    l.add(obj);
                }
            }
            objectYMap.put(1, l);
        } catch (IOException ex) {
            System.err.println("Can't load texture:" + ex);
            return;
        }

        ShaderProgram shaderTexture;
        Texture emptyTexture;
        try {
            shaderTexture = new ShaderProgram("./EXT/shader.vert", "./EXT/shader.frag");
        } catch (Exception ex) {
            System.err.println("Error on shader loading: " + ex);
            return;
        }
        int matrixID = GL20.glGetUniformLocation(shaderTexture.getProgramID(), "camera");
        int uniformObjectDataID = GL20.glGetUniformLocation(shaderTexture.getProgramID(), "objectData");

        try {
            emptyTexture = new Texture(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
            emptyTexture.loadTexture();
        } catch (IOException ex) {
            System.err.println("Can't load texture:" + ex);
            return;
        }
        
        
        Matrix4f idMatrix = new Matrix4f();
        Vector4f idVec4 = new Vector4f(0,0,0,0);
        
        int frameCount = 0;
        double lastFrameUpdate = GLFW.glfwGetTime();
        
        
        GridTouple gridTouple = null;
        if(wt.drawGrid){
            gridTouple = new GridTouple(100,10);
        }
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL20.glUseProgram(shaderTexture.getProgramID());
        
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        
        ratio = (float) windowTouple.height / windowTouple.width;
        rotateX = (float) java.lang.Math.toRadians(30.0);
  
        while (!GLFW.glfwWindowShouldClose(wt.window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // clear the framebuffer      
            frameCount++;
            enableDepth();
            //draw 3D stuff
            try (MemoryStack stack = stackPush()) {
                Matrix4f mvp = computeIsometricView();
                GL20.glUniformMatrix4fv(matrixID, false, mvp.get(stack.callocFloat(16)));
            }
            if(gridTouple!=null){
                renderGridArray(gridTouple.mainAxesID,gridTouple.mainAxesLength,uniformObjectDataID,3,1,1,1);
                renderGridArray(gridTouple.interAxesID,gridTouple.interAxesLength,uniformObjectDataID,1,0.75f,0.75f,0.75f);
                renderGridArray(gridTouple.smallNegativeAxesID,gridTouple.smallNegativeAxesID,uniformObjectDataID,0.3f,0.5f,1f,0.5f);
                renderGridArray(gridTouple.smallPositiveAxesID,gridTouple.smallPositiveAxesID,uniformObjectDataID,0.3f,1f,0.5f,0.5f);
            }
           // float max = (float) java.lang.Math.sqrt(terrainVecMax.x*terrainVecMax.x + terrainVecMax.y*terrainVecMax.y + terrainVecMax.z*terrainVecMax.z);
            for (int y = terrainSize.y-1; y >= 0 ; y--) {
                //float y2 = (y+terrainVecMin.y); y2*=y2;
                for (int x = 0; x < terrainSize.x; x++) {
                    //float x2 = (x+terrainVecMin.x); x2*=x2;
                    for (int z = 0; z < terrainSize.z; z++) {
                        Terrain3D ter = terrainArray[x][y][z];
                        if(ter != null && ter.isVisibleArrayPos(x, y, z, terrainArray, terrainSize)){
                            //float z2 = (z+terrainVecMin.x); z2*=z2;
                            //float d = (float)java.lang.Math.sqrt(y2+x2+z2) / max;
                            //new Vector4f(0,0,0,d) swa
                            //renderObject(0,0,ter,ter.convertArrayToSpacePosition(x, y, z,terrainVecMin),idVec4 , uniformObjectDataID);
                            //idVec4
                        }
                    }
                }
                ArrayList<PositionableObject3D> al = objectYMap.get(y);
                if(al!=null){
                    for (PositionableObject3D obj : (List<PositionableObject3D>) al.clone()) {
                        renderObject(0,0,obj,obj.getPosition(), idVec4,uniformObjectDataID);
                    }
                }
            }
            //draw 2D Overlay
            
            
            //GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer      
            disableDepth();
            try (MemoryStack stack = stackPush()) {
                GL20.glUniformMatrix4fv(matrixID, false, idMatrix.get(stack.callocFloat(16)));
            }
            renderObject(0,0,grid, grid.getPosition(), idVec4,uniformObjectDataID);
            double currentFrameTime = GLFW.glfwGetTime();
            if(currentFrameTime > lastFrameUpdate+1f){
                try {
                    double dx = currentFrameTime - lastFrameUpdate;
                    if(text!=null)
                        text.getTexture().releaseTexture();
                    text = textFactory.getText3D((
                                "FPS:"+(int)(frameCount/dx)
                                +"\ncamera Position:"+cameraCenterPoint.x+","+cameraCenterPoint.y+","+cameraCenterPoint.z
                                +"\ncamera size:"+cameraSize
                                +"\ncamera rotation:"+cameraRotation
                            )
                            ,0,0,wt.width,wt.height
                    );
                    frameCount=0;
                   // Plane3D obj = new Plane3D(text.getTexture());

                    //System.out.println((1/dx));
                   // l.setIcon(new ImageIcon(text.getImage()));
                   // l.repaint();
                    //renderObject(obj,idVec, positionID, scaleID, textureID, vertexBuffer, textureBuffer);
                } catch (IOException ex) {
                    System.err.println("Error while creating Text3D object: "+ex);
                }
                lastFrameUpdate = currentFrameTime;
            }
            renderObject(0,0,text,text.getPosition(), idVec4,uniformObjectDataID);
            
            
            

            GLFW.glfwSwapBuffers(wt.window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();
        }
    }
 
    float ratio = 1;
    float rotateX = 1;

    private Matrix4f computeIsometricView() {
        float planeX = cameraSize;
        //float planeZ = (size+1)*(size+1);
        float planeY = (planeX) * ratio;
        float planeZ = (5+cameraSize); planeZ*=planeZ;
        //planeZ +=105;
        Matrix4f rtrn = (new Matrix4f()).ortho(-planeX,planeX,-planeY,planeY,-planeZ,planeZ);
        
        //System.err.println(size+"\n"+rtrn.toString(nf)+"\n");
        
        rtrn.rotateX(rotateX);
        rtrn.rotateY((float) -java.lang.Math.toRadians(45.0 + cameraRotation));
        
        rtrn.translate(cameraCenterPoint.x, cameraCenterPoint.y, cameraCenterPoint.z);

        //System.out.println(rtrn.toString(nf));
        return rtrn;
    }

    private boolean checkSupport() {
        boolean proceed = true;
        GLCapabilities capabilities = getCapabilities();  
        if (!capabilities.OpenGL11) {
            System.err.println("opengl 1.1 not supported");
            proceed = false;
        }
        if (!capabilities.OpenGL15) {
            System.err.println("opengl 1.5 not supported");
            proceed = false;
        }
        if (!capabilities.OpenGL20) {
            System.err.println("opengl 2.0 not supported");
            proceed = false;
        }
        if (!capabilities.OpenGL30) {
            System.err.println("opengl 3.0 not supported");
            proceed = false;
        }
        if (!capabilities.GL_ARB_shader_objects) {
            System.err.println("Shader objects not supported");
            proceed = false;
        }
        if (!capabilities.GL_ARB_vertex_shader) {
            System.err.println("Vertex shader not supported");
            proceed = false;
        }
        if (!capabilities.GL_ARB_fragment_shader) {
            System.err.println("Fragment shader not supported");
            proceed = false;
        }
        
        if(0 == capabilities.glCreateShader){
            System.err.println("Function glCreateShader not supported");
            proceed = false;
        }
        if(0 == capabilities.glShaderSource){
            System.err.println("Function glShaderSource not supported");
            proceed = false;
        }
        if(0 == capabilities.glCompileShader){
            System.err.println("Function glCompileShader not supported");
            proceed = false;
        }
        if(0 == capabilities.glGetShaderiv){
            System.err.println("Function glGetShaderiv not supported");
            proceed = false;
        }
        if(0 == capabilities.glCreateProgram){
            System.err.println("Function glCreateProgram not supported");
            proceed = false;
        }
        if(0 == capabilities.glAttachShader){
            System.err.println("Function glAttachShader not supported");
            proceed = false;
        }
        if(0 == capabilities.glLinkProgram){
            System.err.println("Function glLinkProgram not supported");
            proceed = false;
        }
        if(0 == capabilities.glGetProgramInfoLog){
            System.err.println("Function glGetProgramInfoLog not supported");
            proceed = false;
        }
        if(0 == capabilities.glLinkProgram){
            System.err.println("Function glLinkProgram not supported");
            proceed = false;
        }
        if(0 == capabilities.glDetachShader){
            System.err.println("Function glDetachShader not supported");
            proceed = false;
        }
        if(0 == capabilities.glDeleteShader){
            System.err.println("Function glDeleteShader not supported");
            proceed = false;
        }
        if(0 == capabilities.glDeleteProgram){
            System.err.println("Function glDeleteProgram not supported");
            proceed = false;
        }

        if(0 == capabilities.glDepthMask){
            System.err.println("Function glDepthMask not supported");
            proceed = false;
        }

        if(0 == capabilities.glEnable){
            System.err.println("Function glEnable not supported");
            proceed = false;
        }

        if(0 == capabilities.glDepthFunc){
            System.err.println("Function glDepthFunc not supported");
            proceed = false;
        }

        if(0 == capabilities.glClear){
            System.err.println("Function glClear not supported");
            proceed = false;
        }

        if(0 == capabilities.glDisable){
            System.err.println("Function glDisable not supported");
            proceed = false;
        }


        if(0 == capabilities.glCullFace){
            System.err.println("Function glCullFace not supported");
            proceed = false;
        }

        if(0 == capabilities.glBlendFunc){
            System.err.println("Function glBlendFunc not supported");
            proceed = false;
        }

        if(0 == capabilities.glFrontFace){
            System.err.println("Function glFrontFace not supported");
            proceed = false;
        }

        if(0 == capabilities.glClearColor){
            System.err.println("Function glClearColor not supported");
            proceed = false;
        }

        if(0 == capabilities.glUseProgram){
            System.err.println("Function glUseProgram not supported");
            proceed = false;
        }

        if(0 == capabilities.glEnableVertexAttribArray){
            System.err.println("Function glEnableVertexAttribArray not supported");
            proceed = false;
        }

        if(0 == capabilities.glBindBuffer){
            System.err.println("Function glBindBuffer not supported");
            proceed = false;
        }

        if(0 == capabilities.glUniform1fv){
            System.err.println("Function glUniform1fv not supported");
            proceed = false;
        }

        if(0 == capabilities.glVertexAttribPointer){
            System.err.println("Function glVertexAttribPointer not supported");
            proceed = false;
        }

        if(0 == capabilities.glDrawArrays){
            System.err.println("Function glDrawArrays not supported");
            proceed = false;
        }

        if(0 == capabilities.glLineWidth){
            System.err.println("Function glLineWidth not supported");
            proceed = false;
        }

        if(0 == capabilities.glGetUniformLocation){
            System.err.println("Function glGetUniformLocation not supported");
            proceed = false;
        }


        if(0 == capabilities.glUniformMatrix4fv){
            System.err.println("Function glUniformMatrix4fv not supported");
            proceed = false;
        }

        if(0 == capabilities.glBindBuffer){
            System.err.println("Function glBindBuffer not supported");
            proceed = false;
        }

        if(0 == capabilities.glBufferData){
            System.err.println("Function glBufferData not supported");
            proceed = false;
        }

        if(0 == capabilities.glGenBuffers){
            System.err.println("Function glGenBuffers not supported");
            proceed = false;
        }

        if(0 == capabilities.glDeleteBuffers){
            System.err.println("Function glDeleteBuffers not supported");
            proceed = false;
        }

        if(0 == capabilities.glDeleteTextures){
            System.err.println("Function glDeleteTextures not supported");
            proceed = false;
        }

        if(0 == capabilities.glGenTextures){
            System.err.println("Function glGenTextures not supported");
            proceed = false;
        }

        if(0 == capabilities.glBindTexture){
            System.err.println("Function glBindTexture not supported");
            proceed = false;
        }

        if(0 == capabilities.glTexImage2D){
            System.err.println("Function glTexImage2D not supported");
            proceed = false;
        }

        if(0 == capabilities.glTexParameteri){
            System.err.println("Function glTexParameteri not supported");
            proceed = false;
        }

        if(0 == capabilities.glGenerateMipmap){
            System.err.println("Function glGenerateMipmap not supported");
            proceed = false;
        }

        return proceed;
    }
    
    
    
    
    
    private class GridTouple{
        int mainAxesID=0;
        int interAxesID=0;
        int smallPositiveAxesID=0;
        int smallNegativeAxesID=0;
        int mainAxesLength=0;
        int interAxesLength=0;
        int smallPositiveAxesLength=0;
        int smallNegativeAxesLength=0;
        public GridTouple(int gridSize,int interAxesStep){
            mainAxesID = createBuffer();
            interAxesID = createBuffer();
            smallPositiveAxesID = createBuffer();
            smallNegativeAxesID = createBuffer();
            int interAxesSize = gridSize/interAxesStep;
            int smallPositiveAxesSize = (gridSize-interAxesSize)/2;
            int smallNegativeAxesSize = smallPositiveAxesSize;


            //3 axes * 2 perpendicular lines * 2 sides * 2 point * 3 coordinates * 2 gridsize = 144
            interAxesSize*=144;
            smallNegativeAxesSize*=144  * 2 * gridSize;
            smallPositiveAxesSize*=144  * 2 * gridSize;
            float[] mainAxesArray = {
                -gridSize,0,0,
                gridSize,0,0,//X

                0,-gridSize,0,
                0,gridSize,0,//Y

                0,0,-gridSize,
                0,0,gridSize//Z
            };
            float[] interAxesArray = new float[interAxesSize];
            float[] smallPositiveAxesArray = new float[smallPositiveAxesSize]; 
            float[] smallNegativeAxesArray = new float[smallNegativeAxesSize]; 
            int interAxesArrayIt = 0;
            int smallPositiveAxesArrayIt = 0;
            int smallNegativeAxesArrayIt = 0;


            for(int i=-gridSize;i<=gridSize;i++){
                if(i==0){
                    continue;
                }
                float[] axes = {
                    //perpendicular to Z axis
                    i,0,-gridSize,
                    i,0,gridSize,
                    -i,0,-gridSize,
                    -i,0,gridSize,

                    0,i,-gridSize,
                    0,i,gridSize,
                    0,-i,-gridSize,
                    0,-i,gridSize,

                    //perpendicular to Y axis
                    i,-gridSize,0,
                    i,gridSize,0,
                    -i,-gridSize,0,
                    -i,gridSize,0,

                    0,-gridSize,i,
                    0,gridSize,i,
                    0,-gridSize,-i,
                    0,gridSize,-i,


                    //perpendicular to Z axis
                    -gridSize,i,0,
                    gridSize,i,0,
                    -gridSize,-i,0,
                    gridSize,-i,0,

                    -gridSize,0,i,
                    gridSize,0,i,
                    -gridSize,0,-i,
                    gridSize,0,-i,
                };
                if( java.lang.Math.abs(i%interAxesStep) != 0 ){
                    boolean positive = false;
                    for(int j=0 ; j<axes.length;j++){
                        if(j%6 == 0) {
                            positive = !positive;
                        }
                        if(positive){
                            smallPositiveAxesArray[smallPositiveAxesArrayIt] = axes[j];
                            smallPositiveAxesArrayIt++;
                        }else{
                            smallNegativeAxesArray[smallNegativeAxesArrayIt] = axes[j];
                            smallNegativeAxesArrayIt++;
                        }
                    }
                }else{
                    for(int j=0 ; j<axes.length;j++){
                        interAxesArray[interAxesArrayIt + j] = axes[j];
                    }   
                    interAxesArrayIt += axes.length;
                }
            }
            Object3D.loadArrayBuffer(mainAxesID, mainAxesArray);
            Object3D.loadArrayBuffer(interAxesID, interAxesArray);
            Object3D.loadArrayBuffer(smallPositiveAxesID, smallPositiveAxesArray);
            Object3D.loadArrayBuffer(smallNegativeAxesID, smallNegativeAxesArray);

            mainAxesLength=mainAxesArray.length/2;
            interAxesLength=interAxesArray.length/2;
            smallPositiveAxesLength=smallPositiveAxesArray.length/2;
            smallNegativeAxesLength=smallNegativeAxesArray.length/2;
        }
    }
    
    
    
    
}
