#version 330

/* 
 * @license
 * 
 * Copyright Studio 42 GmbH 2017. All rights reserved.
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

uniform sampler2D inBuffer;
uniform vec2 inBufferResolution;

layout (location = 0) out vec4 colorBuffer;

#define FXAA_REDUCE_MIN   (1.0/128.0)
#define FXAA_REDUCE_MUL   (1.0/8.0)
#define FXAA_SPAN_MAX     8.0

void main()
{
    vec2 inBufferPos = gl_FragCoord.xy * inBufferResolution;

	vec3 luma = vec3(0.299, 0.587, 0.114);	
	float lumaTL = dot(luma, texture(inBuffer, inBufferPos + (vec2(-1.0, -1.0) * inBufferResolution)).xyz);
	float lumaTR = dot(luma, texture(inBuffer, inBufferPos + (vec2(1.0, -1.0) * inBufferResolution)).xyz);
	float lumaBL = dot(luma, texture(inBuffer, inBufferPos + (vec2(-1.0, 1.0) * inBufferResolution)).xyz);
	float lumaBR = dot(luma, texture(inBuffer, inBufferPos + (vec2(1.0, 1.0) * inBufferResolution)).xyz);
	float lumaM  = dot(luma, texture(inBuffer, inBufferPos).xyz);

	vec2 dir;
	dir.x = -((lumaTL + lumaTR) - (lumaBL + lumaBR));
	dir.y = ((lumaTL + lumaBL) - (lumaTR + lumaBR));
	
	float dirReduce = max((lumaTL + lumaTR + lumaBL + lumaBR) * (FXAA_REDUCE_MUL * 0.25), FXAA_REDUCE_MIN);
	float inverseDirAdjustment = 1.0/(min(abs(dir.x), abs(dir.y)) + dirReduce);
	
	dir = min(vec2(FXAA_SPAN_MAX, FXAA_SPAN_MAX), 
		max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX), dir * inverseDirAdjustment)) * inBufferResolution;

	vec3 result1 = (1.0/2.0) * (
		texture(inBuffer, inBufferPos + (dir * vec2(1.0/3.0 - 0.5))).xyz +
		texture(inBuffer, inBufferPos + (dir * vec2(2.0/3.0 - 0.5))).xyz);

	vec3 result2 = result1 * (1.0/2.0) + (1.0/4.0) * (
		texture(inBuffer, inBufferPos + (dir * vec2(0.0/3.0 - 0.5))).xyz +
		texture(inBuffer, inBufferPos + (dir * vec2(3.0/3.0 - 0.5))).xyz);

	float lumaMin = min(lumaM, min(min(lumaTL, lumaTR), min(lumaBL, lumaBR)));
	float lumaMax = max(lumaM, max(max(lumaTL, lumaTR), max(lumaBL, lumaBR)));
	float lumaResult2 = dot(luma, result2);
	
	if(lumaResult2 < lumaMin || lumaResult2 > lumaMax)
		colorBuffer = vec4(result1, 1.0);
	else
		colorBuffer = vec4(result2, 1.0);
}