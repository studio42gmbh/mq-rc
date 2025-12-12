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
package de.s42.mq.pcg.voxels;

import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardPCGVoxels implements PCGVoxels
{

	public static void applyValue(int[] data, int componentIndex, int value)
	{
		data[componentIndex] = value;
	}

	public static int retrieveValue(int[] data, int componentIndex)
	{
		return data[componentIndex];
	}

	public static int retrieveOffsetValue(StandardPCGVoxels voxels, int[] data, int componentIndex, int offX, int offY, int offZ)
	{
		int offset = offX * voxels.height * voxels.depth + offY * voxels.depth + offZ;

		return data[componentIndex + offset];
	}

	protected final int[] data;
	protected final int width;
	protected final int height;
	protected final int depth;
	protected final int count;
	protected final Vector3f origin;

	public StandardPCGVoxels(int width, int height, int depth, Vector3f origin)
	{
		assert width > 0 : "width > 0";
		assert height > 0 : "height > 0";
		assert depth > 0 : "depth > 0";
		assert origin != null : "origin != null";

		this.count = width * height * depth;
		this.data = new int[count];
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.origin = new Vector3f(origin);
	}

	@Override
	public void process(PCGVoxelProcessor processor)
	{
		assert processor != null : "processor != null";

		processor.process(this, data);
	}

	@Override
	public void process(PCGVoxelPositionProcessor processor)
	{
		assert processor != null : "processor != null";

		int index = 0;
		for (float x = 0.0f; x < width; x += 1.0f) {
			for (float y = 0.0f; y < height; y += 1.0f) {
				for (float z = 0.0f; z < depth; z += 1.0f) {
					processor.process(data, x, y, z, index);
					index++;
				}
			}
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	@Override
	public int[] getData()
	{
		return data;
	}

	@Override
	public int getCount()
	{
		return width * height * depth;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public int getDepth()
	{
		return depth;
	}

	@Override
	public int get(Vector3i position)
	{
		assert position != null : "position != null";

		return get(position.x, position.y, position.z);
	}

	@Override
	public int get(int x, int y, int z)
	{
		return data[getIndex(x, y, z)];
	}

	@Override
	public int getIndex(Vector3i position)
	{
		assert position != null : "position != null";

		return getIndex(position.x, position.y, position.z);
	}

	@Override
	public int getIndex(int x, int y, int z)
	{
		assert x >= 0 && x < width : "x >= 0 && x < width";
		assert y >= 0 && y < height : "y >= 0 && y < height";
		assert z >= 0 && z < depth : "z >= 0 && z < depth";

		return x * height * depth + y * depth + z;
	}

	@Override
	public Vector3f getOrigin()
	{
		return origin;
	}

	@Override
	public void set(Vector3i position, int value)
	{
		assert position != null : "position != null";

		set(position.x, position.y, position.z, value);
	}

	@Override
	public void set(int x, int y, int z, int value)
	{
		set(getIndex(x, y, z), value);
	}

	@Override
	public void set(int index, int value)
	{
		assert index >= 0 && index < data.length : "index >= 0 && index < data.length";

		data[index] = value;
	}
	// "Getters/Setters" </editor-fold>
}
