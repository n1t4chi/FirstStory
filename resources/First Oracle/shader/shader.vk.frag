#version 450
#extension GL_ARB_separate_shader_objects : enable

layout( binding = 1 ) uniform sampler2D textureSampler;
layout( location = 0 ) in vec2 UV;
layout( location = 1 ) in vec4 colour;
layout( location = 2 ) in vec4 overlayColour;
layout( location = 3 ) in float maxAlphaChannel;
layout( location = 4 ) flat in int textureIndex;

layout( location = 0 ) out vec4 outColor;

void mixColour( in vec4 base, in vec4 overlay, out vec4 ret ) {
    if( overlay.a >= 1 ){
        ret = overlay;
    } if( overlay.a <= 0 ){
        ret = base;
    } else {
        float r = overlay.r * overlay.a +  base.r * base.a * ( 1 - overlay.a );
        float g = overlay.g * overlay.a +  base.g * base.a * ( 1 - overlay.a );
        float b = overlay.b * overlay.a +  base.b * base.a * ( 1 - overlay.a );
        float a = overlay.a + base.a * ( 1 - overlay.a );

        ret = vec4( r/a, g/a, b/a, a );
    }
}

void main() {
    outColor = texture( textureSampler, UV ).rgba;
    mixColour( outColor, colour, outColor );
    mixColour( outColor, overlayColour, outColor );

    if( maxAlphaChannel < outColor.a ) {
        outColor.a = maxAlphaChannel;
    }
    if( outColor.a <= 0 ) {
        discard;
    }
}