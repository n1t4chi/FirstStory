/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;

import java.util.List;

/**
 * @author n1t4chi
 */
public class NonOptimised2D extends BaseApp {
    
    public NonOptimised2D(
        Terrain2D< ? >[][] terrains2D,
        Index2D terrain2dShift,
        List< PositionableObject2D< ?, ? > > renderables2D
    ) {
        super( terrains2D, terrain2dShift, renderables2D, BaseApp.createNonOptimisedScene );
    }
}
