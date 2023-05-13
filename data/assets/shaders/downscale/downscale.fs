#version 330

uniform vec2 inBufferResolution;
uniform sampler2D inBuffer;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

// http://www.iryoku.com/next-generation-post-processing-in-call-of-duty-advanced-warfare
vec4 downscale13(in sampler2D inBuffer, in vec2 inBufferCoord)
{
	vec4 A = texture(inBuffer, inBufferCoord + inBufferResolution * vec2(-1.0, -1.0));
    vec4 B = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 0.0, -1.0));
    vec4 C = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 1.0, -1.0));
    vec4 D = texture(inBuffer, inBufferCoord + inBufferResolution * vec2(-0.5, -0.5));
    vec4 E = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 0.5, -0.5));
    vec4 F = texture(inBuffer, inBufferCoord + inBufferResolution * vec2(-1.0,  0.0));
    vec4 G = texture(inBuffer, inBufferCoord                                 );
    vec4 H = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 1.0,  0.0));
    vec4 I = texture(inBuffer, inBufferCoord + inBufferResolution * vec2(-0.5,  0.5));
    vec4 J = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 0.5,  0.5));
    vec4 K = texture(inBuffer, inBufferCoord + inBufferResolution * vec2(-1.0,  1.0));
    vec4 L = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 0.0,  1.0));
    vec4 M = texture(inBuffer, inBufferCoord + inBufferResolution * vec2( 1.0,  1.0));

    vec2 div = (1.0 / 4.0) * vec2(0.5, 0.125);

    vec4 o = (D + E + I + J) * div.x;
    o += (A + B + G + F) * div.y;
    o += (B + C + H + G) * div.y;
    o += (F + G + L + K) * div.y;
    o += (G + H + M + L) * div.y;

    return o;
}

void main(void) 
{
	vec4 inBufferData = downscale13(inBuffer, inBufferCoord);

	outBuffer = inBufferData;
}
