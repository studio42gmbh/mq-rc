#version 330

uniform sampler2D baseSampler;
uniform int identifier;
uniform float mana;
uniform float time;
const float alphaThreshold = 0.02;

in vec2 texCoordsVarying;

layout (location = 0) out vec4 colorBuffer;
layout (location = 6) out int identifierBuffer;

void main ()
{
	vec4 color = texture(baseSampler, texCoordsVarying);

	float y = texCoordsVarying.y;

	float intensity = 
		(0.2 + smoothstep(0.0, 0.2, abs(sin(y * 10.0 * 3.1415))) * 0.8) *
		(abs(sin(-time * 2.0 + y * 3.1415 )) * 0.4 + 1.0) *
		(y * 0.5 + 0.5) * 
		(1.0 - abs(texCoordsVarying.x - 0.5));

	vec4 blue = vec4(vec3(0.3, (1.0 - texCoordsVarying.y) * 1.5 + 0.25, 1.0) * intensity, 1.0);

	float blend = step(1.0 - (mana * 10.0 - fract(mana * 10.0)) * 0.1, y);

	vec4 finalColor = mix(color, blue, blend);

	if ( finalColor.a < alphaThreshold ) discard;

	colorBuffer = finalColor;

	identifierBuffer = identifier;
}