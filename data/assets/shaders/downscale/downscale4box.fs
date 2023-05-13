#version 330

uniform vec2 inBufferResolution;
uniform sampler2D inBuffer;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

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
	vec4 inBufferData = downscale4box(inBuffer, inBufferCoord);

	outBuffer = inBufferData;
}
