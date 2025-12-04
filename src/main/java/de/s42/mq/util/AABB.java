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

import de.s42.base.strings.StringHelper;
import de.s42.mq.collision.SphereCollider;
import java.util.Objects;
import org.joml.Vector3f;

/**
 * See also
 * https://gamedev.stackexchange.com/questions/18436/most-efficient-aabb-vs-ray-collision-algorithms/18459#18459
 *
 * @author Benjamin Schiller
 */
public final class AABB
{

	public Vector3f min;
	public Vector3f max;

	public AABB()
	{
		min = new Vector3f(Float.POSITIVE_INFINITY);
		max = new Vector3f(Float.NEGATIVE_INFINITY);
	}

	public AABB(AABB other)
	{
		assert other != null : "other != null";

		this.min = new Vector3f(other.min);
		this.max = new Vector3f(other.max);
	}

	public AABB(Vector3f min, Vector3f max)
	{
		assert min != null : "min != null";
		assert max != null : "max != null";

		this.min = new Vector3f(min);
		this.max = new Vector3f(max);
	}

	public AABB set(AABB other)
	{
		assert other != null : "other != null";

		max.set(other.max);
		min.set(other.min);

		return this;
	}

	public Vector3f getCenter()
	{
		return getDelta().mul(0.5f).add(min);
	}

	public AABB translate(Vector3f translate)
	{
		assert translate != null : "translate != null";

		min.add(translate);
		max.add(translate);

		return this;
	}

	public AABB merge(AABB other)
	{
		assert other != null : "other != null";

		max.max(other.max);
		min.min(other.min);

		return this;
	}

	public AABB merge(Vector3f other)
	{
		assert other != null : "other != null";

		max.max(other);
		min.min(other);

		return this;
	}

	public Vector3f getMin()
	{
		return min;
	}

	public void setMin(Vector3f min)
	{
		assert min != null : "min != null";

		this.min.set(min);
	}

	public Vector3f getMax()
	{
		return max;
	}

	public void setMax(Vector3f max)
	{
		assert max != null : "max != null";

		this.max.set(max);
	}

	public Vector3f getDelta()
	{
		return (new Vector3f(max)).sub(min);
	}

	public float getXDelta()
	{
		return max.x - min.x;
	}

	public float getYDelta()
	{
		return max.y - min.y;
	}

	public float getZDelta()
	{
		return max.z - min.z;
	}

	public Vector3f[] getCornerVectors()
	{
		return new Vector3f[]{
			new Vector3f(min.x, min.y, min.z),
			new Vector3f(min.x, min.y, max.z),
			new Vector3f(min.x, max.y, max.z),
			new Vector3f(max.x, max.y, max.z),
			new Vector3f(max.x, max.y, min.z),
			new Vector3f(max.x, min.y, min.z),
			new Vector3f(min.x, max.y, min.z),
			new Vector3f(max.x, min.y, max.z)
		};
	}

	public float getFurthestCornerDistance(Vector3f position)
	{
		assert position != null : "position != null";

		float result = 0.0f;

		for (Vector3f corner : getCornerVectors()) {
			result = Math.max(corner.distance(position), result);
		}

		return result;
	}

	public float getClosestCornerDistance(Vector3f position)
	{
		assert position != null : "position != null";

		float result = Float.POSITIVE_INFINITY;

		for (Vector3f corner : getCornerVectors()) {
			result = Math.min(corner.distance(position), result);
		}

		return result;
	}

	public SphereCollider getBoundingSphereCollider()
	{
		Vector3f deltaHalf = (new Vector3f(max)).sub(min).mul(0.5f);

		float radius = deltaHalf.length();
		Vector3f origin = deltaHalf.add(min);

		return new SphereCollider(origin, radius);
	}

	@Override
	public String toString()
	{
		return StringHelper.toString(this);
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 41 * hash + Objects.hashCode(this.min);
		hash = 41 * hash + Objects.hashCode(this.max);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final AABB other = (AABB) obj;
		if (!Objects.equals(this.min, other.min)) {
			return false;
		}

		return Objects.equals(this.max, other.max);
	}
}
