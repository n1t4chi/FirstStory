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
    }else if( base.a > 0 ){
        float r = overlay.r * overlay.a +  base.r * base.a * ( 1 - overlay.a );
        float g = overlay.g * overlay.a +  base.g * base.a * ( 1 - overlay.a );
        float b = overlay.b * overlay.a +  base.b * base.a * ( 1 - overlay.a );
        float a = overlay.a + base.a * ( 1 - overlay.a );

        ret = vec4( r/a, g/a, b/a, a );
    } else {
        ret = vec4( 0, 0, 0, 0 );
    }
}

void main() {
    vec4 baseVertexColour = texture( textureSampler, UV ).rgba;
    //c = base.x * base.a + overlay.x * overlay.a*(1- base.a)
    mixColour( baseVertexColour, overlayColour, outColor );
//    mixColour( outColor, colour, outColor );

    if(maxAlphaChannel < outColor.a){
        outColor.a = maxAlphaChannel;
    }
}
//#version 450
//#extension GL_ARB_separate_shader_objects : enable
//
//void main() {
//    vec4 baseVertexColour = texture( textureSampler, UV ).rgba;
//
//
//    //c = base.x * base.a + overlay.x * overlay.a*( 1- base.a )
////    outColor = mixColour( baseVertexColour, colour );
////    outColor = mixColour( baseVertexColour, overlayColour );
////    outColor = colour;
//
//    if( maxAlphaChannel < outColor.a ){
//        outColor.a = maxAlphaChannel;
//    }
//}