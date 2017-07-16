/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;

import com.firststory.objects3D.Terrain.Terrain3D;
import static com.firststory.objects3D.Object3D.getHexVertexStart;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;


/**
 *
 * @author n1t4chi
 */
public class HexPrismUtil {
    public static final double VERTEX_ID_HEX = Math.pow(1337, 16);
    public static final double UV_ID_HEX = Math.pow(1337, 16);
    /**
     * Value of sqrt(3)/2; Double version.
     */
    public static final double SQRT3_DIV2_D = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077;
    //public static final double SQRT3_DIV2_D = java.lang.Math.pow(3/4, 0.5f);
    /**
     * Value of sqrt(3)/2.
     */
    public static final float SQRT3_DIV2 = 0.8660254037844386467637231707529361834714026269051903140279034897259665084544000185405730933786242878378130707077f;
    //public static final float SQRT3_DIV2 = (float) java.lang.Math.pow(3/4, 0.5f);

    /**
     * Returns copy of array of triangles in VBO indexing that make up hexagonal prism.
     * @return array of triangles
     */
    public static float[] getCommonHexVertex() {
        return VERTEX_HEXAGONAL_PRISM.clone();
    }

    /**
     * Returns copy of commonly used vertex for hexagonal prisms.<br>
     * For multiple uses best to call this method once and use copy.
     * @return commonly used vertex
     */
    public static short[] getTrianglesHex() {
        return TRIANGLES_HEXAGONAL_PRISM.clone();
    }
    
    /**
     * Do not even think about modifying this or everything will get fucked!<br>
     */
    public static final float[] VERTEX_HEXAGONAL_PRISM = {
        /*0*/ 0,-1,0,
            
        /*1*/ 1,-1,0,   
        /*2*/ 0.5f,-1,-SQRT3_DIV2,
        /*3*/ -0.5f,-1,-SQRT3_DIV2,
        /*4*/ -1,-1,0,
        /*5*/ -0.5f,-1,SQRT3_DIV2,
        /*6*/ 0.5f,-1,SQRT3_DIV2,

        /*7*/ 0,1,0,
            
        /*8*/ 1,1,0,
        /*9*/ 0.5f,1,-SQRT3_DIV2,
        /*10*/ -0.5f,1,-SQRT3_DIV2,
        /*11*/ -1,1,0,
        /*12*/ -0.5f,1,SQRT3_DIV2,
        /*13*/ 0.5f,1,SQRT3_DIV2,
    };
    /**
     * Do not even think about modifying this or everything will get fucked!
     */
    public static final short[] TRIANGLES_HEXAGONAL_PRISM;
    /**
     * Do not even think about modifying this or everything will get fucked!
     */
    public static final float[] TRIANGLES_HEXAGONAL_NO_VBO;
    
    static {
        short i = getHexVertexStart();
        int ci = i;
        short[] f = {
            //face 0
            (short)(i+2),(short)(i+3),(short)(i+10),
            (short)(i+2),(short)(i+10),(short)(i+9),
            //face 1
            (short)(i+3),(short)(i+4),(short)(i+11),
            (short)(i+3),(short)(i+11),(short)(i+10),
            //face 2
            (short)(i+4),(short)(i+5),(short)(i+12),
            (short)(i+4),(short)(i+12),(short)(i+11),
            //face 3
            (short)(i+5),(short)(i+6),(short)(i+13),
            (short)(i+5),(short)(i+13),(short)(i+12),
            //face 4
            (short)(i+6),(short)(i+1),(short)(i+8),
            (short)(i+6),(short)(i+8),(short)(i+13),
            //face 5
            (short)(i+1),(short)(i+2),(short)(i+9),
            (short)(i+1),(short)(i+9),(short)(i+8),

            //face 6 
            (short)(i+7),(short)(i+9),(short)(i+10),
            (short)(i+7),(short)(i+10),(short)(i+11),
            (short)(i+7),(short)(i+11),(short)(i+12),
            (short)(i+7),(short)(i+12),(short)(i+13),
            (short)(i+7),(short)(i+13),(short)(i+8),
            (short)(i+7),(short)(i+8),(short)(i+9),

            //face 7
            (short)(i+0),(short)(i+2),(short)(i+3),
            (short)(i+0),(short)(i+3),(short)(i+4),
            (short)(i+0),(short)(i+4),(short)(i+5),
            (short)(i+0),(short)(i+5),(short)(i+6),
            (short)(i+0),(short)(i+6),(short)(i+1),
            (short)(i+0),(short)(i+1),(short)(i+2)
        };
        TRIANGLES_HEXAGONAL_PRISM = f;
        float[] g = new float[f.length*3];
        float[] ver = getCommonHexVertex();
        for(int j=0; j<f.length;j++){
            g[j*3] = ver[ci+3*f[j]];
            g[j*3+1] = ver[ci+3*f[j]+1];
            g[j*3+2] = ver[ci+3*f[j]+2];
        }
        TRIANGLES_HEXAGONAL_NO_VBO = g;  
    }
    public static float[] getUVMapHex(int frameIter,int frameCount,int lineCount){
        if(frameIter <0  || frameIter >= frameCount)
            throw new IllegalArgumentException("frameCount cannot be less than 1");
        float hor = 1/8f;
        //lower a bit UV map so pixels from other face will not be taken into consideration.
        float del = 0.01f;
        float vertUp = (frameIter)/(float)lineCount+del;
        float vertDown = (frameIter+1)/(float)lineCount-del;
        float vertMiddle = (frameIter+0.5f)/(float)lineCount;
        float vertMidUp = (frameIter+0.5f-SQRT3_DIV2/2)/lineCount+del;
        float vertMidDown = (frameIter+0.5f+SQRT3_DIV2/2)/lineCount-del;
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
            
            //face 6
            6.5f*hor,vertMiddle, 6.75f*hor-del,vertUp,   6.25f*hor+del,vertUp,
            6.5f*hor,vertMiddle, 6.25f*hor+del,vertUp,   6*hor+del,vertMiddle,
            6.5f*hor,vertMiddle, 6*hor+del,vertMiddle,   6.25f*hor+del,vertDown,
            6.5f*hor,vertMiddle, 6.25f*hor+del,vertDown, 6.75f*hor-del,vertDown,
            6.5f*hor,vertMiddle, 6.75f*hor-del,vertDown, 7*hor-del,vertMiddle,
            6.5f*hor,vertMiddle, 7*hor-del,vertMiddle,   6.75f*hor-del,vertUp,
            
            //face 7
            7.5f*hor,vertMiddle, 7.75f*hor-del,vertUp,   7.25f*hor+del,vertUp,
            7.5f*hor,vertMiddle, 7.25f*hor+del,vertUp,   7*hor+del,vertMiddle,
            7.5f*hor,vertMiddle, 7*hor+del,vertMiddle,   7.25f*hor+del,vertDown,
            7.5f*hor,vertMiddle, 7.25f*hor+del,vertDown, 7.75f*hor-del,vertDown,
            7.5f*hor,vertMiddle, 7.75f*hor-del,vertDown, 8*hor-del,vertMiddle,
            7.5f*hor,vertMiddle, 8*hor-del,vertMiddle,   7.75f*hor-del,vertUp
        };
        return rtrn;
    }
    
    
    public static Vector3fc convertArrayToSpacePosition(int x, int y, int z,Vector3ic terrainMin) {
        x -=terrainMin.x();
        y -=terrainMin.y();
        z -=terrainMin.z();
        return new Vector3f(x*1.5f/*spacing: 0.75 but size dim is 2*/, 2 * y, (2*z+Math.abs(x)%2)*SQRT3_DIV2);
    }

    public static Vector3ic convertSpaceToArrayPosition(float x, float y, float z) {
        return new Vector3i((int)(x/1.5f),(int)(y/2), (int) (z/2-((int)x)%2));
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
                
            terrain[x][y][z-1] != null && //left
            terrain[x][y][z+1] != null && //right
                 
            terrain[x+1][y][z-x%2] != null && //side 3
            terrain[x+1][y][z+1-x%2] != null && //side 4
            terrain[x-1][y][z-x%2] != null && //side 5
            terrain[x-1][y][z+1-x%2] != null  //side 6
        ){
            return false;
        }
        return true;
    }
    
}
