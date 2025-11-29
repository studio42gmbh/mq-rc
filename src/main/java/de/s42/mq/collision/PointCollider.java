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
package de.s42.mq.collision;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * See https://gamedev.stackexchange.com/questions/96459/fast-ray-sphere-collision-code
 *
 * @author Benjamin Schiller
 */
public final class PointCollider extends AbstractCollider<PointCollider>
{

	public Vector3f origin;

	public PointCollider()
	{
	}

	public PointCollider(Vector3f origin)
	{
		this.origin = origin;
	}

	@Override
	public PointCollider copy()
	{
		PointCollider copy = new PointCollider(new Vector3f(origin));
		copy.userObject = userObject;

		return copy;
	}

	@Override
	public boolean contains(Vector3f position)
	{
		return false;
	}

	@Override
	public boolean intersect(Ray ray, Vector3f result)
	{
		return false;
	}

	@Override
	public boolean intersectsFrustum(Matrix4f viewProjection)
	{
		Vector4f test = (new Vector4f(origin, 1.0f)).mul(viewProjection);
		test.div(test.w);

		return !(test.x < -1 || test.x > 1
			|| test.y < -1 || test.y > 1
			|| test.z < -1 || test.z > 1);
	}

	@Override
	public Vector3f getOrigin()
	{
		return origin;
	}

	@Override
	public void setOrigin(Vector3f origin)
	{
		this.origin = origin;
	}
}
