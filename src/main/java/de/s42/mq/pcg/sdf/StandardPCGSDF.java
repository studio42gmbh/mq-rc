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
package de.s42.mq.pcg.sdf;

import de.s42.mq.util.MQMath;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardPCGSDF implements PCGSDF
{

	protected final float[] data;
	protected final int componentSize;
	protected int count;

	public StandardPCGSDF(int elementCount)
	{
		assert elementCount > 0 : "elementCount > 0";

		// x,y,z,r
		componentSize = 4;

		data = new float[elementCount * componentSize];

	}

	@Override
	public float getMin(Vector3f position)
	{
		assert position != null : "position != null";

		return getMin(position.x, position.y, position.z);
	}

	@Override
	public float getMin(float x, float y, float z)
	{

		float sqDistance = Float.MAX_VALUE;
		for (int i = 0; i < count * componentSize; i += componentSize) {
			float dX = data[i] - x;
			float dY = data[i + 1] - y;
			float dZ = data[i + 2] - z;
			float sSQR = data[i + 3];

			float sqD = dX * dX + dY * dY + dZ * dZ;

			if (sqD < sSQR) {
				return 0.0f;
			}

			sqDistance = Math.min(sqDistance, sqD - sSQR);
		}

		return MQMath.sqrt(sqDistance);
	}

	@Override
	public void addSphere(float x, float y, float z, float radius)
	{
		assert count * componentSize <= data.length - componentSize : "count * componentSize <= data.length - componentSize";

		int i = count * componentSize;
		data[i] = x;
		data[i + 1] = y;
		data[i + 2] = z;
		data[i + 3] = radius * radius;
		count++;
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	// "Getters/Setters" </editor-fold>
}
