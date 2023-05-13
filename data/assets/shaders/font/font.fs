#version 330

uniform sampler2D baseSampler;
uniform int identifier;

uniform vec4 tint;
uniform float tintMultiplier;
uniform float edge;
uniform float edgeSize;

uniform vec4 tint2;
uniform float tint2Multiplier;
uniform float edge2;
uniform float edge2Size;

layout (location = 0) out vec4 colorBuffer;
layout (location = 6) out int identifierBuffer;
  
in vec2 varyingUv;

void main()
{
	vec4 signedDistance = textureLod(baseSampler, varyingUv, 0);

	float edgeSizeHalf = edgeSize * 0.5;

	// outline color for fonts
	if (edge2 < edge) {
		float edge2SizeHalf = edge2Size * 0.5;
		float alpha = smoothstep(edge - edgeSizeHalf, edge + edgeSizeHalf, signedDistance.a);
		float alpha2 = smoothstep(edge2 - edge2SizeHalf, edge2 + edge2SizeHalf, signedDistance.a);
		vec3 tintFinal = mix(tint2.rgb * tint2Multiplier, tint.rgb * tintMultiplier, alpha);
		colorBuffer = vec4(tintFinal, alpha2);
	}
	// simple direct rendering
	else {
		float alpha = smoothstep(edge - edgeSizeHalf, edge + edgeSizeHalf, signedDistance.a);
		colorBuffer = vec4(tint.rgb * tintMultiplier, alpha);
	}

	//colorBuffer = vec4(1.0, 0.0, 1.0, 1.0);

	identifierBuffer = identifier;
}