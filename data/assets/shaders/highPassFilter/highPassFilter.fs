#version 330

in vec2 inBufferCoord;

uniform sampler2D inBuffer;
uniform vec2 inBufferResolution;
uniform float thresholdLow;
uniform float thresholdHigh;
uniform float intensity;

layout (location = 0) out vec4 outBuffer;

const vec3 relativeLuminanceConst = vec3(0.2125, 0.7154, 0.0721);

// https://en.wikipedia.org/wiki/Relative_luminance
float relativeLuminance(in vec3 color)
{
    return dot(color, relativeLuminanceConst);
}

// http://www.iryoku.com/next-generation-post-processing-in-call-of-duty-advanced-warfare
vec4 downscale4box(in sampler2D inBuffer, in vec2 inBufferCoord)
{
	vec4 d = inBufferResolution.xyxy * vec4(-1.0, -1.0, 1.0, 1.0);

    vec4 s;
    s =  texture(inBuffer, inBufferCoord + d.xy);
    s += texture(inBuffer, inBufferCoord + d.zy);
    s += texture(inBuffer, inBufferCoord + d.xw);
    s += texture(inBuffer, inBufferCoord + d.zw);

    return s * (1.0 / 4.0);
}


void main(void) 
{
	//vec4 inBufferData = texture(inBuffer, inBufferCoord);
	vec4 inBufferData = downscale4box(inBuffer, inBufferCoord);

	float luminance = relativeLuminance(inBufferData.rgb);

	vec3 filteredColor = inBufferData.rgb * smoothstep(thresholdLow, thresholdHigh, luminance) * intensity;
	
	outBuffer = vec4(filteredColor, 1.0);  
}
