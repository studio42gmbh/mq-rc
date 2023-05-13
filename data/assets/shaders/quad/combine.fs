#version 330

uniform sampler2D inBuffer;
uniform sampler2D inBuffer2;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

void main(void) 
{
	vec4 inBufferData = texture(inBuffer, inBufferCoord);
	vec4 inBuffer2Data = texture(inBuffer2, inBufferCoord);

	outBuffer = inBufferData * inBuffer2Data;
}
