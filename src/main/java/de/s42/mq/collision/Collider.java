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

import de.s42.mq.core.Copyable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 * @param <CopyType>
 */
public interface Collider<CopyType extends Collider> extends Copyable<CopyType>
{

	/**
	 * Set higher that the float min epsilon to avoid irregular results
	 */
	public final static float COLLIDER_EPSILON = 0.000001f;

	/**
	 * Returns the intersection point into the result vector
	 *
	 * @param ray
	 * @param result
	 * @return
	 */
	boolean intersect(Ray ray, Vector3f result);

	/**
	 * Returns the intersection of this collider withthe given frstum matrix.
	 *
	 * @param viewProjection
	 * @return
	 */
	boolean intersectsFrustum(Matrix4f viewProjection);

	/**
	 * Retuirns true if the given point is contained in the given collider
	 *
	 * @param position
	 * @return
	 */
	boolean contains(Vector3f position);

	/**
	 * Returns the origin of the collider
	 *
	 * @return
	 */
	Vector3f getOrigin();

	/**
	 * Sets the origin of the collider
	 *
	 * @param origin
	 */
	void setOrigin(Vector3f origin);

	/**
	 * Return an associated user object
	 *
	 * @return
	 */
	Object getUserObject();

	/**
	 * Set the user object
	 *
	 * @param userObject
	 */
	void setUserObject(Object userObject);
}
