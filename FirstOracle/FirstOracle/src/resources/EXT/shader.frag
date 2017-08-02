#version 330 core

// Interpolated values from the vertex shaders
in vec2 UV;
//Overlay colour applied to texture. Non visible parts of of texture do not have overlay colour applied to.
in vec4 overlayVertexColour;
//Alpha channel override for colour at the end. For example to make solid things transparent
in float alphaOverride;


// Ouput data
out vec4 color;

// Values that stay constant for the whole mesh.
uniform sampler2D myTextureSampler;

void main(){
    vec4 baseVertexColour = texture( myTextureSampler, UV ).rgba;
    //c = base.x * base.a + overlay.x * overlay.a*(1- base.a)
    vec4 baseColour = baseVertexColour;
    vec4 overlayColour = overlayVertexColour;
    if(overlayColour.a == 1){
        color = overlayColour;
    }else if(baseColour.a > 0){
        float r = overlayColour.r * overlayColour.a +  baseColour.r * baseColour.a * (1-overlayColour.a);
        float g = overlayColour.g * overlayColour.a +  baseColour.g * baseColour.a * (1-overlayColour.a);
        float b = overlayColour.b * overlayColour.a +  baseColour.b * baseColour.a * (1-overlayColour.a);
        float a = overlayColour.a + baseColour.a * (1 - overlayColour.a);

        color = vec4(r/a,g/a,b/a,a);
    }else{
        color = vec4(0,0,0,0);
    }
    if((color.a > 0) && (alphaOverride >= 0)){
        color.a = alphaOverride;
    }
}