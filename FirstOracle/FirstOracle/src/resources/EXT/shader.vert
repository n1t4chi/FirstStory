#version 330 core

// Input vertex data, different for all executions of this shader.
in vec3 vertexPosition;
in vec2 vertexUV;

// Output data ; will be interpolated for each fragment.
out vec2 UV;
out vec4 overlayVertexColour;
out float alphaOverride;

// Values that stay constant for the whole mesh.
uniform mat4 camera;

/**
    0-2 -> object position in space
    3-5 -> object scale
    6-8 -> object rotation
    9-12 -> object overlay color
    13 -> object alpha channel override
*/
uniform float[14] objectData;

void main(){
    UV = vertexUV;
    overlayVertexColour = vec4(objectData[9],objectData[10],objectData[11],objectData[12]);
    alphaOverride = objectData[13];
    float posX = vertexPosition.x*objectData[3];
    float posY = vertexPosition.y*objectData[4];
    float posZ = vertexPosition.z*objectData[5];


    if(objectData[6] != 0){
        float y = posY;
        float z = posZ;
        float sinX = sin(objectData[6]);
        float cosX = cos(objectData[6]);
        posY = (y*cosX) - (z*sinX);
        posZ = (y*sinX) + (z*cosX);
    }
    if(objectData[7] != 0){
        float x = posX;
        float z = posZ;
        float sinY = sin(objectData[7]);
        float cosY = cos(objectData[7]);

        posX = (x*cosY) - (z*sinY);
        posZ = (x*sinY) + (z*cosY);
    }
    if(objectData[8] != 0){
        float x = posX;
        float y = posY;
        float sinZ = sin(objectData[8]);
        float cosZ = cos(objectData[8]);

        posX = (x*cosZ) - (y*sinZ);
        posY = (x*sinZ) + (y*cosZ);
    }
    gl_Position =  
        camera * 
        vec4( //#3 object position in space
            objectData[0]+posX,
            objectData[1]+posY,
            objectData[2]+posZ,
            1
        )
    ;

}