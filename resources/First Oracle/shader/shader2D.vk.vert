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
    float posX = vertexPosition.x*scale.x;
    float posY = vertexPosition.y*scale.y;

    if(rotation != 0){
        float x = posX;
        float y = posY;
        float rot = toRadians(rotation);
        float sinZ = sin(rot);
        float cosZ = cos(rot);

        posX = (x*cosZ) - (y*sinZ);
        posY = (x*sinZ) + (y*cosZ);
    }
    vec4 v =
        camera *
        vec4(
            translation.x+posX,
            translation.y+posY,
            0,1
        );
    gl_Position = vec4(v.x,v.y,0,1);

    colour = vertexColour;
    UV = vertexUV;
    overlayColour = ubo.overlayColour;
    maxAlphaChannel = ubo.maxAlphaChannel;
}