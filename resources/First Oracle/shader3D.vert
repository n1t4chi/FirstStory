#version 330 core

layout (location = 0) in vec3 vertexPosition;
layout (location = 1) in vec2 vertexUV;

out vec2 UV;

uniform mat4 camera;
uniform vec3 translation;
uniform vec3 scale;
uniform vec3 rotation;

float toRadians(float angle){
    return angle*0.01745329251994329576923690768488612713442871888541725456;
}

void main(){
    UV = vertexUV;
    float posX = vertexPosition.x*scale.x;
    float posY = vertexPosition.y*scale.y;
    float posZ = vertexPosition.z*scale.z;


    if(rotation.x != 0){
        float y = posY;
        float z = posZ;
        float rotX = toRadians(rotation.x);
        float sinX = sin(rotX);
        float cosX = cos(rotX);
        posY = (y*cosX) - (z*sinX);
        posZ = (y*sinX) + (z*cosX);
    }
    if(rotation.y != 0){
        float x = posX;
        float z = posZ;
        float rotY = toRadians(rotation.y);
        float sinY = sin(rotY);
        float cosY = cos(rotY);
        posX = (x*cosY) - (z*sinY);
        posZ = (x*sinY) + (z*cosY);
    }
    if(rotation.z != 0){
        float x = posX;
        float y = posY;
        float rotZ = toRadians(rotation.z);
        float sinZ = sin(rotZ);
        float cosZ = cos(rotZ);
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