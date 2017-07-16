/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import com.firststory.objects3D.HexPrismUtil;
import static com.firststory.objects3D.HexPrismUtil.SQRT3_DIV2;
import com.firststory.objects3D.Positionable.Cube3D;
import com.firststory.objects3D.Positionable.HexPrism3D;
import com.firststory.objects3D.Object3D;
import com.firststory.objects3D.Texture;
import com.firststory.objects3D.Positionable.PositionableObject3D;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Positionable.Plane3D;
import com.firststory.objects3D.Terrain.Terrain3D;
import java.awt.Font;
import static java.awt.Font.PLAIN;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import org.joml.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.ARBVertexBufferObject.*;
import sun.font.TrueTypeFont;

/**
 *
 * @author n1t4chi
 */
public class Window {

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

    // position
    Vector3f position = new Vector3f(0, 0, 5);
    // horizontal angle : toward -Z
    float horizontalAngle = 3.14f;
    // vertical angle : 0, look at the horizon
    float verticalAngle = 0.0f;
    // Initial Field of View
    float initialFoV = 45.0f;
    float speed = 3.0f; // 3 units / second
    float mouseSpeed = 0.5f;
    int xpos = 0, ypos = 0;
    int dx = 0;
    int dy = 0;
    double lastTime = 0;
    NumberFormat nf = new DecimalFormat("#,##0.00");

    /**
     * Initialises window and starts whole application.
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
     */
    public void run(WINDOW_MODE windowMode, String title, int width, int height, int pos_x, int pos_y, int antiAliasing) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        WindowTouple wt = null;
        KeyController controller = null;
        VBOPair vbo = null;
        try{
            wt = init(windowMode, title, width, height, pos_x, pos_y, antiAliasing);
            controller = new KeyController();
            controller.start();
            vbo = initVBO(PositionableObject3D.getCommonVertex());
            setVisible(wt.window);
            loop(wt, vbo);
        }finally{
            try{
                if(controller!=null){
                    controller.kill();
                }
                if(vbo!=null){
                    glDeleteBuffersARB(vbo.VBOsize);
                    glDeleteBuffersARB(vbo.VBOVertex);
                }
                if(wt!=null){
                    // Free the window callbacks and destroy the window
                    glfwFreeCallbacks(wt.window);
                    glfwDestroyWindow(wt.window);
                }
            }finally{
                // Terminate GLFW and free the error callback
                glfwTerminate();
                glfwSetErrorCallback(null).free();
            }
        }
    }

    private class WindowTouple {

        final long window;
        final int width;
        final int height;

        public WindowTouple(long window, int width, int height) {
            this.window = window;
            this.width = width;
            this.height = height;
        }
    }

    private WindowTouple initWindow(WINDOW_MODE windowMode, String title, int width, int height, int pos_x, int pos_y) {
        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode mode = glfwGetVideoMode(monitor);
        // Create the window
        width = (width > 0) ? width : mode.width();
        height = (height > 0) ? height : mode.height();
        long window;
        switch (windowMode) {
            case WINDOWED_FULLSCREEN:
                glfwWindowHint(GLFW_RED_BITS, mode.redBits());
                glfwWindowHint(GLFW_GREEN_BITS, mode.greenBits());
                glfwWindowHint(GLFW_BLUE_BITS, mode.blueBits());
                glfwWindowHint(GLFW_REFRESH_RATE, mode.refreshRate());
            case FULLSCREEN:
                window = glfwCreateWindow(width, height, title, monitor, NULL);
                break;
            case BORDERLESS:
                glfwWindowHint(GLFW_DECORATED, GL_FALSE);
            case WINDOWED:
            default:
                window = glfwCreateWindow(width, height, title, NULL, NULL);
                break;
        }
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        int[] left = new int[1];
        int[] top = new int[1];
        int[] right = new int[1];
        int[] bottom = new int[1];
        glfwGetWindowFrameSize(window, left, top, right, bottom);
        if (pos_x >= 0 && pos_y >= 0) {
            glfwSetWindowPos(window, pos_x + left[0], pos_y + top[0]);
        }
        return new WindowTouple(window, width, height);
    }

    /**
     * Enables various functionality: vsync, depth test, alpha channels,
     * culling, counterclockwise order of vertex.
     */
    private void enableFunctionality() {
        // Enable v-sync
        glfwSwapInterval(1);

        // Enable depth test
        glEnable(GL_DEPTH_TEST);
        // Accept fragment if it closer to the camera than the former one
        glDepthFunc(GL_LEQUAL);
        //alpha channels
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glFrontFace(GL_CCW);
    }

    private void setVisible(long window) {
        glfwShowWindow(window);
    }

    private class VBOPair {

        final int VBOVertex;
        final int VBOsize;

        public VBOPair(int VBOVertex, int VBOIndex) {
            this.VBOVertex = VBOVertex;
            this.VBOsize = VBOIndex;
        }
    }

    private VBOPair initVBO(float[] commonVertexData) {
        int VBOVertex = glGenBuffersARB();
        glBindBufferARB(GL_ARRAY_BUFFER_ARB, VBOVertex);
        glBufferDataARB(GL_ARRAY_BUFFER_ARB, commonVertexData, GL_STATIC_DRAW_ARB);
        return new VBOPair(VBOVertex, commonVertexData.length);
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
    private WindowTouple init(WINDOW_MODE windowMode, String title, int width, int height, int pos_x, int pos_y, int antiAliasing) {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_SAMPLES, antiAliasing);

        WindowTouple wt = initWindow(windowMode, title, width, height, pos_x, pos_y);

        // Make the OpenGL context current
        glfwMakeContextCurrent(wt.window);
        GL.createCapabilities();

        if (!checkSupport()) {
            throw new UnsupportedOperationException("Lack of openGL support.");
        }
        enableFunctionality();

        try (MemoryStack stack = stackPush()) {
            DoubleBuffer b1 = stack.callocDouble(1);
            DoubleBuffer b2 = stack.callocDouble(1);
            glfwGetCursorPos(wt.window, b1, b2);
            xpos = (int) b1.get();
            ypos = (int) b2.get();
        }
        GLFW.glfwSetCursorPosCallback(wt.window, cursorCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long arg0, double arg1, double arg2) {
                int x = (int) arg1;
                int y = (int) arg2;
                if (glfwGetMouseButton(wt.window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                    //glfwSetCursorPos(window,width/2, height/2); xpos - x  ypos - y
                    dx = xpos - x;
                    dy = ypos - y;
                }
                xpos = x;
                ypos = y;
            }
        });
        GLFW.glfwSetScrollCallback(wt.window, scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long l, double d, double d1) {
                //System.out.println("mouse sscroll: l:"+l+" d:"+d+" d1:"+d1);
                //mouse_dx = d;
                mouse_dy = d1;
                size -= d1;
                if (size < 3) {
                    size = 3;
                }
            }
        });

        GLFW.glfwSetKeyCallback(wt.window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW_PRESS) {
                    KeyMap.put(key, action);
                } else if (action == GLFW_RELEASE) {
                    KeyMap.remove(key);
                }
            }
        });

        return wt;
    }
    ConcurrentHashMap<Integer, Integer> KeyMap = new ConcurrentHashMap<>(10);

    private class KeyController extends Thread {

        boolean work = true;

        public void kill() {
            work = false;
        }
        float speed1 = 15f;

        @Override
        public void run() {
            while (work) {
                double currentTime = glfwGetTime();
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
            case GLFW_KEY_Q:
                //rot -= 45;
                rot -= 5*del;
                if (rot <= 0) {
                    rot = 360 - rot;
                }
                //return true;
                break;
            case GLFW_KEY_E:
                //rot += 45;
                rot += 5*del;
                if (rot >= 360) {
                    rot = rot - 360;
                }
                //return true;
                break;
            case GLFW_KEY_W:
                pos.add(0, 0, del);
                break;
            case GLFW_KEY_S:
                pos.add(0, 0, -del);
                break;
            case GLFW_KEY_A:
                pos.add(del, 0, 0);
                break;
            case GLFW_KEY_D:
                pos.add(-del, 0, 0);
                break;
            case GLFW_KEY_SPACE:
                pos.add(0, del, 0);
                break;
            case GLFW_KEY_LEFT_CONTROL:
                pos.add(0, -del, 0);
                break;
            default:
                return true;
        }
        return false;
    }

    double mouse_dy = 0;

    public void renderVBO(int VBO, int VBOmax, int UVMapID, int TrianglesID, int TrianglesSize) {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, VBO);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, UVMapID);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);

        ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, TrianglesID);
        GL12.glDrawRangeElements(GL11.GL_TRIANGLES, 0, VBOmax, TrianglesSize, GL11.GL_UNSIGNED_SHORT, 0);
    }

    Texture lastTexture = null;
    Object3DType lastObjectType = null;
    double lastVertexID = 0;
    double lastUVID = 0;
    java.awt.Font font = new java.awt.Font("Times New Roman", Font.BOLD, 24);
    
    private void render_text(char[] text , float x, float y, float sx, float sy){
      //  font.
    }

    private void renderObject(Object3D obj,Vector3fc objPos, int positionID, int scaleID, int textureID, int vertexBuffer, int textureBuffer) {

        float[] vertex = obj.getTriangles();
        int buff_size = vertex.length / 3;
        // float g_color_buffer_data[] = new float[buff_size*4];
        float UVMap[] = obj.getUV(0);

        Vector3fc objScale = obj.getScale();
        glUniform3f(positionID, objPos.x(), objPos.y(), objPos.z());
        glUniform3f(scaleID, objScale.x(), objScale.y(), objScale.z());

        if (lastObjectType != obj.getType() || lastVertexID != obj.getVertexID()) {
            //1st attribute buffer: vertices
            glDisableVertexAttribArray(0);
            glEnableVertexAttribArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        }
        if (lastObjectType != obj.getType() || lastUVID != obj.getUVID()) {
            glDisableVertexAttribArray(1);
            glEnableVertexAttribArray(1);
            glBindBuffer(GL_ARRAY_BUFFER, textureBuffer);
            glBufferData(GL_ARRAY_BUFFER, UVMap, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        }

        Texture tex = obj.getTexture();
        if (!tex.equals(lastTexture)) {
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, tex.getWidth(), tex.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, tex.getTexture());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glGenerateMipmap(GL_TEXTURE_2D);
        }
        lastTexture = tex;
        lastObjectType = obj.getType();
        lastUVID = obj.getUVID();
        lastVertexID = obj.getVertexID();

        glDrawArrays(GL_TRIANGLES, 0, buff_size);

    }
    /**
     * Map(level,list of objects).
     */
    HashMap<Integer,ArrayList<PositionableObject3D>> objectYMap = new HashMap<>(20);
    Vector3i terrainVecMin;
    Vector3i terrainVecMax;
    Vector3i terrainSize;
    Terrain3D[][][] terrainArray;

    private void loop(WindowTouple wt, VBOPair vbo) {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        Texture tex1, tex2, tex3;
        try {
            Terrain3D ter;
            tex1 = new Texture("./EXT/texture3DHEX2.png", 1, 1);
            terrainVecMin = new Vector3i(-60,0,-60);
            terrainVecMax = new Vector3i(60,21,60);
            terrainSize = new Vector3i(
                    terrainVecMax.x - terrainVecMin.x+( (java.lang.Math.signum(terrainVecMin.x)!=java.lang.Math.signum(terrainVecMax.y))?1:0 ) ,
                    terrainVecMax.y - terrainVecMin.y+( (java.lang.Math.signum(terrainVecMin.y)!=java.lang.Math.signum(terrainVecMax.z))?1:0 ),
                    terrainVecMax.z - terrainVecMin.z+( (java.lang.Math.signum(terrainVecMin.y)!=java.lang.Math.signum(terrainVecMax.z))?1:0 ) 
            );
            terrainArray = new Terrain3D[terrainSize.x][terrainSize.y][terrainSize.z];
            ter = new com.firststory.objects3D.Terrain.HexPrismTerr3D(tex1);
            for (int x = terrainVecMin.x; x <= terrainVecMax.x; x++) {
                for (int z = terrainVecMin.z; z <= terrainVecMax.z; z++) {
                    for (int y = terrainVecMin.y; y <= terrainVecMax.y-1; y++) {
                        terrainArray[x-terrainVecMin.x][y-terrainVecMin.y][z-terrainVecMin.z]=ter;
                    }
                }
            }
            PositionableObject3D obj;
            tex1 = new Texture("./EXT/texture3D.png", 1, 1);
            ArrayList<PositionableObject3D> l = new ArrayList<>();
            for (int x = terrainVecMin.x; x <= terrainVecMax.x; x++) {
                for (int z = terrainVecMin.z; z <= terrainVecMax.z; z++) {
                    obj = new Cube3D(tex1);
                    Vector3fc v = HexPrismUtil.convertArrayToSpacePosition(x, 21, z,terrainVecMin);
                    obj.setPosition(v.x(),v.y()-0.5f,v.z());
                    obj.setScale(0.5f, 0.5f, 0.5f);
                    
                    l.add(obj);
                }
            }
            objectYMap.put(21, l);
        } catch (IOException ex) {
            System.err.println("Can't load texture:" + ex);
            return;
        }

//        float[] vertex = obj.getTriangles();
        //int buff_size = vertex.length / 3;
        // float g_color_buffer_data[] = new float[buff_size*4];
        //int textureID = glGenTextures();
        //float UVMap[] = obj.getUVMap(0);

        /* for (int i = 0; i < UVMap.length; i++) {
            System.out.print(UVMap[i] + ",");
            if (i % 6 == 5) {
                System.out.println("");
            } else if (i % 2 == 1) {
                System.out.print(" ");
            }
        }
        System.out.println("");*/
        //fp.put(g_vertex_buffer_data);
        int vertexBuffer = glGenBuffers();
        int textureBuffer = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
//        glBufferData(GL_ARRAY_BUFFER, vertex, GL_STATIC_DRAW);

//        glBindBuffer(GL_ARRAY_BUFFER, texturebuffer);
//        glBufferData(GL_ARRAY_BUFFER, UVMap, GL_STATIC_DRAW);
        //   int colourbuffer= glGenBuffers();
        //   glBindBuffer(GL_ARRAY_BUFFER, colourbuffer);
        //  glBufferData(GL_ARRAY_BUFFER, g_color_buffer_data, GL_STATIC_DRAW);
        //System.err.println("here8");
        //System.err.println("here8.1");
        //System.err.println("here8.2");
        //System.err.println("here8.3");
        //System.err.println("here8.4");
        //System.err.println("here8.5");
        // Generate mipmaps, by the way.
        //System.err.println("here9");
        ShaderProgram shader;
        try {
            shader = new ShaderProgram("./EXT/shader.vert", "./EXT/shader.frag");
        } catch (Exception ex) {
            System.err.println("Error on shader loading: " + ex);
            return;
        }
        int textureID = glGenTextures();
        int matrixID = glGetUniformLocation(shader.getProgramID(), "camera");
        int positionID = glGetUniformLocation(shader.getProgramID(), "objPos");
        int scaleID = glGetUniformLocation(shader.getProgramID(), "objScale");
        glUseProgram(shader.getProgramID());
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        //System.err.println("here10");
        while (!glfwWindowShouldClose(wt.window)) {
            //System.out.println("\n\n#######################################");
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer      
            System.out.println(pos.toString(nf));
            //glClearDepth(1000);
            //1st attribute buffer: vertices
//            glEnableVertexAttribArray(0);
//            glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
//            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //2nd attribute buffer: colours
            //glEnableVertexAttribArray(1);
            //glBindBuffer(GL_ARRAY_BUFFER, colourbuffer);
            //glVertexAttribPointer(1,4, GL_FLOAT, false, 0, 0);
            //System.err.println("here11");
            //2nd attribute buffer:  texture
//            glEnableVertexAttribArray(1);
//            glBindBuffer(GL_ARRAY_BUFFER, texturebuffer);
//            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            //System.err.println("here12");
            try (MemoryStack stack = stackPush()) {
                //System.out.println("pos:" + pos.toString(nf) + " rot:" + rot);

                /* Matrix4f Model = new Matrix4f();
                
                Matrix4f Projection;
                Matrix4f View;
                Entry<Matrix4f,Matrix4f> ent = computeMatricesFromInputs(wt.window,wt.width,wt.height);
                Projection = ent.getKey();
                View = ent.getValue();
                Matrix4f mvp = Projection.mul(View.mul(Model));*/
                Matrix4f mvp = computeMatricesFromInputs(wt);

                //System.out.println("proj:"+Projection.toString(nf));
                //System.out.println("view:"+View.toString(nf));
                //System.out.println("mvp:"+mvp.toString(nf));
                glUniformMatrix4fv(matrixID, false, mvp.get(stack.callocFloat(16)));
            }
            for (int y = terrainVecMin.y; y <= terrainVecMax.y-2; y++) {
                for (int x = terrainVecMin.x; x <= terrainVecMax.x; x++) {
                    for (int z = terrainVecMin.z; z <= terrainVecMax.z; z++) {
                        Terrain3D ter = terrainArray[x-terrainVecMin.x][y-terrainVecMin.y][z-terrainVecMin.z];
                        if(ter != null && ter.isVisible(x, y, z,terrainVecMin, terrainArray, terrainSize)){
                            //System.out.println("Render "+x+","+y+","+z);
                            renderObject(ter,ter.convertArrayToSpacePosition(x, y, z,terrainVecMin), positionID, scaleID, textureID, vertexBuffer, textureBuffer);
                        }
                    }
                }
                ArrayList<PositionableObject3D> al = objectYMap.get(y);
                if(al!=null){
                    for (PositionableObject3D obj : (List<PositionableObject3D>) al.clone()) {
                        renderObject(obj,obj.getPosition(), positionID, scaleID, textureID, vertexBuffer, textureBuffer);
                    }
                }
            }
            

            //draw triangle:
//            glDrawArrays(GL_TRIANGLES, 0, buff_size);
//            glDisableVertexAttribArray(0);
            glfwSwapBuffers(wt.window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            //System.out.println("#######################################\n\n");
        }
    }

    float rot = 0;
    float size = 25;

    private Matrix4f computeMatricesFromInputs(WindowTouple wt) {
        float ratio = (float) wt.height / wt.width;
        float planeX = size;
        float planeY = planeX * ratio;
        float planeZ = planeX * planeY;
        Matrix4f rtrn = (new Matrix4f()).ortho(-planeX, planeX, -planeY, planeY, -planeZ, planeZ);

        rtrn.rotateX((float) java.lang.Math.toRadians(30.0));
        rtrn.rotateY((float) -java.lang.Math.toRadians(45.0 + rot));
        float camera_height = pos.y;
        rtrn.translate(-pos.x, -camera_height, -pos.z);

        //System.out.println(rtrn.toString(nf));
        return rtrn;
    }

    Vector3f pos = new Vector3f();

    /**
     *
     * @param window
     * @param width
     * @param height
     * @return (Projection,View) matrices pair.
     */
    private Entry<Matrix4f, Matrix4f> computeMatricesFromInputs(long window, int width, int height) {
        double currentTime = glfwGetTime();
        float deltaTime = (float) (currentTime - lastTime);
        lastTime = currentTime;

        float FoV = (float) (initialFoV - 5 * mouse_dy);
        mouse_dy = 0;
        initialFoV = FoV;

        horizontalAngle += mouseSpeed * deltaTime * (dx);
        verticalAngle += mouseSpeed * deltaTime * (dy);
        dx = 0;
        dy = 0;
        // Direction : Spherical coordinates to Cartesian coordinates conversion
        Vector3f direction = new Vector3f(
                (float) (java.lang.Math.cos(verticalAngle) * java.lang.Math.sin(horizontalAngle)),
                (float) (java.lang.Math.sin(verticalAngle)),
                (float) (java.lang.Math.cos(verticalAngle) * java.lang.Math.cos(horizontalAngle))
        );
        // Right vector
        Vector3f right = new Vector3f(
                (float) (java.lang.Math.sin(horizontalAngle - 3.14f / 2.0f)),
                0,
                (float) (java.lang.Math.cos(horizontalAngle - 3.14f / 2.0f))
        );

        // Up vector : perpendicular to both direction and right
        Vector3f up = right.cross(direction);

        // Move forward
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
            //System.out.println(" up ");
            position.add(direction.mul(deltaTime * speed, new Vector3f()));
        }
        // Move backward
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
            //System.out.println(" down ");
            position.sub(direction.mul(deltaTime * speed, new Vector3f()));
        }
        // Strafe right
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) {
            //System.out.println(" right ");
            position.add(right.mul(deltaTime * speed, new Vector3f()));
        }
        // Strafe left
        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) {
            //System.out.println(" left ");
            position.sub(right.mul(deltaTime * speed, new Vector3f()));
        }

        // Projection matrix : 45&deg; Field of View, 4:3 ratio, display range : 0.1 unit <-> 100 units
        Matrix4f Projection = new Matrix4f();
        Projection.perspective((float) java.lang.Math.toRadians(FoV), width / height, 0.001f, 100.0f);

        Matrix4f view = new Matrix4f();
        view.lookAt(
                position, // Camera is here
                position.add(direction, new Vector3f()), // and looks here : at the same position, plus "direction"
                up // Head is up (set to 0,-1,0 to look upside-down)
        );

        Entry<Matrix4f, Matrix4f> rtrn = new AbstractMap.SimpleEntry<>(
                Projection,
                view
        );

        return rtrn;
    }

    private boolean checkSupport() {
        boolean proceed = true;
        if (!getCapabilities().OpenGL11) {
            System.err.println("opengl 1.1 not supported");
            proceed = false;
        }
        if (!getCapabilities().OpenGL15) {
            System.err.println("opengl 1.5 not supported");
            proceed = false;
        }
        if (!getCapabilities().OpenGL20) {
            System.err.println("opengl 2.0 not supported");
            proceed = false;
        }
        if (!getCapabilities().OpenGL30) {
            System.err.println("opengl 3.0 not supported");
            proceed = false;
        }
        if (!getCapabilities().GL_ARB_shader_objects) {
            System.err.println("Shader objects not supported");
            proceed = false;
        }
        if (!getCapabilities().GL_ARB_vertex_shader) {
            System.err.println("Vertex shader not supported");
            proceed = false;
        }
        if (!getCapabilities().GL_ARB_fragment_shader) {
            System.err.println("Fragment shader not supported");
            proceed = false;
        }

        return proceed;
    }
}
