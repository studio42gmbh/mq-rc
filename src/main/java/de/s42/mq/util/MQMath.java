/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.util;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public final class MQMath
{

	public static final float EPSILON = 0.000001f;
	public static final float ONE_MINUS_EPSILON = 1.0f - EPSILON;
	public static final float ONE_PLUS_EPSILON = 1.0f + EPSILON;
	public static final float PI = (float) Math.PI;
	public static final float TAU = (float) (Math.PI * 2.0);
	public static final float PIHALF = (float) (Math.PI * 0.5);

	private MQMath()
	{
	}

	public static float saturate(float value)
	{
		return clamp(value, 0.0f, 1.0f);
	}

	public static float clamp(float value, float min, float max)
	{
		assert max >= min;

		return Math.max(min, Math.min(max, value));
	}

	public static float lerp(float value, float target, float t)
	{
		return value + (target - value) * t;
	}

	public static Vector2f lerp(Vector2f value, Vector2f target, float t)
	{
		return new Vector2f(
			value.x + (target.x - value.x) * t,
			value.y + (target.y - value.y) * t
		);
	}

	public static Vector3f lerp(Vector3f value, Vector3f target, float t)
	{
		return new Vector3f(
			value.x + (target.x - value.x) * t,
			value.y + (target.y - value.y) * t,
			value.z + (target.z - value.z) * t
		);
	}

	public static float toRadians(float degrees)
	{
		return (float) Math.toRadians(degrees);
	}

	public static float toDegrees(float radians)
	{
		return (float) Math.toDegrees(radians);
	}
}
