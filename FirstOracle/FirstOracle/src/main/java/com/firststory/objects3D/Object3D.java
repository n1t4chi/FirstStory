/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

import com.firststory.objects3D.Positionable.HexPrism3D;
import com.firststory.objects3D.Positionable.Cube3D;
import com.firststory.objects3D.Positionable.Plane3D;
import java.io.IOException;
import org.joml.Vector3f;
import org.joml.Vector3fc;


/**
 * Class representing 3D model, also contains some common data declarations and static utility methods.<br>
 * There are 2 methods {@link #getTrianglesVBO()} and {@link #getTriangles()}
 * which should be used depending whether the {@link #usesVBOIndexing()} returns true or false respectively.<br>
 * Classes that extend this interface should throw exception if other method is used since data type is vastly different and also the usage.
 * @author n1t4chi
 */
public abstract class Object3D {
     /**
     * Returns copy of commonly used vertex for VBO indexing.<br>
     * For multiple uses best to call this method once and use copy.
     * @return commonly used vertex
     */
    public static float[] getCommonVertex() {
        return VERTEX.clone();
    }
    
    /**
     * Returns first index of vertex in VBO indexing for cube.
     * @return index
     */
    public static short getCubeVertexStart() {
        return VERTEX_INDEX_START_CUBE;
    }

    /**
     * Returns first index of vertex in VBO indexing for hexagonal prism.
     * @return index
     */
    public static short getHexVertexStart() {
        return VERTEX_INDEX_START_HEX;
    }

    /**
     * Returns first index of vertex in VBO indexing for plane.
     * @return index
     */
    public static short getPlaneVertexStart() {
        return VERTEX_INDEX_START_PLANE;
    }
    
    /**
     * Array containing all vertex for VBO
     */
    private static final float[] VERTEX;
    private static final short VERTEX_INDEX_START_CUBE;
    private static final short VERTEX_INDEX_START_PLANE;
    private static final short VERTEX_INDEX_START_HEX;
    static {
        float[] plane = PlaneUtil.getCommonPlaneVertex();
        float[] cube = CubeUtil.getCommonCubeVertex();
        float[] hex = HexPrismUtil.getCommonHexVertex();
        float[] f = new float[plane.length + cube.length + hex.length];
        short i=0;
        VERTEX_INDEX_START_CUBE = (short) (i);
        for(float v : cube){
            f[i++] = v; 
        }
        VERTEX_INDEX_START_PLANE = (short) (i);
        for(float v : plane){
            f[i++] = v; 
        }
        VERTEX_INDEX_START_HEX = (short) (i);
        for(float v : hex){
            f[i++] = v; 
        }
        VERTEX = f;
    };
    
    private final Vector3f scale = new Vector3f(1,1,1);
    private final Texture texture;
    

    public Object3D(String path,Object3DType type,int frameCount, int lineCount ) throws IOException {
        this.texture = new Texture(path, frameCount, lineCount);
        if(texture == null)
            throw new NullPointerException("Texture cannot be null");
    }
    public Object3D(Texture texture) {
        if(texture == null)
            throw new NullPointerException("Texture cannot be null");
        this.texture = texture;
    }
    
    
    /**
     * Returns texture
     * @return texture
     */
    public Texture getTexture() {
        return texture;
    }
  
    
    /**
     * Changes scale of this object.<br>
     * Remember that scaling Y will result in base of the object being higher if lower points are not 0 on Y dimension.
     * @param scale new scale
     */
    public void setScale(Vector3fc scale) {
        this.scale.set(scale);
    }
    /**
     * Changes scale of this object.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public void setScale(float x,float y,float z) {
        this.scale.set(x,y,z);
    }
    /**
     * Changes X scale of this object.
     * @param x X coordinate
     */
    public void setScaleX(float x) {
        this.scale.set(x,this.scale.y,this.scale.z);
    }
    /**
     * Changes Y scale of this object.<br>
     * Remember that scaling Y will result in base of the object being higher if lower points are not 0 on Y dimension.
     * @param y Y coordinate
     */
    public void setScaleY(float y) {
        this.scale.set(this.scale.x,y,this.scale.z);
    }
    /**
     * Changes Z scale of this object.
     * @param z Z coordinate
     */
    public void setScaleZ(float z) {
        this.scale.set(this.scale.x,this.scale.y,z);
    }
    /**
     * Returns immutable scale vector of this object.
     * @return scale
     */
    public Vector3fc getScale(){
        return scale.toImmutable();
    }
    /**
     * Returns mutable scale vector of this object. Use with caution!
     * @return mutable scale
     */
    public Vector3f getScaleMutable(){
        return scale;
    }
    
    
    
    /**
     * Returns whether the object uses common VBO indexing or not which should indicate how the drawing should be handled.
     * @return whether the VBO indexing is used
     */
    public abstract boolean usesVBOIndexing();
    /**
     * Returns array of triangle representation of this object within VBO indexing.<br>
     * Classes not supporting this should just throw unsupported exception.
     * @return array of triangles
     * @throws UnsupportedClassVersionError if VBO is not supported
     * @see #usesVBOIndexing() 
     */
    public abstract short[] getTrianglesVBO();
    /**
     * Returns array of triangle representation of this object.<br>
     * Classes that use VBP should throw unsupported exception.
     * @return array of triangles
     * @throws UnsupportedClassVersionError if VBO is supported
     * @see #usesVBOIndexing() 
     */
    public abstract float[] getTriangles();
    /**
     * Returns array of vertex representation of this object.
     * @return array or vertices
     */
    public abstract float[] getVertex();
    
    public abstract Object3DType getType();
   
    
    /*
    public class UVInfo{
        final int frameCount;
        final int LineCount;
        final int frameIter;
        final double ID;
        public UVInfo(int frameCount, int LineCount, int frameIter) {
            ID = uvID();
            this.frameCount = frameCount;
            this.LineCount = LineCount;
            this.frameIter = frameIter;
        }
    }
    /* *
     * Returns identification entry about UV coordinates which allow to check whether the objects are the same.
     * @param frameIter iteration
     * @return 
     * /
    public UVInfo getUVMapInfo(int frameIter){
        Texture tex = getTexture();
        int fc = tex.getFrameCount();
        int lc = tex.getLineCount();
        return new UVInfo(fc,lc,frameIter);
    }
    public class VertexInfo{
        final int size;
        final double ID;
        public VertexInfo(int size) {
            this.ID = vertexID();
            this.size = size;
        }
    }
    
    /* *
     * Returns identification entry about UV coordinates which allow to check whether the objects are the same.
     * @param frameIter iteration
     * @return 
     * /
    public VertexInfo getVertexInfo(int frameIter){
        Texture tex = getTexture();
        return new VertexInfo(VERTEX.length);
    }*/
    /**
     * Returns array containing UV coordinates.
     * @param frameIter which frame iteration to return
     * @return UV coordinates
     */
    public abstract float[] getUV(int frameIter);
    /**
     * Return uniform ID of vertex Array for this object so multiple objects having same arrays could be matched using this function.<br>
     * Cannot be 0!!!<br>
     * Best if returned value is constant for object and returned immediately.
     * @return vertex array ID
     */
    public abstract double getVertexID();
    /**
     * Return uniform ID of UV Array for this object so multiple objects having same UV mapping regardless of current line of texture could be matched using this function.<br>
     * Cannot be 0!!!<br>
     * Best if returned value is constant for object and returned immediately.
     * @return vertex array ID
     */
    public abstract double getUVID();
    
}
