/*
 * Copyright (c) 2017 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.object.Texture;
import com.firststory.firstoracle.object.UVMap;
import com.firststory.firstoracle.object3D.BoundingBox3D;
import com.firststory.firstoracle.object3D.ObjectTransformations3D;
import com.firststory.firstoracle.object3D.Vertices3D;

/**
 * @author: n1t4chi
 */
public interface Object2D {
    
    Texture getTexture();
    
    UVMap getUvMap();
    
    Vertices2D getVertices();
    
    ObjectTransformations2D getTransformations();
    
    default BoundingBox2D getBBO(){
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations() );
    }
}
