#type vertex
#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoordinate;
layout (location = 2) in vec4 aTint;

uniform mat4 uCombined;

out vec2 texCoordinate;
out vec4 tint;

void main()
{
    tint = aTint;
    tint.a *= (255.0/254.0);
    gl_Position = uCombined * vec4(aPos.x, aPos.y, 0.0, 1.0);
    texCoordinate = aTexCoordinate;
}


#type fragment
#version 330 core

out vec4 fragmentColor;

in vec4 tint;
in vec2 texCoordinate;

uniform sampler2D uTex;

void main()
{
    fragmentColor = tint * texture(uTex, texCoordinate);
}
