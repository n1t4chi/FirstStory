/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.shader.ShaderProgram2D;
import org.joml.Vector2fc;
import org.joml.Vector2ic;

/**
 * @author n1t4chi
 */
public interface Terrain2D< Vertices extends Vertices2D >
    extends Object2D< Identity2DTransformations, Vertices >
{
    
    @Override
    default Identity2DTransformations getTransformations() {
        return Identity2DTransformations.getIdentity();
    }
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param arrayShift shift of array in space
     * @return position in space
     */
    Vector2fc computePosition( int x, int y, Vector2ic arrayShift );

    default void bindPosition(
        RenderingContext renderingContext, int x, int y, Vector2ic arrayShift
    ) {
        renderingContext.getShaderProgram2D().bindPosition( computePosition( x, y, arrayShift ) );
    }

    default void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ){
        ShaderProgram2D shaderProgram2D = renderingContext.getShaderProgram2D();
        Object2DTransformations transformations = getTransformations();
        
        shaderProgram2D.bindMaxAlphaChannel( 1f );
        shaderProgram2D.bindOverlayColour( FirstOracleConstants.VECTOR_ZERO_4F );
        
        shaderProgram2D.bindRotation( transformations.getRotation() );
        shaderProgram2D.bindScale( transformations.getScale() );
        
        int bufferSize = bindCurrentVerticesAndGetSize(
            renderingContext.getVertexAttributeLoader(), currentRenderTime );
        bindCurrentUvMap( renderingContext.getVertexAttributeLoader(), currentRenderTime, cameraDataProvider.getCameraRotation2D() );
        
        if ( renderingContext.getUseTexture() ) {
            getTexture().bind( renderingContext.getTextureLoader() );
        }
        
        renderingContext.drawTriangles( bufferSize );
        
        if ( renderingContext.getDrawBorder() ) {
            shaderProgram2D.bindMaxAlphaChannel( 1 );
            shaderProgram2D.bindOverlayColour( renderingContext.getBorderColour() );
            renderingContext.setLineWidth( 1f );
            renderingContext.drawLineLoop( bufferSize );
        }
    }
    
}
