/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.files;

import com.firststory.firstoracle.scene.RegistrableScene2D;
import com.firststory.firstoracle.scene.RegistrableScene3D;

/**
 * @author n1t4chi
 */
public class ScenePair< S2D extends RegistrableScene2D, S3D extends RegistrableScene3D > {
    
    private final S2D scene2D;
    private final S3D scene3D;
    
    public ScenePair( S2D scene2D, S3D scene3D ) {
        this.scene2D = scene2D;
        this.scene3D = scene3D;
    }
    
    public S2D getScene2D() {
        return scene2D;
    }
    
    public S3D getScene3D() {
        return scene3D;
    }
}
