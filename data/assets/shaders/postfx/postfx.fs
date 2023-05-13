#version 330

#define VIGNETTE
#define CHROMATIC_ABBERATION

uniform sampler2D inBuffer;
uniform vec2 inBufferResolution;

uniform int blitMode;
uniform float exposure;
uniform float vignetteStart;
uniform float vignetteEnd;
uniform vec4 vignetteColor;
uniform float chromaticAbberationStrength;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

const float DEFAULT_GAMMA_FACTOR = 2.2;
const vec3 RELATIVE_LUMINANCE = vec3(0.2125, 0.7154, 0.0721);

//https://en.wikipedia.org/wiki/Relative_luminance
float relativeLuminance(in vec3 color)
{
    return dot(color, RELATIVE_LUMINANCE);
}

vec3 applyGamma(vec3 color, float gammaFactor)
{
	return pow(color, vec3(1.0/gammaFactor));
}

// http://filmicworlds.com/blog/filmic-tonemapping-operators/
vec3 tonemapHejlBurgessDawson( vec3 originalColor )
{
   originalColor *= exposure;
   vec3 x = max(vec3(0.0), originalColor - 0.004);
   vec3 color = (x*(6.2*x+.5))/(x*(6.2*x+1.7)+0.06);

   return color;
}

// https://github.com/dmnsgn/glsl-tone-map/blob/master/aces.glsl
vec3 tonemapAces( vec3 originalColor ) 
{
	originalColor *= exposure;
	const float a = 2.51;
	const float b = 0.03;
	const float c = 2.43;
	const float d = 0.59;
	const float e = 0.14;
	return applyGamma(
		clamp(
			(originalColor * (a * originalColor + b)) / 
			(originalColor * (c * originalColor + d) + e), 0.0, 1.0
		), DEFAULT_GAMMA_FACTOR
	);
}

vec3 tonemapLinear( vec3 originalColor )
{
   vec3 color = originalColor * exposure;

   return applyGamma(color, DEFAULT_GAMMA_FACTOR);
}

/**
* @param distorsion is the amount of shift per color
*/
vec4 getColorDistorted( in sampler2D inBuffer, in vec2 inBufferCoord, in vec2 direction, in vec3 distortion ) 
{
  return vec4(
		texture(inBuffer, inBufferCoord + direction * distortion.r).r,
		texture(inBuffer, inBufferCoord + direction * distortion.g).g,
		texture(inBuffer, inBufferCoord + direction * distortion.b).b,
		1.0
  );
}

void main(void) 
{

#if defined( CHROMATIC_ABBERATION )	|| defined ( VIGNETTE )

	vec2 directionFromCenter = inBufferCoord - vec2(0.5);
	float distanceFromCenter = length(directionFromCenter);
	directionFromCenter /= distanceFromCenter;
	float distanceFromCenterSq = distanceFromCenter * distanceFromCenter;

#endif


  // CHROMATIC_ABBERATION

#if defined( CHROMATIC_ABBERATION )

	//base values for chromatic distorsion
	vec3 chromaticDistorsion = vec3(-inBufferResolution.x, 0.0, inBufferResolution.x)  * chromaticAbberationStrength * distanceFromCenterSq;
	
	vec4 inBufferData = getColorDistorted(inBuffer, inBufferCoord, directionFromCenter, chromaticDistorsion);

#else

	vec4 inBufferData = texture(inBuffer, inBufferCoord);

#endif


  // VIGNETTE

#if defined( VIGNETTE )

  float vignetteStrength = smoothstep(vignetteStart, vignetteEnd, distanceFromCenter);
  inBufferData = mix(inBufferData, vignetteColor, vignetteStrength);

#endif

  // draw just red
  if (blitMode == 1) {
    outBuffer = vec4(inBufferData.r, 0.0, 0.0, 1.0);
  }
  // draw just green
  else if (blitMode == 2) {
    outBuffer = vec4(0.0, inBufferData.g, 0.0, 1.0);
  }
  // draw just blue
  else if (blitMode == 3) {
    outBuffer = vec4(0.0, 0.0, inBufferData.b, 1.0);
  }
  // draw just alpha
  else if (blitMode == 4) {
    outBuffer = vec4(inBufferData.a, inBufferData.a, inBufferData.a, 1.0);
  }
  // draw luminance
  else if (blitMode == 5) {
	float luminance = relativeLuminance(inBufferData.rgb);
    outBuffer = vec4(luminance, luminance, luminance, 1.0);
  }
  // draw inverted
  else if (blitMode == 6) {
    outBuffer = vec4(vec3(1.0) - inBufferData.rgb, 1.0);
  }
  // draw no gamma
  else if (blitMode == 7) {
    outBuffer = inBufferData;
  }
  // draw no tonemap just gamma
  else if (blitMode == 8) {
    outBuffer = vec4(applyGamma(inBufferData.rgb , DEFAULT_GAMMA_FACTOR), inBufferData.a);
  }
  // draw tonemapping data normal default gamma - 0 or default
  else if (blitMode == 9) {
	outBuffer = vec4(tonemapAces(inBufferData.rgb), inBufferData.a);
  }
  // draw tonemapping data normal default gamma - 0 or default
  else {
	outBuffer = vec4(tonemapLinear(inBufferData.rgb), inBufferData.a);
  }
}
