/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.data;

import com.firststory.firstoracle.rendering.RenderType;

/**
 * @author n1t4chi
 */
public enum LineType {
    LINES( RenderType.LINES ), LINE_LOOP( RenderType.LINE_LOOP );
    
    /**
     * Returns line type based on render type.
     * If there is no corresponding type (eg. {@link RenderType#TRIANGLES } ) {@link RenderType#LINES} is returned
     *
     * @param type render type
     * @return corresponding line type
     */
    public static LineType getLineType( RenderType type ) {
        if ( RenderType.LINE_LOOP.equals( type ) ) {
            return LINE_LOOP;
        }
        return LINES;
    }
    
    private final RenderType renderType;
    
    LineType( RenderType renderType ) {
        this.renderType = renderType;
    }
    
    public RenderType getRenderType() {
        return renderType;
    }
}
