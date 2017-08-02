/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

import static com.firststory.objects3D.Object3D.hashUVparam;
import com.firststory.objects3D.Terrain.Terrain3D;
import java.util.HashMap;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

/**
 *
 * @author n1t4chi
 */
public class CubeUtil {
    /**
     * Returns copy of array of triangles in VBO indexing that make up cube.
     * @return array of triangles
     */
    public static short[] getTrainglesCube() {
        return TRIANGLES_CUBE.clone();
    }
    /**
     * Returns copy of commonly used vertex for cubes.<br>
     * For multiple uses best to call this method once and use copy.
     * @return commonly used vertex
     */
    public static float[] getCommonCubeVertex() {
        return VERTEX_CUBE.clone();
    }
    /**
     * Do not even think about modifying this or everything will get fucked!<br>
     * Use {@link #getCommonCubeVertex()} to get copy of this array.
     */
    public static final float[] VERTEX_CUBE = {
        /*0*/ -1,-1,-1,
        /*1*/ 1,-1,-1,
        /*2*/ 1,1,-1,
        /*3*/ -1,1,-1,
        /*4*/ -1,-1,1,
        /*5*/ 1,-1,1,
        /*6*/ 1,1,1,
        /*7*/ -1,1,1
    };
    /**
     * Do not even think about modifying this or everything will get fucked!
     */
    public static final short[] TRIANGLES_CUBE;
    /**
     * Do not even think about modifying this or everything will get fucked!
     */
    public static final float[] TRIANGLES_CUBE_NO_VBO;
    static {
        int i = Object3D.getCubeVertexStart();
        int ci = i;
        short[] f = {
            //face 0
            (short)(i+1),(short)(i+0),(short)(i+3),
            (short)(i+1),(short)(i+3),(short)(i+2), 

            //face 1
            (short)(i+0),(short)(i+4),(short)(i+7),
            (short)(i+0),(short)(i+7),(short)(i+3),

            //face 2
            (short)(i+4),(short)(i+5),(short)(i+6),
            (short)(i+4),(short)(i+6),(short)(i+7),

            //face 3
            (short)(i+5),(short)(i+1),(short)(i+2),
            (short)(i+5),(short)(i+2),(short)(i+6),

            //face 4
            (short)(i+7),(short)(i+6),(short)(i+2),
            (short)(i+7),(short)(i+2),(short)(i+3),

            //face 5
            (short)(i+4),(short)(i+5),(short)(i+1),
            (short)(i+4),(short)(i+1),(short)(i+0)
        };
        TRIANGLES_CUBE = f;
        float[] g = new float[f.length*3];
        float[] ver = getCommonCubeVertex();
        for(int j=0; j<f.length;j++){
            g[j*3] = ver[ci+3*f[j]];
            g[j*3+1] = ver[ci+3*f[j]+1];
            g[j*3+2] = ver[ci+3*f[j]+2];
        }
        TRIANGLES_CUBE_NO_VBO = g;
    }
    
    
    /**
     * Returns UV map for plane object
     * @param frame Which frame to return
     * @param frames How many frames this texture represent
     * @param rows How many rows for frames are in this texture.
     * @return 
     */
    public static float[] getUVMapCube(int frame,int frames,int rows){
        if(frame <0  || frame >= frames)
            throw new IllegalArgumentException("illegal frame:"+frame+", frames:"+frames+", rows:"+rows);
        float hor = 1/8f;
        float del = 0.01f;
        float vertUp = (frame)/(float)rows+del;
        float vertDown = (frame+1)/(float)rows-del;
        float[] rtrn = {
            //face 0
            0*hor+del,vertDown, 1*hor-del,vertDown, 1*hor-del,vertUp,
            0*hor+del,vertDown, 1*hor-del,vertUp, 0*hor+del,vertUp,
                
            //face 1
            1*hor+del,vertDown, 2*hor-del,vertDown, 2*hor-del,vertUp,
            1*hor+del,vertDown, 2*hor-del,vertUp, 1*hor+del,vertUp,
                
            //face 2
            2*hor+del,vertDown, 3*hor-del,vertDown, 3*hor-del,vertUp,
            2*hor+del,vertDown, 3*hor-del,vertUp, 2*hor+del,vertUp,
                
            //face 3
            3*hor+del,vertDown, 4*hor-del,vertDown, 4*hor-del,vertUp,
            3*hor+del,vertDown, 4*hor-del,vertUp, 3*hor+del,vertUp,
                
            //face 4
            4*hor+del,vertDown, 5*hor-del,vertDown, 5*hor-del,vertUp,
            4*hor+del,vertDown, 5*hor-del,vertUp, 4*hor+del,vertUp,
                
            //face 5
            5*hor+del,vertDown, 6*hor-del,vertDown, 6*hor-del,vertUp,
            5*hor+del,vertDown, 6*hor-del,vertUp, 5*hor+del,vertUp,
        };
        
        return rtrn;
    }          
    public static Vector3fc convertArrayToSpacePosition(int x, int y, int z,Vector3ic terrainMin) {
        x +=terrainMin.x();
        y +=terrainMin.y();
        z +=terrainMin.z();
        return new Vector3f(2*x,2*y,2*z);
    }

    public static Vector3ic convertSpaceToArrayPosition(float x, float y, float z) {
        return new Vector3i((int)(x/2),(int)(y/2),(int)(z/2));
    }

    public static boolean isVisible(int x, int y, int z,Vector3ic terrainMin,Terrain3D[][][] terrain,Vector3ic terrainSize){
        return isVisibleArrayId(x-terrainMin.x(),y-terrainMin.y(),z-terrainMin.z(),terrain,terrainSize);
    }
    public static boolean isVisibleArrayId(int x, int y, int z,Terrain3D[][][] terrain,Vector3ic terrainSize){
        if( x<=0 || x>=terrainSize.x()-1 || //check extremas so that next if statement will not fail
            z<=0 || z>=terrainSize.z()-1 ||
            y >= terrainSize.y()-1
        ){
            return true;
        }else if(
            terrain[x][y+1][z] != null && //up
            terrain[x+1][y][z] != null && //side 1
            terrain[x][y][z+1] != null && //side 2
            terrain[x-1][y][z] != null && //side 3
            terrain[x][y][z-1] != null  //side 4
        ){
            return false;
        }
        return true;
    }
    private static int cubeVertexBufferID = 0;
    public static void setCubeVertexBufferID(int VertexBufferID){
        CubeUtil.cubeVertexBufferID = VertexBufferID;
    }
    public static int getCubeVertexBufferID(){
        return cubeVertexBufferID;
    }
    
    
    private final static HashMap<Long,Integer> cubeUVBufferIDs = new HashMap<>(32);
    
    
    
    public static void setCubeUVBufferID(int UVBufferID, int frame, int direction, int rows, int columns) {
        cubeUVBufferIDs.put(hashUVparam(frame, direction, rows, columns), UVBufferID);
    }

    public static int getCubeUVBufferID(int frame, int direction, int rows, int columns) {
        Integer rtrn = cubeUVBufferIDs.get(hashUVparam(frame, direction, rows, columns));
        if (rtrn == null)
            return 0;
        else
            return rtrn;
    }

    
    
}
