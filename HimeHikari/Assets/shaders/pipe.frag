#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
	vec3 position;
} fs_in;

uniform vec2 bird;
uniform sampler2D tex;
uniform int top;

void main()
{
	vec2 ttc = fs_in.tc;
	vec3 tposition = fs_in.position;
	if (top == 1)
		ttc.y = 1.0 - fs_in.tc.y;
		
	color = texture(tex, ttc);
	if (color.w < 1.0)
		discard;
	color *= 2.0 / (length(bird - tposition.xy) + 1.5) + 0.5;
	color.w = 1.0;
}