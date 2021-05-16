#type vertex
#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in float aColor;

uniform mat4 uCombined;

out vec2 TexCoords;
out vec4 fColor;

void main()
{
    gl_Position = uCombined * vec4(aPos.x, aPos.y, 0.0, 1.0);
    fColor = vec4(aColor,aColor,aColor,aColor);
    TexCoords = aTexCoords;
}

#type fragment
#version 330 core

out vec4 FragColor;

in vec4 fColor;
in vec2 TexCoords;

uniform sampler2D uTex;

void main()
{
FragColor = texture(uTex, TexCoords);
}

