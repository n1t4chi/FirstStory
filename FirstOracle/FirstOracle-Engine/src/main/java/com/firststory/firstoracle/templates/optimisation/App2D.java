/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.templates.optimisation;

import com.firststory.firstoracle.data.Index2D;
import com.firststory.firstoracle.object2D.PositionableObject2D;
import com.firststory.firstoracle.object2D.Terrain2D;

import java.util.ArrayList;

/**
 * @author n1t4chi
 */
public class App2D {
    
    public static void main( String[] args ) {
        var terrains2D = new Terrain2D[300][300];
        var terrain2dShift = Index2D.id2( 0, 0 );
        var renderables2D = new ArrayList< PositionableObject2D< ?, ? > >( 1000 );
        
        Utils.getApp2D( terrains2D, terrain2dShift, renderables2D ).run();
    }
}
