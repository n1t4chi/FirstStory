#version 330 core

layout (location = 0) in vec2 vertexPosition;
layout (location = 1) in vec2 vertexUV;

out vec2 UV;

uniform mat3 camera;
uniform vec2 translation;
uniform vec2 scale;
uniform float rotation;


float toRadians(float angle){
    return angle/0.01745329251994329576923690768488612713442871888541725456;
}

void main(){
    UV = vertexUV;
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
    vec3 v =
        camera *
        vec3(
            translation.x+posX,
            translation.y+posY,
            1
        );
    gl_Position = vec4(v.x,v.y,1,1);

}