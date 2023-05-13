#version 330

uniform sampler2D baseSampler;
uniform vec4 tint;
uniform float tintMultiplier;
uniform float alphaThreshold;

layout (location = 0) out vec4 colorBuffer;
  
in vec2 varyingUv;
in vec2 varyingUv2;
in float varyingBlend;

const vec3 relativeLuminanceConst = vec3(0.2125, 0.7154, 0.0721);

// https://en.wikipedia.org/wiki/Relative_luminance
float relativeLuminance(in vec3 color)
{
    return dot(color, relativeLuminanceConst);
}

void main()
{
	vec4 color = 
		mix(texture(baseSampler, varyingUv), 
			texture(baseSampler, varyingUv2),
			varyingBlend);

	float l = relativeLuminance(color.rgb);
	if ( l < alphaThreshold ) discard;

    colorBuffer = color * tint * tintMultiplier;
}