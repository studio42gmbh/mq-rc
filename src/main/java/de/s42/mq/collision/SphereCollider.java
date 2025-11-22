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

import org.joml.Vector3f;

/**
 * See https://gamedev.stackexchange.com/questions/96459/fast-ray-sphere-collision-code
 *
 * @author Benjamin Schiller
 */
public final class SphereCollider extends AbstractCollider<SphereCollider>
{

	public Vector3f origin;
	public float radius;
	public float radiusSquared;

	public SphereCollider()
	{
	}

	public SphereCollider(Vector3f origin, float radius)
	{
		this.origin = origin;
		this.radius = radius;
		this.radiusSquared = radius * radius;
	}

	@Override
	public SphereCollider copy()
	{
		SphereCollider copy = new SphereCollider(new Vector3f(origin), radius);
		copy.userObject = userObject;

		return copy;
	}

	@Override
	public boolean contains(Vector3f position)
	{
		return origin.distanceSquared(position) < radiusSquared;
	}

	@Override
	public boolean intersect(Ray ray, Vector3f result)
	{
		Vector3f delta = (new Vector3f(ray.origin)).sub(origin);
		float b = delta.dot(ray.direction);
		float c = delta.dot(result) - radiusSquared;

		// Exit if r’s origin outside s (c > 0) and r pointing away from s (b > 0)
		if (c > 0.0f && b > 0.0f) {
			return false;
		}

		float discr = b * b - c;

		// A negative discriminant corresponds to ray missing sphere
		if (discr < 0.0f) {
			return false;
		}

		// Ray now found to intersect sphere, compute smallest t value of intersection
		float t = -b - (float) Math.sqrt(discr);

		// If t is negative, ray started inside sphere (so clamp t to zero or false)
		if (t < 0.0f) {
			return false;
			//t = 0.0f; // allows to hit the surface
		}

		result.set(ray.direction).mul(t).add(ray.origin);

		return true;
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
		this.radiusSquared = radius * radius;
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
