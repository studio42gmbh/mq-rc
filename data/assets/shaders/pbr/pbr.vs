#version 330

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform sampler2D heromeaoSampler;

in vec3 position;
in vec3 normal;
in vec2 texCoords;

out vec2 texCoordsVarying;
out vec3 worldPositionVarying;
out vec3 normalVarying;
out vec3 toCameraVector;
out vec3 toLightVector;
out vec3 lightColor;

const float heightScale = 0.0;

vec3 getCameraPositionFromViewMatrix(in mat4 viewMatrix)
{
	return (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz;
}

float getHeight(vec4 heromeao)
{
	return heromeao.r;
}

void main() 
{
  texCoordsVarying = texCoords;

  float height = getHeight(texture(heromeaoSampler, texCoordsVarying));

  normalVarying = normalize(mat3(modelMatrix) * normal);

  vec3 transformedPosition = position;

  // displace
  vec3 displacement = normal * height * heightScale;
  transformedPosition = transformedPosition + displacement;

  vec4 worldPosition = modelMatrix * vec4(transformedPosition, 1.0);
  worldPositionVarying = worldPosition.xyz;
  gl_Position = projectionMatrix * viewMatrix * worldPosition;

  toCameraVector = getCameraPositionFromViewMatrix(viewMatrix);
}
