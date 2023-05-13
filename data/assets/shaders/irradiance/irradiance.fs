#version 330

precision highp float;

uniform sampler2D inBuffer;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

const float PI = 3.14159265359;
const float TAU = PI * 2.0;
const float PIHALF = PI * 0.5;
const vec2 invAtan = vec2(0.1591, 0.3183);
const int mipLevel = 5;
const float sampleDelta = PI / 256.0;

vec2 sampleSphericalMap(vec3 v)
{
    vec2 uv = vec2(atan(v.z, -v.x), asin(-v.y));
    uv *= invAtan;
    uv += 0.5;
    return uv;
}

vec3 fromSphericalCoordinates(float phi, float theta)
{
	float sinTheta = sin(theta);
	float cosTheta = cos(theta);
	float cosPhi = cos(phi);
	float sinPhi = sin(phi);

	vec3 normal = vec3(
		sinTheta * cosPhi,
		cosTheta,
		sinTheta * -sinPhi		
	);
	normalize(normal);

	return normal;
}

vec3 irradiance(vec3 normal)
{
	vec3 irradiance = vec3(0.0);  

	vec3 up    = vec3(0.0, 1.0, 0.0);
	vec3 right = normalize(cross(up, normal));
	up         = normalize(cross(normal, right));

	float nrSamples = 0.0; 
	for(float phi = 0.0; phi < TAU; phi += sampleDelta)
	{
		for(float theta = 0.0; theta < PIHALF; theta += sampleDelta)
		{
			// spherical to cartesian (in tangent space)
			vec3 tangentSample = vec3(sin(theta) * cos(phi),  sin(theta) * sin(phi), cos(theta));
			// tangent space to world
			vec3 sampleVec = tangentSample.x * right + tangentSample.y * up + tangentSample.z * normal; 

			irradiance += texture(inBuffer, sampleSphericalMap(sampleVec), mipLevel).rgb * cos(theta) * sin(theta);
			nrSamples++;
		}
	}
	irradiance = PI * irradiance * (1.0 / nrSamples);

	return irradiance;
}

void main(void) {

	float phi	= inBufferCoord.x * TAU;
	float theta	= inBufferCoord.y * PI;

	outBuffer = vec4(irradiance(fromSphericalCoordinates(phi, theta)), 1.0);
}

