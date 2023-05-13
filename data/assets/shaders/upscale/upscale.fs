#version 330

uniform vec2 inBufferResolution;
uniform sampler2D inBuffer;
uniform float sampleScale;
uniform float intensity;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

// http://www.iryoku.com/next-generation-post-processing-in-call-of-duty-advanced-warfare
// 9-tap bilinear upsampler (tent filter)
vec4 upsampleTent(in sampler2D inBuffer, in vec2 inBufferCoord, in vec4 sampleScale)
{
    vec4 d = inBufferResolution.xyxy * vec4(1.0, 1.0, -1.0, 0.0) * sampleScale;

    vec4 s;

	s =  texture(inBuffer, inBufferCoord - d.xy);
    s += texture(inBuffer, inBufferCoord - d.wy) * 2.0;
    s += texture(inBuffer, inBufferCoord - d.zy);

    s += texture(inBuffer, inBufferCoord + d.zw) * 2.0;
    s += texture(inBuffer, inBufferCoord       ) * 4.0;
    s += texture(inBuffer, inBufferCoord + d.xw) * 2.0;

    s += texture(inBuffer, inBufferCoord + d.zy);
    s += texture(inBuffer, inBufferCoord + d.wy) * 2.0;
    s += texture(inBuffer, inBufferCoord + d.xy);

    return s * (1.0 / 16.0);
}


void main(void) 
{
	vec4 inBufferData = upsampleTent(inBuffer, inBufferCoord, vec4(sampleScale)) * intensity;

	outBuffer = inBufferData;
}
