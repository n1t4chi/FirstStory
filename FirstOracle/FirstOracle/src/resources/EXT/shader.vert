#version 330 core

// Input vertex data, different for all executions of this shader.
in vec3 vPos;
in vec2 vUV;

// Output data ; will be interpolated for each fragment.
out vec2 UV;

// Values that stay constant for the whole mesh.
uniform mat4 camera;
uniform vec3 objPos;
uniform vec3 objScale;

void main(){

    // Output position of the vertex, in clip space : MVP * position
    gl_Position =  camera * (vec4(vPos.x*(objScale.x),vPos.y*(objScale.y),vPos.z*(objScale.z),1)+vec4(objPos,1));

    // UV of the vertex. No special space for this one.
    UV = vUV;
}