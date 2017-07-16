/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import com.firststory.objects3D.Texture;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import static org.lwjgl.BufferUtils.createByteBuffer;

/**
 * Class containing some utility methods for 
 * @author n1t4chi
 */
public class Utilities {
    /**
     * Reads given text file and returns in string format.
     * @param path Path to file
     * @return Text from that wile
     * @throws IOException 
     */
    public static String readTextResource(String path) throws IOException{
        Path p = Paths.get(path);
        Reader r;
        if(Files.isReadable(p)){
            r = new FileReader(path);
        }else{
            r = new InputStreamReader(Texture.class.getClassLoader().getResourceAsStream(path));  
        }
        try (BufferedReader br = new BufferedReader(r)){
            StringBuilder vertexSB = new StringBuilder();
            br.lines().forEach((s)->vertexSB.append(s).append('\n'));
            
            if(vertexSB.length()>0)
                return vertexSB.substring(0, vertexSB.length()-1);
            else
                return vertexSB.toString();
        }
        
    }
    
    public static ByteBuffer readBinaryResource(String resource) throws IOException{
        System.err.println("here8.0.1.1");
        Path p = Paths.get(resource);
        ByteBuffer bf;
        System.err.println("here8.0.1.2");
        if(Files.isReadable(p)){
            System.err.println("here8.0.1.2.1.0");
            try(SeekableByteChannel sbc = Files.newByteChannel(p,StandardOpenOption.READ)){
                System.err.println("here8.0.1.2.1.1");
                bf = createByteBuffer((int) (sbc.size()-1));
                System.err.println("here8.0.1.2.1.2");
                int read;
                while( (read = sbc.read(bf)) != -1 && sbc.position()+1 != sbc.size()){
                    System.err.println("here8.0.1.2.1.2 loop read:"+read+" pos:"+sbc.position()+" size:"+sbc.size());
                    
                }
                System.err.println("here8.0.1.2.1.3");
            }
        }else{
            System.err.println("here8.0.1.2.2.0"+resource);
            try(InputStream io = Texture.class.getClassLoader().getResourceAsStream(resource);
                ReadableByteChannel rbc = Channels.newChannel(io);
            ){
                System.err.println("here8.0.1.2.2.1");
                bf = createByteBuffer(16384);
                System.err.println("here8.0.1.2.2.2");
                while(rbc.read(bf)!= -1 ){
                    if(bf.remaining() == 0){
                        System.err.println("here8.0.1.2.2.3");
                        ByteBuffer nbf = createByteBuffer(bf.capacity()*2);
                        bf.flip();
                        nbf.put(nbf);
                        bf = nbf;
                    }
                }
                System.err.println("here8.0.1.2.2.4");
            }
        }
        bf.flip();
        return bf;
    }
}
