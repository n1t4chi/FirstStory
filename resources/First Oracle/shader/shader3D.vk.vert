#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 0) uniform UniformBufferObject {
    mat4 camera;
} inUniform;

layout(location = 0) in vec3 inVertexPosition;
layout(location = 1) in vec2 inVertexUV;
layout(location = 2) in vec4 inVertexColour;

/*
    CAMERA = 0-3
    POSITION = 4;
    SCALE = 5;
    ROTATION = 6;
    COLOUR = 7;
    ALPHA_CHANNEL = 8;
*/
//layout(location = 3) in vec4 uniformData[9];

layout(location = 4) in vec4 inTranslation;
layout(location = 5) in vec4 inScale;
layout(location = 6) in vec4 inRotation;
layout(location = 7) in vec4 inOverlayColour;
layout(location = 8) in vec4 inMaxAlphaAlpha;
layout(location = 9) in int inTextureIndex;

layout(location = 0) out vec2 outUV;
layout(location = 1) out vec4 outColour;
layout(location = 2) out vec4 outOverlayColour;
layout(location = 3) out float outMaxAlphaChannel;
layout(location = 4) out int outTextureIndex;

out gl_PerVertex {
    vec4 gl_Position;
};

float toRadians(float angle){
    return angle*0.01745329251994329576923690768488612713442871888541725456;
 }

void main() {

    float transX = inTranslation.x;
    float transY = inTranslation.y;
    float transZ = inTranslation.z;

    float scaleX = inScale.x;
    float scaleY = inScale.y;
    float scaleZ = inScale.z;

    float rotX = inRotation.x;
    float rotY = inRotation.y;
    float rotZ = inRotation.z;

    mat4 camera = inUniform.camera;
    vec4 overlayColour = inOverlayColour;
    float maxAlphaChannel = inMaxAlphaAlpha.x;


    float posX = inVertexPosition.x*scaleX;
    float posY = inVertexPosition.y*scaleY;
    float posZ = inVertexPosition.z*scaleZ;


    if(rotX != 0){
        float y = posY;
        float z = posZ;
        float rotX = toRadians(rotX);
        float sinX = sin(rotX);
        float cosX = cos(rotX);
        posY = (y*cosX) - (z*sinX);
        posZ = (y*sinX) + (z*cosX);
    }
    if(rotY != 0){
        float x = posX;
        float z = posZ;
        float rotY = toRadians(rotY);
        float sinY = sin(rotY);
        float cosY = cos(rotY);
        posX = (x*cosY) - (z*sinY);
        posZ = (x*sinY) + (z*cosY);
    }
    if(rotZ != 0){
        float x = posX;
        float y = posY;
        float rotZ = toRadians(rotZ);
        float sinZ = sin(rotZ);
        float cosZ = cos(rotZ);
        posX = (x*cosZ) - (y*sinZ);
        posY = (x*sinZ) + (y*cosZ);
    }




    gl_Position =
        inUniform.camera *
        vec4(
            transX + posX,
            transY + posY,
            transZ + posZ,
            1
        )
    ;
    gl_Position.y = -gl_Position.y;
    gl_Position.z = (gl_Position.z + 1) / 2;

    outColour = inVertexColour;
    outUV = inVertexUV;
    outOverlayColour = overlayColour;
    outMaxAlphaChannel = maxAlphaChannel;
//    outTextureIndex = inTextureIndex;
}