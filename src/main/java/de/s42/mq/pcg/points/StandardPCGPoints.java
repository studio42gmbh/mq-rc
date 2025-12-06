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
public final class StandardPCGPoints implements PCGPoints
{

	public final static int PCG_POINTS_STRUCT_DEFAULT_COMPONENTS = 4;
	public final static int PCG_POINTS_STRUCT_OFFSET_POSITION = 0;
	public final static int PCG_POINTS_STRUCT_OFFSET_MASK = 3;

	public final static int PCG_POINT_MASK_VISIBLE = 0b00000001;
	public final static int PCG_POINT_MASK_ALL = PCG_POINT_MASK_VISIBLE;

	public static int retrieveMask(float[] data, int componentIndex)
	{
		return Float.floatToRawIntBits(data[componentIndex + PCG_POINTS_STRUCT_OFFSET_MASK]);
	}

	public static void applyMaskAnd(float[] data, int componentIndex, int maskAnd)
	{
		applyMask(data, componentIndex, retrieveMask(data, componentIndex) & maskAnd);
	}

	public static void applyMaskOr(float[] data, int componentIndex, int maskOr)
	{
		applyMask(data, componentIndex, retrieveMask(data, componentIndex) | maskOr);
	}

	public static void applyMask(float[] data, int componentIndex, int mask)
	{
		data[componentIndex + PCG_POINTS_STRUCT_OFFSET_MASK] = Float.intBitsToFloat(mask);
	}

	public static boolean retrieveIsVisible(float[] data, int componentIndex)
	{
		return (retrieveMask(data, componentIndex) & PCG_POINT_MASK_VISIBLE) != 0;
	}

	public static void applyIsVisible(float[] data, int componentIndex, boolean visible)
	{
		if (visible) {
			applyMaskOr(data, componentIndex, PCG_POINT_MASK_VISIBLE);
		} else {
			applyMaskAnd(data, componentIndex, PCG_POINT_MASK_ALL & ~PCG_POINT_MASK_VISIBLE);
		}
	}

	public static float retrievePositionX(float[] data, int componentIndex)
	{
		return data[componentIndex + PCG_POINTS_STRUCT_OFFSET_POSITION];
	}

	public static float retrievePositionY(float[] data, int componentIndex)
	{
		return data[componentIndex + PCG_POINTS_STRUCT_OFFSET_POSITION + 1];
	}

	public static float retrievePositionZ(float[] data, int componentIndex)
	{
		return data[componentIndex + PCG_POINTS_STRUCT_OFFSET_POSITION + 2];
	}

	public static Vector3f retrievePosition(float[] data, int componentIndex, Vector3f target)
	{
		target.x = retrievePositionX(data, componentIndex);
		target.y = retrievePositionY(data, componentIndex);
		target.z = retrievePositionZ(data, componentIndex);

		return target;
	}

	public static void applyPositionX(float[] data, int componentIndex, float x)
	{
		data[componentIndex + PCG_POINTS_STRUCT_OFFSET_POSITION] = x;
	}

	public static void applyPositionY(float[] data, int componentIndex, float y)
	{
		data[componentIndex + PCG_POINTS_STRUCT_OFFSET_POSITION + 1] = y;
	}

	public static void applyPositionZ(float[] data, int componentIndex, float z)
	{
		data[componentIndex + PCG_POINTS_STRUCT_OFFSET_POSITION + 2] = z;
	}

	public static void applyPosition(float[] data, int componentIndex, float x, float y, float z)
	{
		applyPositionX(data, componentIndex, x);
		applyPositionY(data, componentIndex, y);
		applyPositionZ(data, componentIndex, z);
	}

	/**
	 *
	 * @param data
	 * @param componentIndex
	 * @param extensionOffset offset of extension component (0 = first extension component)
	 * @return
	 */
	public static float retrieveExtendedComponent(float[] data, int componentIndex, int extensionOffset)
	{
		return data[componentIndex + PCG_POINTS_STRUCT_DEFAULT_COMPONENTS + extensionOffset];
	}

	/**
	 *
	 * @param data
	 * @param componentIndex index of element in component space (elementNr * componentSize)
	 * @param extensionOffset offset of extension component (0 = first extension component)
	 * @param value
	 */
	public static void applyExtendedComponent(float[] data, int componentIndex, int extensionOffset, float value)
	{
		data[componentIndex + PCG_POINTS_STRUCT_DEFAULT_COMPONENTS + extensionOffset] = value;
	}

	protected final float[] data;
	protected final int componentSize;

	public StandardPCGPoints(int count)
	{
		assert count >= 0 : "count >= 0";

		componentSize = PCG_POINTS_STRUCT_DEFAULT_COMPONENTS;

		data = new float[count * componentSize];
	}

	public StandardPCGPoints(int count, int additionalComponentSize)
	{
		assert count >= 0 : "count >= 0";
		assert additionalComponentSize >= 0 : "additionalComponentSize >= 0";

		componentSize = PCG_POINTS_STRUCT_DEFAULT_COMPONENTS + additionalComponentSize;

		data = new float[count * componentSize];
	}

	@Override
	public int getComponentSize()
	{
		return componentSize;
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

		return retrievePosition(data, index * componentSize, target);
	}

	@Override
	public int getMask(int index)
	{
		assert index >= 0 && index < getCount() : "index >= 0 && index < getCount()";

		return retrieveMask(data, index * componentSize);
	}

	@Override
	public int getCount()
	{
		return data.length / componentSize;
	}

	@Override
	public float[] getData()
	{
		return data;
	}
}
