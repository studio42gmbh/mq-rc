#version 330

#define TAU 6.283185307179586476925286766559

uniform sampler2D noiseSampler;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float time;
uniform float spriteMapSize;
uniform float timeScale;
uniform float noiseScale;
uniform float scaleScale;
uniform float baseScale;
uniform float baseSpread;
uniform float noiseSpeed;
uniform float rotationSpeed;
uniform vec3 velocity;
uniform float maxAge;
uniform float ageJitter;

in vec2 position;
in vec2 uv;
in float seed;

out vec2 varyingUv;
out vec2 varyingUv2;
out float varyingBlend;

void main()
{
	vec3 CameraRight_worldspace = vec3(viewMatrix[0][0], viewMatrix[1][0], viewMatrix[2][0]);
	vec3 CameraUp_worldspace = vec3(viewMatrix[0][1], viewMatrix[1][1], viewMatrix[2][1]);
	vec3 ParticleCenter_worldspace = vec3(modelMatrix[3][0], modelMatrix[3][1], modelMatrix[3][2]);

	float localTime = time * timeScale + 1000.0;

	vec3 noiseStatic = (textureLod(noiseSampler, vec2(seed, seed * 7.0), 0).xzy - 0.5) * 2.0;
	vec3 noise = (textureLod(noiseSampler, vec2(seed, localTime * noiseSpeed), 0).xzy - 0.5) * 2.0;

	float localMaxAge = maxAge + seed * ageJitter;	

	float generation = floor(localTime / localMaxAge);

	float age = mod(localTime, localMaxAge);

	float normalizedAge = age / localMaxAge;

	// @todo add function for scale
	float scale = baseScale + normalizedAge * scaleScale;

	// @todo add function for rotation
	float rotation = (rotationSpeed * normalizedAge + seed) * TAU + generation;

	// create simple velocity offset
	vec4 worldVelocity = modelMatrix * vec4(velocity, 1.0) - vec4(ParticleCenter_worldspace, 1.0);
	vec3 offset = age * worldVelocity.xyz; 

	// add cone like spread
	//float currentSpread = (baseSpread + age * ageSpread) * (1.0 + mod(seed, spreadJitter) - (spreadJitter / 2.0));
	//vec3 right = cross(direction, up);
	//vec3 lup = cross(direction, right);
	//+ (currentSpread * sin(seed + generation)) * lup 
	//+ (currentSpread * cos(seed + generation)) * right;
	
	// add noise to offset
	vec3 noiseOffset = noiseScale * noise + noiseStatic * baseSpread;
	offset.x += noiseOffset.x;
	offset.y += noiseOffset.y;
	offset.z += noiseOffset.z;

	vec2 rotPos = vec2(
		position.x * cos(rotation) + position.y * sin(rotation),
		-position.x * sin(rotation) + position.y * cos(rotation)
	);

	vec3 positionWorldspace = offset
		+ ParticleCenter_worldspace
		+ CameraRight_worldspace * rotPos.x * scale
		+ CameraUp_worldspace * rotPos.y * scale;

	vec3 particlePosition = positionWorldspace;

	vec4 worldPosition = /*modelMatrix **/ vec4(particlePosition, 1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;

	// calculate uvs of current and next cell

	float mapSizeSq = spriteMapSize * spriteMapSize;

	float cell = mod(normalizedAge * mapSizeSq, mapSizeSq);
	varyingBlend = fract(cell);
	cell = floor(cell);
	float cell2 = floor(mod(cell + 1, mapSizeSq));

	varyingUv = uv / spriteMapSize;
	varyingUv.x += mod(cell, spriteMapSize) / spriteMapSize;
	varyingUv.y += floor(cell / spriteMapSize) / spriteMapSize;

	varyingUv2 = uv / spriteMapSize;
	varyingUv2.x += mod(cell2, spriteMapSize) / spriteMapSize;
	varyingUv2.y += floor(cell2 / spriteMapSize) / spriteMapSize;
}  