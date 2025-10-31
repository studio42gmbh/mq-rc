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
package de.s42.mq;

import de.s42.mq.util.MQMath;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Benjamin Schiller
 */
public class MQColor
{

	public float r, g, b, a;

	public final static MQColor Black = new MQColor(0.0f, 0.0f, 0.0f, 1.0f);
	public final static MQColor White = new MQColor(1.0f, 1.0f, 1.0f, 1.0f);
	public final static MQColor TransparentBlack = new MQColor(0.0f, 0.0f, 0.0f, 0.0f);
	public final static MQColor TransparentWhite = new MQColor(1.0f, 1.0f, 1.0f, 0.0f);

	public final static float ONE_DIVIDED_BY_255 = 1.0f / 255.0f;

	public static MQColor createRandomColorRGB()
	{
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return new MQColor(
			random.nextFloat(),
			random.nextFloat(),
			random.nextFloat(),
			1.0f
		);
	}

	public static MQColor createRandomColor()
	{
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return new MQColor(
			random.nextFloat(),
			random.nextFloat(),
			random.nextFloat(),
			random.nextFloat()
		);
	}

	public MQColor randomColor()
	{
		ThreadLocalRandom random = ThreadLocalRandom.current();
		r = random.nextFloat();
		g = random.nextFloat();
		b = random.nextFloat();
		a = random.nextFloat();

		return this;
	}

	/*
	public static Vector3 convertToVector(Vector3 result, int argb)
	{
		assert result != null;

		result.z = ((ONE_DIVIDED_BY_255 * (float) (argb & 0xFF)) * 2.0 - 1.0);
		argb = argb >> 8;
		result.y = ((ONE_DIVIDED_BY_255 * (float) (argb & 0xFF)) * 2.0 - 1.0);
		argb = argb >> 8;
		result.x = (ONE_DIVIDED_BY_255 * (float) (argb & 0xFF)) * 2.0 - 1.0;

		return result;
	}
	 */
	public MQColor()
	{
		r = 0.0f;
		g = 0.0f;
		b = 0.0f;
		a = 1.0f;
	}

	public MQColor(float s)
	{
		this.r = s;
		this.g = s;
		this.b = s;
		this.a = 1.0f;
	}

	public MQColor(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1.0f;
	}

	public MQColor(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public MQColor(MQColor toCopy)
	{
		assert toCopy != null;

		r = toCopy.r;
		g = toCopy.g;
		b = toCopy.b;
		a = toCopy.a;
	}

	public MQColor(int argb)
	{
		b = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		argb = argb >> 8;
		g = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		argb = argb >> 8;
		r = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		argb = argb >> 8;
		a = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
	}

	public int getARGB8()
	{
		assert r >= 0.0 && r <= 1.0;
		assert g >= 0.0 && g <= 1.0;
		assert b >= 0.0 && b <= 1.0;
		assert a >= 0.0 && a <= 1.0;

		int ir = (int) (r * 255.0 + 0.5);
		int ig = (int) (g * 255.0 + 0.5);
		int ib = (int) (b * 255.0 + 0.5);
		int ia = (int) (a * 255.0 + 0.5);

		return (ia << 24) | (ir << 16) | (ig << 8) | ib;
	}

	public int getScaledARGB8(float scale)
	{
		float scale255 = scale * 255.0f;
		int ir = Math.min(Math.max(0, (int) (r * scale255 + 0.5f)), 255);
		int ig = Math.min(Math.max(0, (int) (g * scale255 + 0.5f)), 255);
		int ib = Math.min(Math.max(0, (int) (b * scale255 + 0.5f)), 255);
		int ia = Math.min(Math.max(0, (int) (a * scale255 + 0.5f)), 255);

		return (ia << 24) | (ir << 16) | (ig << 8) | ib;
	}

	public int getScaledRGB8(float scale)
	{
		float scale255 = scale * 255.0f;
		int ir = Math.min(Math.max(0, (int) (r * scale255 + 0.5f)), 255);
		int ig = Math.min(Math.max(0, (int) (g * scale255 + 0.5f)), 255);
		int ib = Math.min(Math.max(0, (int) (b * scale255 + 0.5f)), 255);

		return (255 << 24) | (ir << 16) | (ig << 8) | ib;
	}

	public MQColor set(int argb)
	{
		b = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		argb = argb >> 8;
		g = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		argb = argb >> 8;
		r = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		argb = argb >> 8;
		a = ONE_DIVIDED_BY_255 * (float) (argb & 0xFF);
		return this;
	}

	public MQColor set(String htmlColor)
	{
		java.awt.Color col = java.awt.Color.decode(htmlColor);
		r = (float) col.getRed() / 255.0f;
		g = (float) col.getGreen() / 255.0f;
		b = (float) col.getBlue() / 255.0f;
		a = 1.0f;
		return this;
	}

	public MQColor set(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1.0f;

		return this;
	}

	public MQColor set(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;

		return this;
	}

	public MQColor set(MQColor other)
	{
		assert other != null;

		r = other.r;
		g = other.g;
		b = other.b;
		a = other.a;

		return this;
	}

	public MQColor set(java.awt.Color other)
	{
		assert other != null;

		//float[] compArray = new float[4];
		//other.getColorComponents(compArray);
		r = (float) other.getRed() / 255.0f;
		g = (float) other.getGreen() / 255.0f;
		b = (float) other.getBlue() / 255.0f;
		a = (float) other.getAlpha() / 255.0f;

		return this;
	}

	/*
	public MQColor set(Vector3 other)
	{
		assert other != null;

		r = other.x;
		g = other.y;
		b = other.z;

		return this;
	}
	 */
	public MQColor copy()
	{
		return new MQColor(this);
	}

	public MQColor setRGB(MQColor other)
	{
		assert other != null;

		r = other.r;
		g = other.g;
		b = other.b;

		return this;
	}

	public MQColor add(MQColor other)
	{
		assert other != null;

		r += other.r;
		g += other.g;
		b += other.b;
		a += other.a;

		return this;
	}

	public MQColor add(float value)
	{
		r += value;
		g += value;
		b += value;
		a += value;

		return this;
	}

	public MQColor addRGB(MQColor other)
	{
		assert other != null;

		r += other.r;
		g += other.g;
		b += other.b;

		return this;
	}

	public MQColor addRGB(float value)
	{
		r += value;
		g += value;
		b += value;

		return this;
	}

	public MQColor addSoftRGB(MQColor other)
	{
		assert other != null;

		r = Math.max(r, other.r);
		g = Math.max(g, other.g);
		b = Math.max(b, other.b);

		return this;
	}

	public MQColor subtract(MQColor other)
	{
		assert other != null;

		r -= other.r;
		g -= other.g;
		b -= other.b;
		a -= other.a;

		return this;
	}

	public MQColor subtract(float value)
	{
		r -= value;
		g -= value;
		b -= value;
		a -= value;

		return this;
	}

	public MQColor subtractRGB(MQColor other)
	{
		assert other != null;

		r -= other.r;
		g -= other.g;
		b -= other.b;

		return this;
	}

	public MQColor subtractRGB(float value)
	{
		r -= value;
		g -= value;
		b -= value;

		return this;
	}

	public MQColor multiplyRGB(float scalar)
	{
		r *= scalar;
		g *= scalar;
		b *= scalar;

		return this;
	}

	public MQColor multiply(float scalar)
	{
		r *= scalar;
		g *= scalar;
		b *= scalar;
		a *= scalar;

		return this;
	}

	public MQColor divideRGB(float scalar)
	{
		assert scalar != 0.0;

		r /= scalar;
		g /= scalar;
		b /= scalar;

		return this;
	}

	public MQColor divide(float scalar)
	{
		assert scalar != 0.0;

		r /= scalar;
		g /= scalar;
		b /= scalar;
		a /= scalar;

		return this;
	}

	public MQColor multiply(MQColor other)
	{
		assert other != null;

		r *= other.r;
		g *= other.g;
		b *= other.b;
		a *= other.a;

		return this;
	}

	public MQColor multiplyRGB(MQColor other)
	{
		assert other != null;

		r *= other.r;
		g *= other.g;
		b *= other.b;

		return this;
	}

	public MQColor powRGB(float exponent)
	{
		r = (float) Math.pow(r, exponent);
		g = (float) Math.pow(g, exponent);
		b = (float) Math.pow(b, exponent);

		return this;
	}

	public MQColor clamp()
	{
		r = MQMath.saturate(r);
		g = MQMath.saturate(g);
		b = MQMath.saturate(b);
		a = MQMath.saturate(a);

		return this;
	}

	public MQColor min(float min)
	{
		r = Math.min(r, min);
		g = Math.min(g, min);
		b = Math.min(b, min);
		a = Math.min(a, min);

		return this;
	}

	public MQColor max(float max)
	{
		r = Math.max(r, max);
		g = Math.max(g, max);
		b = Math.max(b, max);
		a = Math.max(a, max);

		return this;
	}

	public MQColor blend(MQColor other, float blend)
	{
		assert other != null;
		assert blend >= 0.0f && blend <= 1.0f;

		float iblend = 1.0f - blend;
		r = r * iblend + other.r * blend;
		g = g * iblend + other.g * blend;
		b = b * iblend + other.b * blend;
		a = a * iblend + other.a * blend;

		return this;
	}

	public MQColor blendRGB(MQColor other, float blend)
	{
		assert other != null;
		assert blend >= 0.0f && blend <= 1.0;

		float iblend = 1.0f - blend;
		r = r * iblend + other.r * blend;
		g = g * iblend + other.g * blend;
		b = b * iblend + other.b * blend;

		return this;
	}

	public float getBrightness()
	{
		return (r + g + b) * (1.0f / 3.0f);
	}

	public float getR()
	{
		return r;
	}

	public float getRClamped()
	{
		return MQMath.saturate(r);
	}

	public void setR(float r)
	{
		this.r = r;
	}

	public float getG()
	{
		return g;
	}

	public float getGClamped()
	{
		return MQMath.saturate(g);
	}

	public void setG(float g)
	{
		this.g = g;
	}

	public float getB()
	{
		return b;
	}

	public float getBClamped()
	{
		return MQMath.saturate(b);
	}

	public void setB(float b)
	{
		this.b = b;
	}

	public float getA()
	{
		return a;
	}

	public float getAClamped()
	{
		return MQMath.saturate(a);
	}

	public void setA(float a)
	{
		this.a = a;
	}

	public MQColor setRGBZero()
	{
		r = 0.0f;
		g = 0.0f;
		b = 0.0f;
		return this;
	}

	public MQColor setZero()
	{
		r = 0.0f;
		g = 0.0f;
		b = 0.0f;
		a = 0.0f;

		return this;
	}

	public float[] getAsArray()
	{
		return new float[]{r, g, b, a};
	}

	public java.awt.Color toAwtColor()
	{
		return new java.awt.Color((float) getRClamped(), (float) getGClamped(), (float) getBClamped(), (float) getAClamped());
	}

	@Override
	public String toString()
	{
		return "[ r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + " ]";
	}
}
