#type vertex
#version 330 core

layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec4 a_Color;
layout(location = 2) in vec2 a_TexCoord;

out vec4 f_Color;
out vec2 f_TexCoord;

void main()
{
    f_Color = a_Color;
    f_TexCoord = a_TexCoord;

    gl_Position = vec4(a_Position, 0.0, 1.0);
}

#type fragment
#version 330 core

in vec4 f_Color;
in vec2 f_TexCoord;

out vec4 FragColor;

uniform sampler2D u_Texture;
uniform bool u_UseTexture;

void main()
{
    if (u_UseTexture)
    {
        vec4 texColor = texture(u_Texture, f_TexCoord);
        FragColor = texColor * f_Color;
    } else
    {
        FragColor = f_Color;
    }
}