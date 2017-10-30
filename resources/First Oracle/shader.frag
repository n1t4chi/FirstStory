#version 330 core

// Interpolated values from the vertex shaders
in vec2 UV;

// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform sampler2D myTextureSampler;
uniform vec4 overlayColour;
uniform float maxAlphaChannel;

void main(){
    vec4 baseVertexColour = texture( myTextureSampler, UV ).rgba;
    //c = base.x * base.a + overlay.x * overlay.a*(1- base.a)
    if(overlayColour.a >= 1){
        color = overlayColour;
    }else if(baseVertexColour.a > 0){
        float r = overlayColour.r * overlayColour.a +  baseVertexColour.r * baseVertexColour.a * (1-overlayColour.a);
        float g = overlayColour.g * overlayColour.a +  baseVertexColour.g * baseVertexColour.a * (1-overlayColour.a);
        float b = overlayColour.b * overlayColour.a +  baseVertexColour.b * baseVertexColour.a * (1-overlayColour.a);
        float a = overlayColour.a + baseVertexColour.a * (1 - overlayColour.a);

        color = vec4(r/a,g/a,b/a,a);
    }else{
        color = vec4(0,0,0,0);
    }
    if(maxAlphaChannel < color.a){
        color.a = maxAlphaChannel;
    }
}