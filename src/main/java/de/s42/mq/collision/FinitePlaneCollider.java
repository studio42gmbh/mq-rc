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

/**
 * @todo @author Benjamin Schiller
 */
public final class FinitePlaneCollider extends AbstractCollider
{

	public Vector3f origin;
	public Vector3f normal;
	public float x;
	public float y;

	public FinitePlaneCollider()
	{
	}

	public FinitePlaneCollider(Vector3f origin, Vector3f normal, float x, float y)
	{
		this.origin = origin;
		this.normal = normal;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean contains(Vector3f position)
	{
		return false;
	}

	@Override
	public boolean intersect(Ray ray, Vector3f result)
	{
		float denominator = normal.dot(ray.direction);

		// Coplanar
		if (denominator > -COLLIDER_EPSILON && denominator < COLLIDER_EPSILON) {
			return false;
		}

		float t = origin.sub(ray.origin, new Vector3f()).dot(normal) / denominator;

		if (t < COLLIDER_EPSILON) {
			return false;
		}

		result.set(ray.direction).mul(t).add(ray.origin);

		return true;
	}

	@Override
	public Vector3f getOrigin()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setOrigin(Vector3f origin)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Object copy()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean intersectsFrustum(Matrix4f frustum)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
