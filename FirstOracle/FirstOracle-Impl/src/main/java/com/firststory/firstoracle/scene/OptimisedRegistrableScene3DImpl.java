/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.scene;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.Terrain3D;

/**
 * @author n1t4chi
 */
public class OptimisedRegistrableScene3DImpl extends RegistrableScene3DImpl {

    public OptimisedRegistrableScene3DImpl( int terrainXSize, int terrainYSize, int terrainZSize, Index3D terrainShift ) {
        super( terrainXSize, terrainYSize, terrainZSize, terrainShift );
    }
    
    public OptimisedRegistrableScene3DImpl( Terrain3D< ? >[][][] terrain, Index3D terrainShift ) {
        super( terrain, terrainShift );
    }
}
