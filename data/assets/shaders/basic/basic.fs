#version 330

uniform sampler2D baseSampler;
uniform int identifier;
const float alphaThreshold = 0.001; // @todo make alphaThreshold configurable

in vec2 texCoordsVarying;

layout (location = 0) out vec4 colorBuffer;
layout (location = 6) out int identifierBuffer;

void main ()
{
	colorBuffer = texture(baseSampler, texCoordsVarying);

	if ( colorBuffer.a < alphaThreshold ) discard;

	identifierBuffer = identifier;
}