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

import de.s42.mq.util.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * See also https://gist.github.com/retroplasma/609a18fe1a71df2706751b7f43ff113f
 *
 * @author Benjamin Schiller
 */
public final class BoxCollider extends AbstractCollider
{

	public AABB aabb;
	public Matrix4f transform;

	public BoxCollider()
	{
	}

	public BoxCollider(AABB aabb, Matrix4f transform)
	{
		this.aabb = aabb;
		this.transform = transform;
	}

	@Override
	public Object copy()
	{
		BoxCollider copy = new BoxCollider(
			new AABB(aabb),
			new Matrix4f(transform)
		);
		copy.userObject = userObject;

		return copy;

	}

	@Override
	public boolean contains(Vector3f position)
	{
		// @todo
		return false;
	}

	@Override
	public boolean intersect(Ray ray, Vector3f result)
	{
		float tMin = 0.0f;
		float tMax = Float.POSITIVE_INFINITY;

		Vector3f position = transform.getRow(3, new Vector3f());
		Vector3f delta = position.sub(ray.origin, new Vector3f());

		{
			Vector3f xAxis = transform.getRow(0, new Vector3f());
			float e = xAxis.dot(delta);
			float f = ray.direction.dot(xAxis);

			if (f > COLLIDER_EPSILON || f < COLLIDER_EPSILON) {

				float t1 = (e + aabb.min.x) / f;
				float t2 = (e + aabb.max.x) / f;

				if (t1 > t2) {
					float temp = t1;
					t1 = t2;
					t2 = temp;
				}

				if (t2 < tMax) {
					tMax = t2;
				}

				if (t1 > tMin) {
					tMin = t1;
				}

				if (tMax < tMin) {
					return false;
				}
			} else {
				if (-e + aabb.min.x > 0.0f || -e + aabb.max.x < 0.0f) {
					return false;
				}
			}
		}

		{
			Vector3f yAxis = transform.getRow(1, new Vector3f());
			float e = yAxis.dot(delta);
			float f = ray.direction.dot(yAxis);

			if (f > COLLIDER_EPSILON || f < COLLIDER_EPSILON) {

				float t1 = (e + aabb.min.y) / f;
				float t2 = (e + aabb.max.y) / f;

				if (t1 > t2) {
					float temp = t1;
					t1 = t2;
					t2 = temp;
				}

				if (t2 < tMax) {
					tMax = t2;
				}

				if (t1 > tMin) {
					tMin = t1;
				}

				if (tMax < tMin) {
					return false;
				}
			} else {
				if (-e + aabb.min.y > 0.0f || -e + aabb.max.y < 0.0f) {
					return false;
				}
			}
		}

		{
			Vector3f zAxis = transform.getRow(2, new Vector3f());
			float e = zAxis.dot(delta);
			float f = ray.direction.dot(zAxis);

			if (f > COLLIDER_EPSILON || f < COLLIDER_EPSILON) {

				float t1 = (e + aabb.min.z) / f;
				float t2 = (e + aabb.max.z) / f;

				if (t1 > t2) {
					float temp = t1;
					t1 = t2;
					t2 = temp;
				}

				if (t2 < tMax) {
					tMax = t2;
				}

				if (t1 > tMin) {
					tMin = t1;
				}

				if (tMax < tMin) {
					return false;
				}
			} else {
				if (-e + aabb.min.z > 0.0f || -e + aabb.max.z < 0.0f) {
					return false;
				}
			}
		}

		result.set(ray.direction).mul(tMin).add(ray.origin);
		return true;
	}

	@Override
	public Vector3f getOrigin()
	{
		return transform.getRow(3, new Vector3f());
	}

	@Override
	public void setOrigin(Vector3f origin)
	{
		transform.setRow(3, new Vector4f(origin, 0.0f));
	}
}
