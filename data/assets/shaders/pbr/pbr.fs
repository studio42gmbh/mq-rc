#version 330

precision highp float;

uniform sampler2D baseSampler;
uniform sampler2D heromeaoSampler;
uniform sampler2D normalSampler;
uniform sampler2D emtrSampler;
uniform samplerCube environmentSampler;
uniform samplerCube irradianceSampler;
uniform sampler2D brdfLUTSampler;
uniform int identifier;
uniform float colorScale;

in vec2 texCoordsVarying;
in vec3 worldPositionVarying;
in vec3 normalVarying;
in vec3 toCameraVector;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 albedoBuffer;
layout (location = 2) out vec4 normalBuffer;
layout (location = 3) out vec4 positionBuffer;
layout (location = 4) out vec4 specialBuffer;
layout (location = 5) out vec4 irradianceBuffer;
layout (location = 6) out int identifierBuffer;

const float PI = 3.14159265359;
const float PI2 = PI * 2.0;
const vec2 invAtan = vec2(1.0 / PI2, 1.0 / PI);
const float DEFAULT_GAMMA_FACTOR = 2.2;
const int maxMipLevel = 5;
const vec2 normalScale = vec2(1.0);
const float roughnessScale = 1.0;
const float metalnessScale = 1.0;
const vec3 emissiveScale = vec3(1.0);
float faceDirection = gl_FrontFacing ? 1.0 : - 1.0;

vec3 saturate(vec3 value)
{
	return clamp(value, 0.0, 1.0);
}

vec2 saturate(vec2 value)
{
	return clamp(value, 0.0, 1.0);
}

float saturate(float value)
{
	return clamp(value, 0.0, 1.0);
}

vec2 sampleSphericalMap(vec3 v)
{
    vec2 uv = vec2(atan(v.z, v.x) + PI, asin(-v.y));
    uv *= invAtan;
    uv += 0.5;
    return uv;
}

vec3 getNormalFromMap(float mipLevel)
{
    vec3 tangentNormal = texture(normalSampler, texCoordsVarying, mipLevel).xyz * 2.0 - 1.0;
	tangentNormal.xy *= normalScale;
	tangentNormal = normalize(tangentNormal);

    vec3 Q1  = dFdx(worldPositionVarying);
    vec3 Q2  = dFdy(worldPositionVarying);
    vec2 st1 = dFdx(texCoordsVarying);
    vec2 st2 = dFdy(texCoordsVarying);

    vec3 N   = normalize(normalVarying);
    vec3 T  = -normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));

	// using matrix
    //mat3 TBN = mat3(T, B, N);
    //return normalize(TBN * tangentNormal);

	// optimized approach using direct computation
	float det = max( dot( T, T ), dot( B, B ) );
	float scale = ( det == 0.0 ) ? 0.0 : faceDirection * inversesqrt( det );
	return normalize( T * ( tangentNormal.x * scale ) + B * ( tangentNormal.y * scale ) + N * tangentNormal.z );
}

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(max(1.0 - cosTheta, 0.0), 5.0);
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(max(1.0 - cosTheta, 0.0), 5.0);
}   

vec3 applyGamma(vec3 color, float gammaFactor)
{
	return pow(color, vec3(1.0/gammaFactor));
}

vec3 applyInvGamma(vec3 color, float invGammaFactor)
{
	return pow(color, vec3(invGammaFactor));
}

float getSpecularMIPLevel( const in float roughness, const in int maxMIPLevel ) 
{
	// simple version assuming the prefiltering in envmap has happened and normalized roughness space there
	return roughness * float(maxMipLevel);

	// complex version
	// Trowbridge-Reitz distribution to Mip level, following the logic of http://casual-effects.blogspot.ca/2011/08/plausible-environment-lighting-in-two.html
	float maxMIPLevelScalar = float( maxMIPLevel );
	float sigma = PI * roughness * roughness / ( 1.0 + roughness );
	float desiredMIPLevel = maxMIPLevelScalar + log2( sigma );
	// clamp to allowable LOD ranges.
	return clamp( desiredMIPLevel, 0.0, maxMIPLevelScalar );
}

vec3 irradiance(vec3 normal)
{
	vec3 irradiance = vec3(0.0);  
	vec3 up    = vec3(0.0, 1.0, 0.0);
	vec3 right = normalize(cross(up, normal));
	up         = normalize(cross(normal, right));

	const float sampleDelta = PI * 0.025;
	float nrSamples = 0.0; 
	for(float phi = 0.0; phi < 2.0 * PI; phi += sampleDelta)
	{
		for(float theta = 0.0; theta < 0.5 * PI; theta += sampleDelta)
		{
			// spherical to cartesian (in tangent space)
			vec3 tangentSample = vec3(sin(theta) * cos(phi),  sin(theta) * sin(phi), cos(theta));
			// tangent space to world
			vec3 sampleVec = tangentSample.x * right + tangentSample.y * up + tangentSample.z * normal; 

			irradiance += texture(environmentSampler, sampleVec, maxMipLevel).rgb * cos(theta) * sin(theta);
			nrSamples++;
		}
	}
	irradiance = PI * irradiance * (1.0 / float(nrSamples));

	return irradiance;
}

vec3 irradianceMap(vec3 normal)
{
	return textureLod(irradianceSampler, normal, 0).rgb;
}

vec3 environmentMap(vec3 normal, float mipBias)
{
	return textureLod(environmentSampler, normal, mipBias).rgb;
}

void main ()
{
	vec3 albedo = applyInvGamma(texture(baseSampler, texCoordsVarying).xyz, DEFAULT_GAMMA_FACTOR);
	vec4 emtr = texture(emtrSampler, texCoordsVarying);
	vec4 heromeao = texture(heromeaoSampler, texCoordsVarying);
	float height = heromeao.r;
	float roughness = saturate(heromeao.g * roughnessScale);
	float metalness = saturate(heromeao.b * metalnessScale);
	float ao = heromeao.a;
	vec3 emissive = applyInvGamma(emtr.rgb, DEFAULT_GAMMA_FACTOR) * emissiveScale;
	float translucency = emtr.a;
	float specularMipLevel = getSpecularMIPLevel(roughness, maxMipLevel);

	vec3 N = getNormalFromMap(0.0);
    vec3 V = normalize(toCameraVector - worldPositionVarying);

	vec3 specularVec = reflect(-V, N);

	// Mixing the reflection with the normal is more accurate and keeps rough objects from gathering light from behind their tangent plane.
	//specularVec = normalize( mix( specularVec, N, roughness * roughness) );

	vec3 environmentSpecularColor = environmentMap(specularVec, specularMipLevel);

	float cosTheta = max(dot(N, V), 0.01); // 0.01 makes the fresnel not so sharp at grasing angles
	vec3 F0 = vec3(0.04); 
    F0 = mix(F0, albedo, metalness);
	vec3 fresnel = fresnelSchlickRoughness(cosTheta, F0, roughness);
	vec3 kD = vec3(1.0) - fresnel;
    kD *= 1.0 - metalness;

	vec3 irradiance = irradianceMap(N);

    vec3 diffuse = irradiance * albedo;

	// avoiding inverted gamma corection - tested with reference substance painter
	vec2 brdf  = textureLod(brdfLUTSampler, vec2(cosTheta, roughness), 0).xy;

	vec3 specular = environmentSpecularColor * (fresnel * brdf.x + brdf.y);

	// final combination of terms
	vec3 color = ((diffuse * kD + specular) * ao + emissive) * colorScale;

    colorBuffer = vec4(color, 1.0);

	specialBuffer = vec4(metalness, roughness, height, ao);

	albedoBuffer = vec4(albedo, 1.0);

	normalBuffer = vec4(N, 1.0);

	positionBuffer = vec4(worldPositionVarying, 1.0);

	irradianceBuffer = vec4(irradiance, 1.0);

	identifierBuffer = identifier;
}