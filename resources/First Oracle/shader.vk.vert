#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 0) uniform UniformBufferObject {
    mat4 matrix;
    vec3 trans;
    vec3 scale;
} ubo;

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec4 vertexColour;

layout(location = 0) out vec2 UV;
layout(location = 1) out vec4 colour;

out gl_PerVertex {
    vec4 gl_Position;
};

void main() {
    gl_Position = ubo.matrix * vec4(
        vertexPosition.x * ubo.scale.x + ubo.trans.x ,
        vertexPosition.y * ubo.scale.y + ubo.trans.y ,
        vertexPosition.z * ubo.scale.z + ubo.trans.z ,
          1.0
    );
    fragColor = inColor;
    fragTexCoord = inTexCoord;
}