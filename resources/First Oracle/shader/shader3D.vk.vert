#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 0) uniform UniformBufferObject {
    mat4 camera;
    vec3 translation;
    vec3 scale;
    vec3 rotation;
    vec4 overlayColour;
    float maxAlphaChannel;
} ubo;

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec4 vertexColour;

/*
    CAMERA = 0-3
    POSITION = 4;
    SCALE = 5;
    ROTATION = 6;
    COLOUR = 7;
    ALPHA_CHANNEL = 8;
*/
//layout(location = 3) in vec4 uniformData[9];

layout(location = 3) in vec4 uniformCamera0;
layout(location = 4) in vec4 uniformCamera1;
layout(location = 5) in vec4 uniformCamera2;
layout(location = 6) in vec4 uniformCamera3;
layout(location = 7) in vec4 uniformPosition;
layout(location = 8) in vec4 uniformScale;
layout(location = 9) in vec4 uniformRotation;
layout(location = 10) in vec4 uniformColour;
layout(location = 11) in vec4 uniformAlpha;

layout(location = 0) out vec2 UV;
layout(location = 1) out vec4 colour;
layout(location = 2) out vec4 overlayColour;
layout(location = 3) out float maxAlphaChannel;

out gl_PerVertex {
    vec4 gl_Position;
};

float toRadians(float angle){
    return angle*0.01745329251994329576923690768488612713442871888541725456;
 }

void main() {

    float transX = uniformPosition.x;
    float transY = uniformPosition.y;
    float transZ = uniformPosition.z;
//    float transX = uniformData[4].x;
//    float transY = uniformData[4].y;
//    float transZ = uniformData[4].z;
//    float transX = ubo.translation.x;
//    float transY = ubo.translation.y;
//    float transZ = ubo.translation.z;

//    float scaleX = ubo.scale.x;
//    float scaleY = ubo.scale.y;
//    float scaleZ = ubo.scale.z;
//    float scaleX = uniformData[5].x;
//    float scaleY = uniformData[5].y;
//    float scaleZ = uniformData[5].z;
    float scaleX = uniformScale.x;
    float scaleY = uniformScale.y;
    float scaleZ = uniformScale.z;


    float rotX = uniformRotation.x;
    float rotY = uniformRotation.y;
    float rotZ = uniformRotation.z;
//    float rotX = uniformData[6].x;
//    float rotY = uniformData[6].y;
//    float rotZ = uniformData[6].z;
//    float rotX = ubo.rotation.x;
//    float rotY = ubo.rotation.y;
//    float rotZ = ubo.rotation.z;


    float posX = vertexPosition.x*scaleX;
    float posY = vertexPosition.y*scaleY;
    float posZ = vertexPosition.z*scaleZ;


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
        ubo.camera *
        vec4(
            transX + posX,
            transY + posY,
            transZ + posZ,
            1
        )
    ;
    gl_Position.z = (-gl_Position.z + 1) / 2;
    colour = vertexColour;
    UV = vertexUV;
    overlayColour = ubo.overlayColour;
    maxAlphaChannel = ubo.maxAlphaChannel;
}