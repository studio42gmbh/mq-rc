// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Studio 42 GmbH ( https://www.s42m.de ).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package de.s42.mq.util;

import static de.s42.mq.util.MQMath.PI;
import static de.s42.mq.util.MQMath.TAU;
import java.util.SplittableRandom;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 *
 * @author Benjamin Schiller
 */
public final class MQRandom
{

	public final static MQRandom create(int seed)
	{
		return new MQRandom(seed);
	}

	protected SplittableRandom random;

	public MQRandom(long seed)
	{
		random = new SplittableRandom(seed);
	}

	public void setSeed(long seed)
	{
		random = new SplittableRandom(seed);
	}

	public float nextFloat()
	{
		return random.nextFloat();
	}

	public float nextFloatPi()
	{
		return random.nextFloat(PI);
	}

	public float nextFloatTau()
	{
		return random.nextFloat(TAU);
	}

	public float nextFloat(float max)
	{
		return random.nextFloat(max);
	}

	public float nextFloat(float min, float max)
	{
		return random.nextFloat(min, max);
	}

	public Vector2f nextVector(float maxX, float maxY, Vector2f target)
	{
		target.x = nextFloat(maxX);
		target.y = nextFloat(maxY);

		return target;
	}

	public Vector3f nextVector(float maxX, float maxY, float maxZ, Vector3f target)
	{
		target.x = nextFloat(maxX);
		target.y = nextFloat(maxY);
		target.z = nextFloat(maxZ);

		return target;
	}

	public Vector4f nextVector(float maxX, float maxY, float maxZ, float maxW, Vector4f target)
	{
		target.x = nextFloat(maxX);
		target.y = nextFloat(maxY);
		target.z = nextFloat(maxZ);
		target.w = nextFloat(maxW);

		return target;
	}
}
