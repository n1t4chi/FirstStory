/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.shader;

import java.io.IOException;

/**
 * @author n1t4chi
 */
public interface ShaderProgram {
    
    void useProgram();
    
    void compile() throws IOException;
    
    void dispose();
}
