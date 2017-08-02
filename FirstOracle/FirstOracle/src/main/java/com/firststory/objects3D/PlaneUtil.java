/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

import static com.firststory.objects3D.Object3D.hashUVparam;
import static com.firststory.objects3D.Object3D.getPlaneVertexStart;
import static com.firststory.objects3D.Object3D.getPlane_0IndexedVertexStart;
import java.util.HashMap;

/**
 *
 * @author n1t4chi
 */
public class PlaneUtil {
    /**
     * Returns copy of array of triangles in VBO indexing that make up plane.
     * @return array of triangles
     */
    public static short[] getTrainglesPlane() {
        return TRIANGLES_PLANE.clone();
    }
    /**
     * Returns copy of commonly used vertex for planes.<br>
     * For multiple uses best to call this method once and use copy.
     * @return commonly used vertex
     */
    public static float[] getCommonPlaneVertex() {
        return VERTEX_PLANE.clone();
    }
    /**
     * Do not even think about modifying this or everything will get fucked!<br>
     * This is no public member due to Object3D being interface.<br>
     * Use {@link #getCommonPlaneVertex()} to get copy of this array.
     */
    public static final float[] VERTEX_PLANE = {
        /*22*/ -1,-1,0,
        /*23*/ 1,-1,0,
        /*24*/ 1,1,0,
        /*25*/ -1,1,0
    };
    
    /**
     * Do not even think about modifying this or everything will get fucked!<br>
     * This is no public member due to Object3D being interface.<br>
     * Use {@link #getCommonPlaneVertex()} to get copy of this array.
     */
    public static final float[] VERTEX_PLANE_0INDEXED = {
        /*26*/ 0,0,0,
        /*27*/ 1,0,0,
        /*28*/ 1,1,0,
        /*29*/ 0,1,0
    };
    
    public static final short[] TRIANGLES_PLANE;
    public static final float[] TRIANGLES_PLANE_NO_VBO;
    public static final short[] TRIANGLES_PLANE_0INDEXED;
    public static final float[] TRIANGLES_PLANE_NO_VBO_0INDEXED;
    static {
        short i1 = getPlaneVertexStart();
        int ci1 = i1;
        short[] f1 = {
            i1,(short)(i1+1),(short)(i1+2),
            i1,(short)(i1+2),(short)(i1+3)
        };
        
        TRIANGLES_PLANE = f1;  
        float[] g1 = new float[f1.length*3];
        float[] ver1 = getCommonPlaneVertex();
        for(int j=0; j<f1.length;j++){
            g1[j*3] = ver1[ci1+3*f1[j]];
            g1[j*3+1] = ver1[ci1+3*f1[j]+1];
            g1[j*3+2] = ver1[ci1+3*f1[j]+2];
        }
        TRIANGLES_PLANE_NO_VBO = g1;    
          
        
        short i2 = getPlane_0IndexedVertexStart();
        int ci2 = i2;
        short[] f2 = {
            i2,(short)(i2+1),(short)(i2+2),
            i2,(short)(i2+2),(short)(i2+3)
        };
        
        TRIANGLES_PLANE_0INDEXED = f2;  
        float[] g2 = new float[f2.length*3];
        float[] ver2 = getCommonPlaneVertex_0Indexed();
        for(int j=0; j<f2.length;j++){
            g2[j*3] = ver2[ci2+3*f2[j]];
            g2[j*3+1] = ver2[ci2+3*f2[j]+1];
            g2[j*3+2] = ver2[ci2+3*f2[j]+2];
        }
        TRIANGLES_PLANE_NO_VBO_0INDEXED = g2;  
    };
    
    
    /**
     * Returns copy of array of triangles in VBO indexing that make up plane.
     * @return array of triangles
     */
    public static short[] getTrainglesPlane_0Indexed() {
        return TRIANGLES_PLANE_0INDEXED.clone();
    }
    /**
     * Returns copy of commonly used vertex for planes.<br>
     * For multiple uses best to call this method once and use copy.
     * @return commonly used vertex
     */
    public static float[] getCommonPlaneVertex_0Indexed() {
        return VERTEX_PLANE_0INDEXED.clone();
    }
    /**
     * Returns UV map for plane object
     * @param frame Which frame to return
     * @param direction Which direction to return
     * @param frames How many frames this texture represent
     * @param directions How many directions this texture can represent.
     * @param rows How many rows for frames are in this texture.
     * @param columns How many columns for directions are in this texture.
     * @return 
     */
    public static float[] getUVMapPlane(int frame,int direction, int frames,int directions, int rows,int columns){
        if(frame <0  || frame >= frames || direction <0  || direction >= directions )
            throw new IllegalArgumentException("Illegal frame or direction");
        float del = 0.01f;
        float vertUp = (frame)/(float)rows+del;
        float vertDown = (frame+1)/(float)rows-del;
        
        float horLeft = (direction)/(float)columns+del;
        float horRight = (direction+1)/(float)columns-del;
        
        float[] rtrn = {
            horLeft,vertDown, horRight,vertDown, horRight,vertUp,
            horLeft,vertDown, horRight,vertUp, horLeft,vertUp
        };
        return rtrn;
    }   
    
    
    
    private static int planeVertexBufferID = 0;
    public static void setPlaneVertexBufferID(int VertexBufferID){
        PlaneUtil.planeVertexBufferID = VertexBufferID;
    }
    public static int getPlaneVertexBufferID(){
        return planeVertexBufferID;
    }
    private static int plane_0IndexedVertexBufferID = 0;
    public static void setPlane_0IndexedVertexBufferID(int VertexBufferID){
        PlaneUtil.plane_0IndexedVertexBufferID = VertexBufferID;
    }
    public static int getPlane_0IndexedVertexBufferID(){
        return plane_0IndexedVertexBufferID;
    }
    
    
    private final static HashMap<Long,Integer> planeUVBufferIDs = new HashMap<>(64);
    
    public static void setPlaneUVBufferID(int UVBufferID, int frame, int direction, int rows, int columns) {
        planeUVBufferIDs.put(hashUVparam(frame, direction, rows, columns), UVBufferID);
    }

    public static int getPlaneUVBufferID(int frame, int direction, int rows, int columns) {
        Integer rtrn = planeUVBufferIDs.get(hashUVparam(frame, direction, rows, columns));
        if (rtrn == null)
            return 0;
        else
            return rtrn;
    }

    
    
    
}
