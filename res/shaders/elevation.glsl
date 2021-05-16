#type vertex
#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in float aElevation;

//uniform mat4 uProjection;
//uniform mat4 uView;
uniform mat4 uCombined;

out float fNoise;

const float contrastMin = 0.1;
const float contrastMax = 1.0;
const float noiseWeight = 0.05;

const float noiseWeightInv = 1 / (1 + noiseWeight);

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

// just set this differently in water shader to get depth. Voila
float setContrast(float f, float b, float e) {
    return b + (e - b) * f;
}

void main(){

    //gl_Position = uProjection * uView * vec4(aPos,0.0f, 1.0f);
    gl_Position = uCombined * vec4(aPos,0.0f, 1.0f);
    float noise = rand(aPos);
    float elevWithNoice = (aElevation + noise * noiseWeight) * noiseWeightInv;
    fNoise = setContrast(elevWithNoice, contrastMin, contrastMax);
}



#type fragment
#version 330 core


in float fNoise;
out vec4 color;

void main(){

    color = vec4(fNoise,fNoise,fNoise,1.0);

}