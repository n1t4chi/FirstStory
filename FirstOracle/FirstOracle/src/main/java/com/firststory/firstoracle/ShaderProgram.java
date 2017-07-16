/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.firststory.firstoracle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.lwjgl.opengl.ARBShaderObjects;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 * ShaderProgram utility class, new instance creates ready to use program with loaded shaders.
 * @author n1t4chi
 */
public class ShaderProgram{

    private final String vertex_file_path;
    private final String fragment_file_path;
    private final int vertexShader;
    private final int fragmentShader;
    private final int program;
    private boolean ready = false;
    /**
     * Returns address to compiled program.
     * @return Program address or 0 if it was disposed.
     */
    public int getProgramID() {
        if(ready)
            return program;
        else
            return 0;
    }


    
    
    /**
     * Compiles given shader source.
     * @param type type of a shader
     * @param source shader source
     * @return shader
     */
    private int compileSource(int type,String source){
        int shader;
        if ((shader = glCreateShader(type)) == 0 ){
            throw new RuntimeException("Shader creation failed, check OpenGL support.");
        }
        glShaderSource(shader, source);
        glCompileShader(shader);
        
        int comp_stat = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (comp_stat == GL_FALSE){
            String err = glGetShaderInfoLog(shader);
            throw new RuntimeException("Shader creation failed.\nLog:"+err+"\ntype: "+shaderTypeToString(type));
        }
        return shader;
    }
    /**
     * Shader type translation for logging purposes
     * @param type shader type
     * @return 
     */
    private static String shaderTypeToString(int type){
        switch(type){
            case GL_VERTEX_SHADER:
                return "Vertex Shader";
            case GL_FRAGMENT_SHADER:
                return "Vertex Shader";
            case GL_GEOMETRY_SHADER:
                return "Geometry Shader";
            default:
                return "Other Shader";
        }
    }
    /**
     * Default constructor, compiles given shader files and creates program ready to use.
     * @param vertex_file_path
     * @param fragment_file_path
     * @throws IOException
     */
    public ShaderProgram(String vertex_file_path,String fragment_file_path) throws IOException{
        this.vertex_file_path = vertex_file_path;
        this.fragment_file_path = fragment_file_path;
	// Create the shaders

	// Read the Vertex Shader code from the file
        String VertexContent;
        try (BufferedReader br = new BufferedReader(new FileReader(vertex_file_path))){
            StringBuilder vertexSB = new StringBuilder();
            br.lines().forEach((s)->vertexSB.append(s).append('\n'));
            VertexContent = vertexSB.toString();
        }
        String FragmentContent;
        try (BufferedReader br = new BufferedReader(new FileReader(fragment_file_path))){
            StringBuilder fragmentSB = new StringBuilder();
            br.lines().forEach((s)->fragmentSB.append(s).append('\n'));
            FragmentContent = fragmentSB.toString();
        }
        
        
	// Compile Vertex Shader
        vertexShader = compileSource(GL_VERTEX_SHADER,VertexContent);      

	// Compile Fragment Shader
        fragmentShader = compileSource(GL_FRAGMENT_SHADER,FragmentContent);
        
        // Create program
        program = glCreateProgram();
        if (program == 0)
            throw new RuntimeException("Could not create program, check OpenGL support"
                +"\nvertex:"+vertex_file_path
                +"\nfragment:"+fragment_file_path
            );
        

	// Link the program
	glAttachShader(program, vertexShader);
	glAttachShader(program, fragmentShader);
        try{
            glLinkProgram(program);
            // Check the program
            String err = glGetProgramInfoLog(program);
            if (err!=null && !err.isEmpty() ){
                System.err.println("Error after program creation for shaders"
                    +"\nvertex:"+vertex_file_path
                    +"\nfragment:"+fragment_file_path
                    +"\nerror:"+err
                );
            }
        }finally{
            glDetachShader(program, vertexShader);
            glDetachShader(program, fragmentShader);

            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
        }
        ready = true;
    }   
    
    /**
     * Disposes this object resources. No operation should be performed after dispose().
     */
    public void dispose(){
        ready = false;
        glDeleteProgram(program);
    }
    


}
