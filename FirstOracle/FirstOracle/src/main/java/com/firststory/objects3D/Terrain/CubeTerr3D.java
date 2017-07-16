/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.Terrain;

import com.firststory.objects3D.CubeUtil;
import static com.firststory.objects3D.CubeUtil.TRIANGLES_CUBE;
import static com.firststory.objects3D.CubeUtil.TRIANGLES_CUBE_NO_VBO;
import static com.firststory.objects3D.CubeUtil.getUVMapCube;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Texture;
import java.io.IOException;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import static com.firststory.objects3D.CubeUtil.VERTEX_ID_CUBE;
import static com.firststory.objects3D.CubeUtil.UV_ID_CUBE;
import static com.firststory.objects3D.CubeUtil.getCommonCubeVertex;

/** 
 * Class representing 3D cube terrain.<br>
 * Vertex model for cube :<br>
 *     /\y<br>
 *      |<br>
 *      3----2<br>
 *     /|   /|<br>
 *    / 0--+-1--&gt;x<br>
 *   7-+--6 /<br>
 *   |/   |/<br>
 *   4----5 <br>
 *  /<br>
 * |/z <br>
 * Centre of the cube is in (0,0,0) and X, Y and Z coordinates are within [-1,1] range.<br>
 * <br>
 * Faces for cube are: [Order of vertices indicates how texture is drawn onto face where first is bottom left and order is counterclockwise]<br>
 * 0 -&gt; back 1,0,3,2<br>
 * 1 -&gt; left 0,4,7,3<br>
 * 2 -&gt; front 4,5,6,7<br>
 * 3 -&gt; right 5,1,2,6<br>
 * 4 -&gt; up 3,2,7,6<br>
 * 5 -&gt; down 0,1,5,4  [Pointing upwards!]<br>
 * <br>
 * UV coordinates <br>
 * see order of vertex in face,<br>
 * for 0 face, image will have corresponding vertex on corners:<br>
 *2 ___ 3<br>
 * |   |<br>
 * |___|<br>
 *1     0    <br>
 * Line of texture is of format: where X is face number and nan is not used. Single face texture should be of size of power of 2<br>
 *  ___ ___ ___ ___ ___ ___ ___ ___<br>
 * | 0 | 1 | 2 | 3 | 4 | 5 |nan|nan|<br>
 * |___|___|___|___|___|___|___|___|<br>
 * Face 5 is pointing upwards [towards 4]!!!<br>
 * <br>
 * 
 * @author n1t4chi
 */
public class CubeTerr3D extends Terrain3D{
    @Override
    public double getVertexID() {
        return VERTEX_ID_CUBE;
    }
    @Override
    public double getUVID() {
        return UV_ID_CUBE;
    }
    

    public CubeTerr3D(Texture texture) {
        super(texture);
    }
    public CubeTerr3D(String path, int frameCount, int lineCount) throws IOException {
        super(path, Object3DType.CUBE, frameCount, lineCount);
    }
    
    

    @Override
    public boolean usesVBOIndexing() {
        return true;
    }

    @Override
    public short[] getTrianglesVBO() {
        return TRIANGLES_CUBE;
    }

    @Override
    public float[] getTriangles() {
        //throw new UnsupportedOperationException("Cube uses VBO for triangle representation");
        return TRIANGLES_CUBE_NO_VBO;
    }

    @Override
    public float[] getVertex() {
        return getCommonCubeVertex();
    }

    @Override
    public Object3DType getType() {
        return Object3DType.CUBE;
    }

    
    @Override
    public float[] getUV(int frameIter) {
        Texture tex = getTexture();
        int fc = tex.getFrameCount();
        int lc = tex.getLineCount();
        return getUVMapCube(frameIter,fc,lc);
    }

              
    @Override
    public Vector3fc convertArrayToSpacePosition(int x, int y, int z,Vector3ic terrainMin) {
        return CubeUtil.convertArrayToSpacePosition(x, y, z,terrainMin);
    }

    @Override
    public Vector3ic convertSpaceToArrayPosition(float x, float y, float z) {
        return CubeUtil.convertSpaceToArrayPosition(x, y, z);
    }

    @Override
    public boolean isVisibleArrayPos(int x, int y, int z, Terrain3D[][][] terrain, Vector3ic terrainSize) {
        return CubeUtil.isVisibleArrayId(x, y, z, terrain, terrainSize);
    }
    
    
    
}
