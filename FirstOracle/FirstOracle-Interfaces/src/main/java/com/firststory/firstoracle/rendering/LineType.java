/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */

package com.firststory.firstoracle.rendering;

/**
 * @author n1t4chi
 */
public enum LineType {
    LINES( RenderType.LINES ),LINE_LOOP( RenderType.LINE_LOOP );
    
    private final RenderType renderType;
    
    LineType( RenderType renderType ) {
        this.renderType = renderType;
    }
    
    public RenderType getRenderType() {
        return renderType;
    }
}
