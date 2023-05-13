#version 330

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec2 screenCoord;

const vec3 sunPosition = vec3(1.0,0.0,0.0);
const float rayleigh = 1.0;
const float turbidity = 2.0;
const float mieCoefficient = 0.005;

out vec3 vWorldPosition;
out vec3 vSunDirection;
out float vSunfade;
out vec3 vBetaR;
out vec3 vBetaM;
out float vSunE;
out vec3 cameraPos;

const vec3 up = vec3(0.0, 1.0, 0.0);

// constants for atmospheric scattering
const float e = 2.71828182845904523536028747135266249775724709369995957;
const float pi = 3.141592653589793238462643383279502884197169;

// mie stuff
// K coefficient for the primaries
const float v = 4.0;
const vec3 K = vec3(0.686, 0.678, 0.666);

// see http://blenderartists.org/forum/showthread.php?321110-Shaders-and-Skybox-madness
// A simplied version of the total Reayleigh scattering to works on browsers that use ANGLE
const vec3 simplifiedRayleigh = 0.0005 / vec3(94, 40, 18);

// wavelength of used primaries, according to preetham
const vec3 lambda = vec3(680E-9, 550E-9, 450E-9);

// earth shadow hack
const float cutoffAngle = pi/1.95;
const float steepness = 1.5;
const float EE = 1000.0;

float sunIntensity(float zenithAngleCos)
{
	zenithAngleCos = clamp(zenithAngleCos, -1.0, 1.0);
	return EE * max(0.0, 1.0 - pow(e, -((cutoffAngle - acos(zenithAngleCos))/steepness)));
}

vec3 totalMie(vec3 lambda, float T)
{
	float c = (0.2 * T ) * 10E-18;
	return 0.434 * c * pi * pow((2.0 * pi) / lambda, vec3(v - 2.0)) * K;
}

// https://stackoverflow.com/questions/46182845/field-of-view-aspect-ratio-view-matrix-from-projection-matrix-hmd-ost-calib/46195462
float getAspectRatioFromPerspectiveProjectionMatrix(mat4 projectionMatrix)
{
	return projectionMatrix[1][1] / projectionMatrix[0][0];
}

// https://stackoverflow.com/questions/46182845/field-of-view-aspect-ratio-view-matrix-from-projection-matrix-hmd-ost-calib/46195462
float getFovFromPerspectiveProjectionMatrix(in mat4 projectionMatrix)
{
	return 2.0 * atan(1.0 / projectionMatrix[1][1]);
}

vec3 getCameraPositionFromViewMatrix(in mat4 viewMatrix)
{
	return (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz;
}


void main(void) 
{
  vec2 vertex = vec2(-1.0) + vec2(
    float((gl_VertexID & 1) << 2),
    float((gl_VertexID & 2) << 1));
  vec4 screenPos = vec4(vertex, 0.0, 1.0);

  gl_Position = screenPos;

  screenCoord = vertex * 0.5 + vec2(0.5, 0.5);

  float aspect = getAspectRatioFromPerspectiveProjectionMatrix(projectionMatrix);
  float fov = getFovFromPerspectiveProjectionMatrix(projectionMatrix);
  float len = tan(fov * 0.5);

    vWorldPosition = (inverse(viewMatrix) * vec4(len * vertex.x * aspect ,len * vertex.y,-1.0,1.0)).xyz;

	cameraPos = getCameraPositionFromViewMatrix(viewMatrix);

	vSunDirection = normalize(sunPosition);

	vSunE = sunIntensity(dot(vSunDirection, up));

	vSunfade = 1.0-clamp(1.0-exp((sunPosition.y/450000.0)),0.0,1.0);

	float rayleighCoefficient = rayleigh - (1.0 * (1.0-vSunfade));

	// extinction (absorbtion + out scattering)
	// rayleigh coefficients
	vBetaR = simplifiedRayleigh * rayleighCoefficient;

	// mie coefficients
	vBetaM = totalMie(lambda, turbidity) * mieCoefficient;
}