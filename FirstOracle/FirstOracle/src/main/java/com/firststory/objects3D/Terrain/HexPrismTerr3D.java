/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D.Terrain;

import com.firststory.objects3D.HexPrismUtil;
import static com.firststory.objects3D.HexPrismUtil.*;
import com.firststory.objects3D.Object3DType;
import com.firststory.objects3D.Texture;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/** 
 * Class representing 3D hexagonal Prism.<br>
 * Model is Hexagon on XZ plane which rises from -1 to 1 on Y.<br>
 * /\z <br>
 * |    0<br>
 * |   __ <br>
 * |1 /  \ 5<br>
 * |2 \__/ 4 <br>
 * |   3<br>
 * +------&gt;X.<br>
 * Centre of the plane is in (0,0,0) and X and Y coordinates are within [-1,1] while Z coordinate lies between (-sqrt(3)/2 to sqrt(3)/2).<br>
 * There are 8 faces starting from back and moving counterclockwise then top and and the end bottom:<br>
 * 0 - back<br>
 * 1 - back/left<br>
 * 2 - front/left<br>
 * 3 - front<br>
 * 4 - back/ right<br>
 * 5 - front/right<br>
 * 6 - up<br>
 * 7 - down [Pointing upwards!]<br>
 * UV Coordinates:<br>
 * Faces 0-5 are rectangles while 6&7 are hexagonal.<br>
 * Line of texture is of format:<br>
 *  ___ ___ ___ ___ ___ ___ ___ ___<br>
 * | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |<br>
 * |___|___|___|___|___|___|___|___|<br>
 *  First 0-5 fully represent face and down/left are hexagons within rectangle like in picture below:<br>
 * +----+<br>
 * |/¯¯\|<br>
 * |\__/|<br>
 * +----+<br>
 * Where sides of hexagon touch sides of bounding rectangle and centres of both figures overlap. <br>
 * Also equilateral triangles within up/down hexagons are indexed like faces with triangle with back edge as base being zero and rest are indexed counterclockwise.<br>
 * @author n1t4chi
 */
public class HexPrismTerr3D extends Terrain3D{
  
    
    
    
    /**
     * Initialises this object with given texture.
     * @param texture 
     */
    public HexPrismTerr3D(Texture texture) {
        super(texture);
    }    
    


    @Override
    public float[] getTriangles() {
        //throw new UnsupportedOperationException("Hexagonal Prism uses VBO for triangle representation");
        return TRIANGLES_HEXAGONAL_NO_VBO;
    }

    @Override
    public float[] getVertex() {
        return getCommonHexVertex();
    }

    @Override
    public Object3DType getType() {
        return Object3DType.HEXPRISM;
    }
    /**
     * Returns array containing UV coordinates.
     * @param frame which frame to return
     * @param direction not used
     * @return UV coordinates
     */
    @Override
    public float[] getUV(int frame,int direction) {
        Texture tex = getTexture();
        int fc = tex.getFrames();
        int lc = tex.getRows();
        return getUVMapHex(frame,fc,lc);
    }

    @Override
    public Vector3fc convertArrayToSpacePosition(int x, int y, int z,Vector3ic terrainMin) {
        return HexPrismUtil.convertArrayToSpacePosition(x,y,z,terrainMin);
    }

    @Override
    public Vector3ic convertSpaceToArrayPosition(float x, float y, float z) {
        return HexPrismUtil.convertSpaceToArrayPosition(x, y, z);
    }
    

    @Override
    public boolean isVisibleArrayPos(int x, int y, int z, Terrain3D[][][] terrain, Vector3ic terrainSize) {
        return HexPrismUtil.isVisibleArrayId(x, y, z, terrain, terrainSize);
    }
    
    
    @Override
    public boolean isVertexBufferLoaded() {
        return getHexPrismVertexBufferID() > 0;
    }

    @Override
    public int getVertexBufferID() {
        return getHexPrismVertexBufferID();
    }

    @Override
    public void setVertexBufferID(int VertexBufferID) {
        setHexPrismVertexBufferID(VertexBufferID);
    }


    @Override
    public boolean isUVBufferLoaded(int frame, int direction) {
        return getHexPrismUVBufferID(frame, direction, getRows(),getColumns()) >0;
    }

    @Override
    public void setUVBufferID(int UVBufferID, int frame, int direction) {
        setHexPrismUVBufferID(UVBufferID, frame, direction, getRows(), getColumns());
    }

    @Override
    public int getUVBufferID(int frame, int direction) {
        return getHexPrismUVBufferID(frame, direction, getRows(), getColumns());
    }
    
    
}
