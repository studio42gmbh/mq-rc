/* 
 * @license
 * 
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For details to the License read http://www.s42m.de/license
 * 
 * @author Benjamin Schiller
 */

// see for input and learning to this
// http://john-chapman-graphics.blogspot.cz/2013/02/pseudo-lens-flare.html
// https://github.com/jeromeetienne/threex.sslensflare

#version 330

uniform sampler2D inBuffer;
uniform vec2 inBufferResolution;
uniform float intensity;
uniform float haloIntensity;
uniform float haloRadius;
uniform float ghostStepDistance;
uniform float chromaticDistorsionScale;
uniform float flareFade;
uniform float haloFade;
uniform int samples;

in vec2 inBufferCoord;

layout (location = 0) out vec4 outBuffer;

vec4 getColor(in vec2 uv) 
{
	return texture( inBuffer, uv);
}

vec4 getColorDistorted(
    in vec2 texcoord,
    in vec2 direction, // direction of distortion
    in vec3 distortion // per-channel distortion factor
	) 
{
  return vec4(
		getColor(texcoord + direction * distortion.r).r,
		getColor(texcoord + direction * distortion.g).g,
		getColor(texcoord + direction * distortion.b).b,
		1.0
  );
}

void main() 
{
	//base values
	vec2 lensUv = inBufferCoord - vec2(0.5);
	vec2 mirroredUv = vec2(0.5) - lensUv;
	//this scale factor is very delicate on the ghost positions ...
	lensUv *= ghostStepDistance;

	//base values for chromatic distorsion
	vec3 chromaticDistorsion = vec3(-inBufferResolution.x * chromaticDistorsionScale, 0.0, inBufferResolution.x * chromaticDistorsionScale);
	vec2 chromaticDistorsionDirection = normalize(lensUv);

	vec4 flareColor = vec4(0.0);
	vec2 currentUv = mirroredUv;

	//create ghost flares
	for (int c = 0; c < samples; ++c) {

		float weight = length(vec2(0.5) - currentUv) / length(vec2(0.5));
		weight = pow(1.0 - weight, flareFade);

		flareColor += getColorDistorted(fract(currentUv), chromaticDistorsionDirection, chromaticDistorsion) * weight;

		currentUv += lensUv;
	}

	//create halo effect
	vec2 haloVec = normalize(lensUv) * haloRadius;
	float weight = length(vec2(0.5) - fract(mirroredUv + haloVec)) / length(vec2(0.5));
	weight = pow(1.0 - weight, haloFade) * haloIntensity;

	flareColor += getColorDistorted(mirroredUv + haloVec, chromaticDistorsionDirection, chromaticDistorsion) * weight;

	//set color with given intensity
	outBuffer = flareColor * intensity;
}