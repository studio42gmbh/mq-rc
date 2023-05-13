#version 330

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

in vec3 position;
in vec2 uv;
layout (location = 2) in vec2 glyphPos;
layout (location = 3) in vec2 glyphSize;
layout (location = 4) in vec2 glyphUvPos;
layout (location = 5) in vec2 glyphUvSize;

out vec2 varyingUv;

void main()
{
	varyingUv = uv;
	varyingUv.x = glyphUvPos.x + varyingUv.x * glyphUvSize.x;
	varyingUv.y = glyphUvPos.y + varyingUv.y * glyphUvSize.y;

	vec3 pos = position;
	pos.x = glyphPos.x + pos.x * glyphSize.x;
	pos.y = glyphPos.y + pos.y * glyphSize.y;

	vec4 worldPosition = modelMatrix * vec4(pos, 1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
}  