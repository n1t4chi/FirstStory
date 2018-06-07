/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.shader.ShaderProgram3D;

/**
 * Class representing 3D Object, contains texture, UV mapping, vertices and objectTransformations.
 *
 * @author n1t4chi
 */
public interface PositionableObject3D< Transformations extends Object3DTransformations, Vertices extends Vertices3D >
    extends Object3D< Transformations, Vertices >
{
    
    void setTransformations( Transformations transformations );
    
    @Override
    default void render(
        RenderingContext renderingContext,
        double currentRenderTime,
        CameraDataProvider cameraDataProvider
    ){
        ShaderProgram3D shaderProgram3D = renderingContext.getShaderProgram3D();
        Object3DTransformations transformations = getTransformations();
    
        shaderProgram3D.bindMaxAlphaChannel( 1f );
        shaderProgram3D.bindOverlayColour( FirstOracleConstants.VECTOR_ZERO_4F );
        
        shaderProgram3D.bindRotation( transformations.getRotation() );
        shaderProgram3D.bindScale( transformations.getScale() );
        
        int bufferSize = bindCurrentVerticesAndGetSize(
            renderingContext.getVertexAttributeLoader(), currentRenderTime );
        bindCurrentUvMap( renderingContext.getVertexAttributeLoader(), currentRenderTime, cameraDataProvider.getCameraRotation3D() );
        
        if ( renderingContext.getUseTexture() ) {
            getTexture().bind( renderingContext.getTextureLoader() );
        }
        
        renderingContext.drawTriangles( bufferSize );
        
        if ( renderingContext.getDrawBorder() ) {
            shaderProgram3D.bindMaxAlphaChannel( 1 );
            shaderProgram3D.bindOverlayColour( renderingContext.getBorderColour() );
            renderingContext.setLineWidth( 1f );
            renderingContext.drawLineLoop( bufferSize );
        }
    }
}
