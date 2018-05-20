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
    float posX = vertexPosition.x*ubo.scale.x;
    float posY = vertexPosition.y*ubo.scale.y;
    float posZ = vertexPosition.z*ubo.scale.z;


    if(ubo.rotation.x != 0){
        float y = posY;
        float z = posZ;
        float rotX = toRadians(ubo.rotation.x);
        float sinX = sin(rotX);
        float cosX = cos(rotX);
        posY = (y*cosX) - (z*sinX);
        posZ = (y*sinX) + (z*cosX);
    }
    if(ubo.rotation.y != 0){
        float x = posX;
        float z = posZ;
        float rotY = toRadians(ubo.rotation.y);
        float sinY = sin(rotY);
        float cosY = cos(rotY);
        posX = (x*cosY) - (z*sinY);
        posZ = (x*sinY) + (z*cosY);
    }
    if(ubo.rotation.z != 0){
        float x = posX;
        float y = posY;
        float rotZ = toRadians(ubo.rotation.z);
        float sinZ = sin(rotZ);
        float cosZ = cos(rotZ);
        posX = (x*cosZ) - (y*sinZ);
        posY = (x*sinZ) + (y*cosZ);
    }

    gl_Position =
        ubo.camera *
        vec4(
            ubo.translation.x+posX,
            ubo.translation.y+posY,
            ubo.translation.z+posZ,
            1
        )
    ;
    gl_Position.z = (-gl_Position.z + 1) / 2;
    colour = vertexColour;
    UV = vertexUV;
    overlayColour = ubo.overlayColour;
    maxAlphaChannel = ubo.maxAlphaChannel;
}