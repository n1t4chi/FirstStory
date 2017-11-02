#version 330 core

// Input vertex data, different for all executions of this shader.
layout (location = 0) in vec2 vertexPosition;
layout (location = 1) in vec2 vertexUV;

// Output data ; will be interpolated for each fragment.
out vec2 UV;

// Values that stay constant for the whole mesh.
uniform mat3 camera;
uniform vec2 translation;
uniform vec2 scale;
uniform float rotation;

/**
    0-2 -> object position in space
    3-5 -> object scale
    6-8 -> object rotation
    9-12 -> object overlay color
    13 -> object max alpha channel override
*/
//uniform float[14] objectData;

void main(){
    UV = vertexUV;
    float posX = vertexPosition.x*scale.x;
    float posY = vertexPosition.y*scale.y;

    if(rotation != 0){
        float x = posX;
        float y = posY;
        float sinZ = sin(rotation);
        float cosZ = cos(rotation);

        posX = (x*cosZ) - (y*sinZ);
        posY = (x*sinZ) + (y*cosZ);
    }
    vec3 v =
        camera *
        vec3( //#3 object position in space
            translation.x+posX,
            translation.y+posY,
            1
        );
    gl_Position = vec4(v.x,v.y,v.z,1);

}