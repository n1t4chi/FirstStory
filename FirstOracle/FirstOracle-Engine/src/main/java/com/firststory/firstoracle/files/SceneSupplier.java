/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

/**
 * @author n1t4chi
 */
public interface SceneSupplier< Parameter, Scene > {
    
    Scene create( Parameter size, Parameter terrainShift );
}