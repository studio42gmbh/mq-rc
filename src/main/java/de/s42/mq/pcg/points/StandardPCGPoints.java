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
package de.s42.mq.pcg.points;

import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardPCGPoints implements PCGPoints
{

	public final static int PCG_POINTS_STRUCT_COMPONENTS = 13;
	public final static int PCG_POINTS_STRUCT_OFFSET_MASK = 12;

	public final static int PCG_POINT_MASK_VISIBLE = 0b00000001;
	public final static int PCG_POINT_MASK_ALL = PCG_POINT_MASK_VISIBLE;

	public static int retrieveMask(float[] data, int index)
	{
		return Float.floatToRawIntBits(data[index + PCG_POINTS_STRUCT_OFFSET_MASK]);
	}

	public static void applyMaskAnd(float[] data, int index, int maskAnd)
	{
		applyMask(data, index, retrieveMask(data, index) & maskAnd);
	}

	public static void applyMaskOr(float[] data, int index, int maskOr)
	{
		applyMask(data, index, retrieveMask(data, index) | maskOr);
	}

	public static void applyMask(float[] data, int index, int mask)
	{
		data[index + PCG_POINTS_STRUCT_OFFSET_MASK] = Float.intBitsToFloat(mask);
	}

	public static boolean retrieveIsVisible(float[] data, int index)
	{
		return (retrieveMask(data, index) & PCG_POINT_MASK_VISIBLE) != 0;
	}

	public static void applyIsVisible(float[] data, int index, boolean visible)
	{
		if (visible) {
			applyMaskOr(data, index, PCG_POINT_MASK_VISIBLE);
		} else {
			applyMaskAnd(data, index, PCG_POINT_MASK_ALL & ~PCG_POINT_MASK_VISIBLE);
		}
	}

	public static float retrievePositionX(float[] data, int index)
	{
		return data[index];
	}

	public static float retrievePositionY(float[] data, int index)
	{
		return data[index + 1];
	}

	public static float retrievePositionZ(float[] data, int index)
	{
		return data[index + 2];
	}

	public static Vector3f retrievePosition(float[] data, int index, Vector3f target)
	{
		target.x = retrievePositionX(data, index);
		target.y = retrievePositionY(data, index);
		target.z = retrievePositionZ(data, index);

		return target;
	}

	public static void applyPositionX(float[] data, int index, float x)
	{
		data[index] = x;
	}

	public static void applyPositionY(float[] data, int index, float y)
	{
		data[index + 1] = y;
	}

	public static void applyPositionZ(float[] data, int index, float z)
	{
		data[index + 2] = z;
	}

	public static void applyPosition(float[] data, int index, float x, float y, float z)
	{
		applyPositionX(data, index, x);
		applyPositionY(data, index, y);
		applyPositionZ(data, index, z);
	}

	protected float[] data;

	public StandardPCGPoints(int count)
	{
		assert count >= 0 : "count >= 0";

		data = new float[count * PCG_POINTS_STRUCT_COMPONENTS];
	}

	@Override
	public void process(PCGPointProcessor processor)
	{
		assert processor != null : "processor != null";

		processor.process(this, data, 0, data.length);
	}

	@Override
	public Vector3f getPosition(int index, Vector3f target)
	{
		assert index >= 0 && index < getCount() : "index >= 0 && index < getCount()";
		assert target != null : "target != null";

		return retrievePosition(data, index * PCG_POINTS_STRUCT_COMPONENTS, target);
	}

	@Override
	public int getMask(int index)
	{
		assert index >= 0 && index < getCount() : "index >= 0 && index < getCount()";

		return retrieveMask(data, index * PCG_POINTS_STRUCT_COMPONENTS);
	}

	@Override
	public int getCount()
	{
		return data.length / PCG_POINTS_STRUCT_COMPONENTS;
	}

	@Override
	public float[] getData()
	{
		return data;
	}
}
