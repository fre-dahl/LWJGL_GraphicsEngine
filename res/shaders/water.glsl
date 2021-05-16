#type vertex
#version 330 core

layout (location=0) in vec3  aPos;
layout (location=1) in vec2  aTexCoords; // se ome jeg kan flytte denne til fragment

uniform mat4 uProjection;
uniform mat4 uView;

out vec2  fTexCoords;

void main(){
    fTexCoords = aTexCoords;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}


    #type fragment
    #version 330 core


in vec2  fTexCoords;

uniform sampler2D uTex;

out vec4 color;

void main(){

    color = texture(uTex, fTexCoords);

}