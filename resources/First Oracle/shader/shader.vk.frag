#version 450
#extension GL_ARB_separate_shader_objects : enable

layout(binding = 1) uniform sampler2D textureSampler[ 100 ];

layout(location = 0) in vec2 UV;
layout(location = 1) in vec4 colour;
layout(location = 2) in vec4 overlayColour;
layout(location = 3) in float maxAlphaChannel;
layout(location = 4) flat in int textureIndex;

layout(location = 0) out vec4 outColor;

void main() {
    vec4 baseVertexColour = colour * texture( textureSampler[ textureIndex ], UV ).rgba;
    //c = base.x * base.a + overlay.x * overlay.a*(1- base.a)
    if(overlayColour.a >= 1){
        outColor = overlayColour;
    }else if(baseVertexColour.a > 0){
        float r = overlayColour.r * overlayColour.a +  baseVertexColour.r * baseVertexColour.a * (1-overlayColour.a);
        float g = overlayColour.g * overlayColour.a +  baseVertexColour.g * baseVertexColour.a * (1-overlayColour.a);
        float b = overlayColour.b * overlayColour.a +  baseVertexColour.b * baseVertexColour.a * (1-overlayColour.a);
        float a = overlayColour.a + baseVertexColour.a * (1 - overlayColour.a);

        outColor = vec4(r/a,g/a,b/a,a);
    }else{
        outColor = vec4(0,0,0,0);
    }
    if(maxAlphaChannel < outColor.a){
        outColor.a = maxAlphaChannel;
    }
}