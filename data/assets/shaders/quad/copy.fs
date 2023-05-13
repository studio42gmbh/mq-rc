#version 330

in vec2 inBufferCoord;

uniform sampler2D inBuffer;

layout (location = 0) out vec4 outBuffer;

void main(void) 
{
	vec4 inBufferData = texture(inBuffer, inBufferCoord);

	outBuffer = inBufferData;
}
