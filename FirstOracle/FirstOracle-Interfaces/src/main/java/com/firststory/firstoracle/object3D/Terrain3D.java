/*
 * Copyright (c) 2018 Piotr "n1t4chi" Olejarz
 */
package com.firststory.firstoracle.object3D;

import com.firststory.firstoracle.FirstOracleConstants;
import com.firststory.firstoracle.rendering.CameraDataProvider;
import com.firststory.firstoracle.rendering.RenderingContext;
import com.firststory.firstoracle.shader.ShaderProgram3D;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

/**
 * @author n1t4chi
 */
public interface Terrain3D< Vertices extends Vertices3D >
    extends Object3D< Identity3DTransformations, Vertices >
{
    
    @Override
    default Identity3DTransformations getTransformations() {
        return Identity3DTransformations.getIdentity();
    }
    
    /**
     * Returns position in space based on position in array
     *
     * @param x          x position in array
     * @param y          y position in array
     * @param z          z position in array
     * @param arrayShift shift of array in space
     * @return position in space
     */
    Vector3fc computePosition( int x, int y, int z, Vector3ic arrayShift );
    
    
    default void bindPosition(
        RenderingContext renderingContext, int x, int y, int z, Vector3ic arrayShift
    ) {
        renderingContext.getShaderProgram3D().bindPosition( computePosition( x, y, z, arrayShift ) );
    }
    
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
