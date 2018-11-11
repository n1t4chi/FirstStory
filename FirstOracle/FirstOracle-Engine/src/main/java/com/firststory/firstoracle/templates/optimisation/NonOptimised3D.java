/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.data.Index3D;
import com.firststory.firstoracle.object3D.PositionableObject3D;
import com.firststory.firstoracle.object3D.Terrain3D;

import java.util.List;

/**
 * @author n1t4chi
 */
public class NonOptimised3D extends BaseApp {
    
    public NonOptimised3D(
        Terrain3D< ? >[][][] terrains3D,
        Index3D terrain3dShift,
        List< PositionableObject3D< ?, ? > > renderables3D
    ) {
        super( terrains3D, terrain3dShift, renderables3D, BaseApp.createNonOptimisedScene );
    }
}
