/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.objects3D;


import com.firststory.firstoracle.Utilities;
import java.io.IOException;
import java.nio.*;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

/**
 * Single texture object. Colours are stored in RGBA format. <br>
 * Example: Texture with 3 faces and 3 frames requires texture of 4 faces and 4 lines<br>
 *  <br>
 *  ___ ___ ___ ___<br>
 * | 00| 10| 20|   |<br>
 * |___|___|___|___|<br>
 * | 01| 11| 21|   |<br>
 * |___|___|___|___|<br>
 * | 01| 11| 21|   |<br>
 * |___|___|___|___|<br>
 * |   |   |   |   |<br>
 * |___|___|___|___|<br>
 * XY - mean X face in Y frame
 * 
 * <br>
 * Used are textures for cube or plane.<br>
 * Plane uses only 1 face per line while cube uses first 6 faces out of 8 in each line<br>
 * Line count must be of power of 2.
 * @author n1t4chi
 */
public class Texture {
    private final int width;
    private final int height;
    private final ByteBuffer texture;
    private final String name;
    private final int lineCount;
    private final int frameCount;
    
    /**
     * Creates object containing texture data.
     * @param path path to texture resource
     * @param frameCount how many frames are held in texture [must be lesser or equal lineCount]
     * @param lineCount how many lines are held in texture
     * @throws IOException 
     */
    public Texture(String path, int frameCount, int lineCount) throws IOException {
        if(path == null || path.isEmpty() || frameCount<1 || lineCount<1 || frameCount>lineCount)
            throw new IllegalArgumentException("Illegal arguments for Texture.\nPath:"+path+", FrameCount:"+frameCount+", LineCount:"+lineCount+".");
        ByteBuffer bf = Utilities.readBinaryResource(path);
        name = path;
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);
        
        texture = STBImage.stbi_load_from_memory(bf, w, h, c, 4);
        if(texture == null){
            throw new RuntimeException("Cannot load image:"+path);
        }
        width = w.get(0);
        height = h.get(0);
        this.frameCount = frameCount;
        this.lineCount = lineCount;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getLineCount() {
        return lineCount;
    }
    

    public int getWidth() {
        return width;
    }
    public ByteBuffer getTexture() {
        return texture;
    }
    public int getHeight() {
        return height;
    }
    public String getName() {
        return name;
    }
    
    
    
}
