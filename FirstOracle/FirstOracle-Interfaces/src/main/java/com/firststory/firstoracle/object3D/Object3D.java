/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UVMap;
import com.firststory.firstoracle.object.Vertices;

/**
 * @author: n1t4chi
 */
public interface Object3D {
    
    Texture getTexture();
    
    UVMap getUvMap();
    
    Vertices3D getVertices();
    
    ObjectTransformations3D getTransformations();
    
    default BoundingBox3D getBBO(){
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
}
