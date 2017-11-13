#version 330 core

layout (location = 0) in vec3 vertexPosition;
layout (location = 1) in vec2 vertexUV;

out vec2 UV;

uniform mat4 camera;
uniform vec3 translation;
uniform vec3 scale;
uniform vec3 rotation;


void main(){
    UV = vertexUV;
    float posX = vertexPosition.x*scale.x;
    float posY = vertexPosition.y*scale.y;
    float posZ = vertexPosition.z*scale.z;


    if(rotation.x != 0){
        float y = posY;
        float z = posZ;
        float sinX = sin(rotation.x);
        float cosX = cos(rotation.x);
        posY = (y*cosX) - (z*sinX);
        posZ = (y*sinX) + (z*cosX);
    }
    if(rotation.y != 0){
        float x = posX;
        float z = posZ;
        float sinY = sin(rotation.y);
        float cosY = cos(rotation.y);

        posX = (x*cosY) - (z*sinY);
        posZ = (x*sinY) + (z*cosY);
    }
    if(rotation.z != 0){
        float x = posX;
        float y = posY;
        float sinZ = sin(rotation.z);
        float cosZ = cos(rotation.z);

        posX = (x*cosZ) - (y*sinZ);
        posY = (x*sinZ) + (y*cosZ);
    }

    gl_Position =
        camera *
        vec4(
            translation.x+posX,
            translation.y+posY,
            translation.z+posZ,
            1
        )
    ;
}