/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object2D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.shader.ShaderProgram2D;

/**
 * Class representing 2D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface PositionableObject2D< Transformations extends PositionableObject2DTransformations, Vertices extends Vertices2D >
    extends Object2D< Transformations, Vertices >
{
    
    void setTransformations( Transformations transformations );
    
    @Override
    default BoundingBox2D getBBO() {
        return getVertices().getBoundingBox().getTransformedBoundingBox( getTransformations(), getTransformations().getPosition() );
    }
    
    default void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ) {
        ShaderProgram2D shaderProgram2D = renderingContext.getShaderProgram2D();
        PositionableObject2DTransformations transformations = getTransformations();
    
        shaderProgram2D.bindPosition( transformations.getPosition() );
        shaderProgram2D.bindMaxAlphaChannel( 1f );
        shaderProgram2D.bindOverlayColour( FirstOracleConstants.VECTOR_ZERO_4F );
    
        shaderProgram2D.bindRotation( transformations.getRotation() );
        shaderProgram2D.bindScale( transformations.getScale() );
    
        int bufferSize = bindCurrentVerticesAndGetSize(
            renderingContext.getVertexAttributeLoader(), currentRenderTime );
        bindCurrentUvMap( renderingContext.getVertexAttributeLoader(),
            currentRenderTime,
            cameraDataProvider.getCameraRotation2D()
        );
    
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