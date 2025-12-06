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

	public final static int PCG_POINTS_STRUCT_COMPONENTS = 4;

	public final static int PCG_POINT_MASK_VISIBLE = 0b00000001;

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
		assert index >= 0 && index < data.length / PCG_POINTS_STRUCT_COMPONENTS : "index >= 0 && index < data.length / PCG_POINTS_STRUCT_COMPONENTS";
		assert target != null : "target != null";

		target.x = data[index * PCG_POINTS_STRUCT_COMPONENTS];
		target.y = data[index * PCG_POINTS_STRUCT_COMPONENTS + 1];
		target.z = data[index * PCG_POINTS_STRUCT_COMPONENTS + 2];

		return target;
	}

	@Override
	public int getMask(int index)
	{
		assert index >= 0 && index < data.length / PCG_POINTS_STRUCT_COMPONENTS : "index >= 0 && index < data.length / PCG_POINTS_STRUCT_COMPONENTS";

		return Float.floatToIntBits(data[index * PCG_POINTS_STRUCT_COMPONENTS + 3]);
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
