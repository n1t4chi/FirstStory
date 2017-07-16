/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

import static com.firststory.objects3D.Object3D.getPlaneVertexStart;

/**
 *
 * @author n1t4chi
 */
public class PlaneUtil {
    public static final double VERTEX_ID_PLANE = Math.pow(666, 12);
    public static final double UV_ID_PLANE = Math.pow(666, 12);
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
    
    public static final short[] TRIANGLES_PLANE;
    public static final float[] TRIANGLES_PLANE_NO_VBO;
    static {
        short i = getPlaneVertexStart();
        int ci = i;
        short[] f = {
            i,(short)(i+1),(short)(i+2),
            i,(short)(i+2),(short)(i+3)
        };
        
        TRIANGLES_PLANE = f;  
        float[] g = new float[f.length*3];
        float[] ver = getCommonPlaneVertex();
        for(int j=0; j<f.length;j++){
            g[j*3] = ver[ci+3*f[j]];
            g[j*3+1] = ver[ci+3*f[j]+1];
            g[j*3+2] = ver[ci+3*f[j]+2];
        }
        TRIANGLES_PLANE_NO_VBO = g;      
    };
    
    
    public static float[] getUVMapPlane(int frameIter,int frameCount,int lineCount){
        if(frameIter <0  || frameIter >= frameCount)
            throw new IllegalArgumentException("frameCount cannot be less than 1");
        float del = 0.001f;
        float vertUp = (frameIter)/(float)lineCount+del;
        float vertDown = (frameIter+1)/(float)lineCount-del;
        float[] rtrn = {
            0+del,vertDown, 1-del,vertDown, 1-del,vertUp,
            0+del,vertDown, 1-del,vertUp, 0+del,vertUp
        };
        return rtrn;
    }   
}
