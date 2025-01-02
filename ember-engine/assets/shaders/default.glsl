#type vertex
#version 330 core

layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec2 a_TexCoord;

out vec2 f_TexCoord;

uniform mat4 u_ViewProjection;
uniform mat4 u_Transform;

void main()
{
    f_TexCoord = a_TexCoord;

    gl_Position = u_ViewProjection * u_Transform * vec4(a_Position, 0.0, 1.0);
}

#type fragment
#version 330 core

in vec2 f_TexCoord;

out vec4 FragColor;

uniform sampler2D u_Texture;
uniform bool u_UseTexture;
uniform vec4 u_Color;

void main()
{
    vec4 texColor = texture(u_Texture, f_TexCoord);
    FragColor = u_UseTexture ? texColor * u_Color : u_Color;
}