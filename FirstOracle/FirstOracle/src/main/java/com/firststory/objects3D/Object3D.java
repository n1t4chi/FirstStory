/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;


/**
 * Class representing 3D model, also contains some common data declarations and static utility methods.<br>
 * There are 2 methods {@link #getTrianglesVBO()} and {@link #getTriangles()}
 * which should be used depending whether the {@link #usesVBOIndexing()} returns true or false respectively.<br>
 * Classes that extend this interface should throw exception if other method is used since data type is vastly different and also the usage.
 * If this object vertex or UV mapping could be shared then respecting function {@link #getVertexID()} and {@link #getUVID()} should be implemented.
 * 
 * @author n1t4chi
 */
public abstract class Object3D implements Closeable{
    
    /**
     * Function used to create simple 64bit long Hash for UV specific data within same shape type.
     * @param frame
     * @param direction
     * @param rows
     * @param columns
     * @return Hash
     */
    public static long hashUVparam(int frame, int direction, int rows, int columns){
        return ((((((long)frame << 16) + direction) << 16) + rows ) << 16) + columns ;
    }
    
    /**
     * Loads given array to buffer under given ID. Buffer is bound after this method ends.
     * @param bufferID
     * @param bufferData 
     */
    public static void loadArrayBuffer(int bufferID,float[] bufferData){
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
        //GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_STATIC_DRAW);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bufferData, GL15.GL_DYNAMIC_DRAW);
    }     
    /**
     * Creates new buffer.
     * @return 
     */
    public static int createBuffer(){
        return GL15.glGenBuffers();
    }
     /**
     * Returns copy of commonly used vertex for shared indexing.<br>
     * For multiple uses best to call this method once and use copy.
     * @return commonly used vertex
     */
    public static float[] getCommonVertex() {
        return VERTEX.clone();
    }
    
    /**
     * Returns first index of vertex in shared indexing for cube.
     * @return index
     */
    public static short getCubeVertexStart() {
        return VERTEX_INDEX_START_CUBE;
    }

    /**
     * Returns first index of vertex in shared indexing for hexagonal prism.
     * @return index
     */
    public static short getHexVertexStart() {
        return VERTEX_INDEX_START_HEX;
    }

    /**
     * Returns first index of vertex in shared indexing for plane.
     * @return index
     */
    public static short getPlaneVertexStart() {
        return VERTEX_INDEX_START_PLANE;
    }
    /**
     * Returns first index of vertex in shared indexing for plane.
     * @return index
     */
    public static short getPlane_0IndexedVertexStart() {
        return VERTEX_INDEX_START_PLANE_0INDEXED;
    }
    
    /**
     * Array containing all shared vertex.
     */
    private static final float[] VERTEX;
    private static final short VERTEX_INDEX_START_CUBE;
    private static final short VERTEX_INDEX_START_PLANE;
    private static final short VERTEX_INDEX_START_PLANE_0INDEXED;
    private static final short VERTEX_INDEX_START_HEX;
    static {
        float[] plane = PlaneUtil.getCommonPlaneVertex();
        float[] plane0i = PlaneUtil.getCommonPlaneVertex_0Indexed();
        float[] cube = CubeUtil.getCommonCubeVertex();
        float[] hex = HexPrismUtil.getCommonHexVertex();
        float[] f = new float[plane.length + plane0i.length + cube.length + hex.length];
        short i=0;
        VERTEX_INDEX_START_CUBE = (short) (i);
        for(float v : cube){
            f[i++] = v; 
        }
        VERTEX_INDEX_START_PLANE = (short) (i);
        for(float v : plane){
            f[i++] = v; 
        }
        VERTEX_INDEX_START_PLANE_0INDEXED = (short) (i);
        for(float v : plane0i){
            f[i++] = v; 
        }
        VERTEX_INDEX_START_HEX = (short) (i);
        for(float v : hex){
            f[i++] = v; 
        }
        VERTEX = f;
    };
    private Texture texture;
    private int frames;
    private int directions;
    private int rows;
    private int columns;

    /**
     * Returns current column count of texture
     * @return current column count of texture
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Returns current direction count of texture
     * @return current direction count of texture
     */
    public int getDirections() {
        return directions;
    }

    /**
     * Returns current frame count of texture
     * @return current frame count of texture
     */
    public int getFrames() {
        return frames;
    }

    /**
     * Returns current row count of texture
     * @return current row count of texture
     */
    public int getRows() {
        return rows;
    }
    
    
    
    
    /**
     * Initialises this object with given texture.
     * @param texture 
     * @throws NullPointerException if texture is null!
     */
    public Object3D(Texture texture) {
        setTexture(texture);
    }
    
    /**
     * Returns texture
     * @return texture
     */
    public Texture getTexture() {
        return texture;
    }
    /**
     * Changes texture of this object to newly given one.
     * @param texture 
     * @throws NullPointerException if texture is null!
     */
    public final void setTexture(Texture texture) {
        if(texture == null)
            throw new NullPointerException("Texture cannot be null!");
        this.texture = texture;
        this.frames = texture.getFrames();
        this.directions = texture.getDirections();
        this.rows = texture.getRows();
        this.columns = texture.getColumns();
    }
    
    
    
    /**
     * Returns scale vector of this object.
     * @return scale
     */
    public abstract Vector3fc getScale();
    /**
     * Returns rotation angles of this object.
     * @return rotation
     */
    public abstract Vector3fc getRotation();
    
    
    /* *
     * Returns whether the object uses common VBO indexing or not which should indicate how the drawing should be handled.
     * @return whether the VBO indexing is used
     */
    //public abstract boolean usesVBOIndexing();
    /* *
     * Returns array of triangle representation of this object within VBO indexing.<br>
     * Classes not supporting this should just throw unsupported exception.
     * @return array of triangles
     * @throws UnsupportedClassVersionError if VBO is not supported
     * @see #usesVBOIndexing() 
     */
   // public abstract short[] getTrianglesVBO();
    
    
    
    /*
     * Classes that use VBO should throw unsupported exception.
     //* @throws UnsupportedClassVersionError if VBO is supported
     * @see #usesVBOIndexing() 
    
     */
    /**
     * Returns array of triangle representation of this object.<br>
     * @return array of triangles
     */
    public abstract float[] getTriangles();
    
    /**
     * Returns amount of vertex used in triangles including duplicates.
     * @return 
     */
    public int getVertexCount(){
        return getTriangles().length/3;
    }
    
    /**
     * Returns array of vertex representation of this object.<br>
     * Used for VBO so classes that DO NOT use VBO should throw unsupported exception.
     * @return array or vertices
     */
    public abstract float[] getVertex();
    
    
    public abstract Object3DType getType();
   
    /**
     * Returns array containing UV coordinates.
     * @param frame which frame to return
     * @param direction which direction to return
     * @return UV coordinates
     */
    public abstract float[] getUV(int frame,int direction);
    
    

    
    /**
     * This method check whether the vertex buffer was loaded using {@link #isVertexBufferLoaded() }
     * and if it is not then creates new buffer and loads data into GPU with {@link #loadVertexBuffer() }
     * otherwise it just binds buffer under {@link #getVertexBufferID() }.<br>
     * Only this method should be called for drawing as it should guarantee least possible data transfer to GPU.
     */
    public void bindVertexBuffer(){
        if(isVertexBufferLoaded()){
            //System.err.println("Binding vertex buffer at "+getVertexBufferID());
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVertexBufferID());
        }else{
            loadVertexBuffer();
        }
    }     
    /**
     * Releases vertex buffer is one was created before.<br>
     */
    public void releaseVertexBuffer(){
        if(isVertexBufferLoaded()){
            GL15.glDeleteBuffers(getVertexBufferID());
            //System.err.println("Deleting vertex buffer at "+getVertexBufferID());
            setVertexBufferID(0);
        }
    }
    /**
     * Creates new vertex buffer and passes data to GPU.<br>
     * There is no obligation for this method to release resources if previously data was loaded but by default it does so.
     */
    public void loadVertexBuffer(){
        releaseVertexBuffer();
        int ID = createBuffer();
        //System.err.println("Loading vertex buffer under "+ID+" with array:"+Arrays.toString(getTriangles()));
        loadArrayBuffer(ID, getTriangles());
        //GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        setVertexBufferID(ID);
    }
    /**
     * Checks whether the vertex buffer is loaded and ready to be bound and used to draw this object.
     * It should be done in constant time as fast as possible.
     * @return true if it is, false otherwise
     */
    public abstract boolean isVertexBufferLoaded();
    /**
     * Returns loaded vertex buffer ID.<br>
     * Should return non positive value is none was loaded.<br>
     * It should be done in constant time as fast as possible.
     * @return vertex buffer ID
     */
    public abstract int getVertexBufferID();
    /**
     * Sets newly created VertexBufferID for later use.
     * @param VertexBufferID new vertex buffer ID
     */
    public abstract void setVertexBufferID(int VertexBufferID);
    
    
    
    
    /**
     * This method check whether the UV buffer was loaded using {@link #isUVBufferLoaded(int, int) }
     * and if it is not then creates new buffer and loads data into GPU with {@link #loadUVBuffer(int, int) }
     * otherwise it just binds buffer under {@link #getUVBufferID(int, int) }.<br>
     * Only this method should be called for drawing as it should guarantee least possible data transfer to GPU.
     * @param frame
     * @param direction 
     */
    public void bindUVBuffer(int frame,int direction){
        if(isUVBufferLoaded(frame,direction)){
            //System.err.println("Binding UV buffer at "+getVertexBufferID());
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getUVBufferID(frame, direction));
        }else{
            loadUVBuffer(frame,direction);
            //GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
        }
    }
    
    /**
     * Creates new vertex buffer and passes data to GPU.<br>
     * There is no obligation for this method to release resources if previously data was loaded but by default it does so.
     * @param frame
     * @param direction 
     */
    public void loadUVBuffer(int frame,int direction){
        releaseUVBuffer(frame,direction);
        int ID = createBuffer();
        setUVBufferID(ID,frame,direction);
        //System.err.println("Loading UV buffer under "+ID+" with array:"+Arrays.toString(getUV(frame, direction)));
        loadArrayBuffer(ID, getUV(frame, direction));
    }
    /**
     * Releases UV buffer for given frame and direction if it's loaded.
     * @param frame
     * @param direction
     */
    public void releaseUVBuffer(int frame,int direction){
        if(isUVBufferLoaded(frame, direction)){
            GL15.glDeleteBuffers(getUVBufferID(frame,direction));
            //System.err.println("Deleting UV buffer at "+getUVBufferID(frame,direction));
            setUVBufferID(0, frame, direction);
        }
    }
    /**
     * Releases all UV buffers associated with this object by calling {@link #releaseUVBuffer(int, int) } on each current direction/frame combination.<br>
     * For objects using built-in shapes this method should be called while disposing application
     * unless they have unique row/column count compared to other objects then 
     * using this method should make no noticeable impact on other objects.
     */
    public void releaseAllUVBuffers(){
        for(int dir=1;dir<getDirections();dir++){  
            for(int fr=1;fr<getFrames();fr++){
                releaseUVBuffer(fr, dir);
            }
        }
    }
    
    /**
     * Checks whether the UV buffer for given frame/direction is loaded and ready to be bound and used to draw this object.<br>
     * It should be done in constant time as fast as possible,
     * for example direct array indexing using this function parameters and simple number check.
     * @param frame
     * @param direction
     * @return true if it is, false otherwise
     */
    public abstract boolean isUVBufferLoaded(int frame,int direction);
    /**
     * Sets newly created UVBufferID for later use.
     * @param UVBufferID
     * @param frame
     * @param direction 
     */
    public abstract void setUVBufferID(int UVBufferID,int frame,int direction);
    /**
     * Checks whether the UV buffer for given frame/direction is loaded and ready to be bound and used to draw this object.<br>
     * It should be done in constant time as fast as possible,
     * for example direct array indexing using this function parameters.
     * @param frame
     * @param direction
     * @return UV buffer ID
     */
    public abstract int getUVBufferID(int frame,int direction);
    
    
    /**
     * This method calls {@link #loadVertexBuffer()} and {@link #releaseAllUVBuffers()}
     * so all resources associated with this object (EXCEPT TEXTURE DATA!!!) will be released.
     */
    @Override
    public void close(){
        loadVertexBuffer();
        releaseAllUVBuffers();
    }
    
    /* *
     * Return uniform ID of vertex Array for this object so multiple objects having same arrays could be matched using this function.<br>
     * Cannot be 0 if array could be shared!!! Also should be outside range [0,1] which is reserved for build-in objects.<br>
     * Best if returned value is constant for object and returned immediately.
     * @return vertex array ID
     */
   /* public double getVertexID(){
        return 0;
    }*/
    /* *
     * Return uniform ID of UV Array for this object so multiple objects having same UV mapping regardless of current line of texture could be matched using this function.<br>
     * Cannot be 0 if mapping should be shared!!! Also should be outside range [0,1] which is reserved for build-in objects.<br>
     * Best if returned value is constant for object and returned immediately.
     * @return UV array ID
     */
  /*  public double getUVID(){
        return 0;
    }*/
    
}





