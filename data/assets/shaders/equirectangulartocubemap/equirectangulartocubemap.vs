#version 330

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

layout (location = 0) in vec3 vertexPosition;

out vec3 worldPosition;

void main() 
{
  worldPosition = vertexPosition;
  gl_Position = projectionMatrix * viewMatrix * vec4(vertexPosition, 1.0);
}