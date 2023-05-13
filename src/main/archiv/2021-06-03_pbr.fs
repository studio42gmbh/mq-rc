#version 450

precision highp float;

uniform sampler2D baseSampler;
uniform sampler2D heromeaoSampler;
uniform sampler2D normalSampler;
uniform sampler2D emtrSampler;
uniform sampler2D environmentSampler;

in vec2 texCoordsVarying;
in vec3 worldPositionVarying;
in vec3 normalVarying;
in vec3 toCameraVector;
in vec3 toLightVector;
in vec3 lightColor;

layout (location = 0) out vec4 colorBuffer;
layout (location = 1) out vec4 albedoBuffer;
layout (location = 2) out vec4 normalBuffer;
layout (location = 3) out vec4 positionBuffer;
layout (location = 4) out vec4 specialBuffer;

const float PI = 3.14159265359;
const vec2 invAtan = vec2(0.1591, 0.3183);
const float DEFAULT_GAMMA_FACTOR = 2.2;

vec3 getNormalFromMap()
{
    vec3 tangentNormal = texture(normalSampler, texCoordsVarying).xyz * 2.0 - 1.0;

    vec3 Q1  = dFdx(worldPositionVarying);
    vec3 Q2  = dFdy(worldPositionVarying);
    vec2 st1 = dFdx(texCoordsVarying);
    vec2 st2 = dFdy(texCoordsVarying);

    vec3 N   = normalize(normalVarying);
    vec3 T  = -normalize(Q1*st2.t - Q2*st1.t);
    vec3 B  = -normalize(cross(N, T));
    mat3 TBN = mat3(T, B, N);

    return normalize(TBN * tangentNormal);
}

float distributionGGX(vec3 N, vec3 H, float roughness)
{
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}

float geometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}

float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = geometrySchlickGGX(NdotV, roughness);
    float ggx1 = geometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(max(1.0 - cosTheta, 0.0), 5.0);
}

float fresnelApproximation(float dotDirectionNormal, float exponentF, float minF)
{
	return max(minF, pow(1.0 - max(dotDirectionNormal, 0.0), exponentF));
}

float fresnel(float dotDirectionNormal, float currentIor)
{
	float cosi = dotDirectionNormal;
	float etai = 1.0;
	float etat = currentIor;
	if (dotDirectionNormal < 0.0) {
		float temp = etai;
		etai = etat;
		etat = temp;
	}

	float sint = etai / etat * sqrt(max(0.0, 1.0 - cosi * cosi));

	if (sint >= 1) {
		return 1.0;
	}
	else {
		float cost = sqrt(max(0.0, 1.0 - sint * sint));
		cosi = abs(cosi);
		float Rs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost));
		float Rp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost));
		return (Rs * Rs + Rp * Rp) / 2.0;
	}
}

vec3 applyGamma(vec3 color, float gammaFactor)
{
	return pow(color, vec3(1.0/gammaFactor));
}

vec3 applyInvGamma(vec3 color, float invGammaFactor)
{
	return pow(color, vec3(invGammaFactor));
}

void main ()
{
  vec3 unitNormal = normalize(normalVarying);
  vec3 unitLight = normalize(toLightVector);
  vec3 unitToCameraVector = normalize(toCameraVector);

  vec3 albedo = applyInvGamma(texture(baseSampler, texCoordsVarying).rgb, 2.2);
  vec4 heromeao = texture(heromeaoSampler, texCoordsVarying);
  vec4 emtr = texture(emtrSampler, texCoordsVarying);
  float height = heromeao.r;
  float roughness = heromeao.g;
  float metalness = heromeao.b;
  float ao = heromeao.a;
  vec3 emissive = emtr.rgb;

	vec3 N = getNormalFromMap();
    vec3 V = unitToCameraVector;

	vec3 F0 = vec3(0.04); 
    F0 = mix(F0, albedo, metalness);
	           
    // reflectance equation
    vec3 Lo = vec3(0.0);
    for(int i = 0; i < 1; ++i) 
    {
        // calculate per-light radiance
        vec3 L = normalize(toLightVector - worldPositionVarying);
        vec3 H = normalize(V + L);
        float distance    = length(toLightVector - worldPositionVarying);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance     = lightColor * attenuation;        
        
        // cook-torrance brdf
        float NDF = distributionGGX(N, H, roughness);        
        float G   = geometrySmith(N, V, L, roughness);      
        vec3 F    = fresnelSchlick(max(dot(H, V), 0.0), F0);       
        
        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - metalness;	  
        
        vec3 numerator    = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
        vec3 specular     = numerator / max(denominator, 0.001);  
            
        // add to outgoing radiance Lo
        float NdotL = max(dot(N, L), 0.0);                
        Lo += (kD * albedo / PI + specular) * radiance * NdotL; 
    }   
  
    vec3 ambient = vec3(0.03) * albedo * ao + emissive;
    vec3 color = ambient + Lo;
    color = color / (color + vec3(1.0));
    color = applyGamma(color, DEFAULT_GAMMA_FACTOR);  
   
    colorBuffer = vec4(color, 1.0);

	specialBuffer = vec4(metalness, roughness, height, ao);

	albedoBuffer = vec4(albedo, 1.0);

	normalBuffer = vec4(N, 1.0);

	positionBuffer = vec4(worldPositionVarying, 1.0);

}