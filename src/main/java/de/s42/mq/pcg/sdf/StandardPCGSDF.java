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
package de.s42.mq.pcg.sdf;

import de.s42.mq.util.MQMath;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardPCGSDF implements PCGSDF
{

	protected final float[] data;
	protected final int componentSize;
	protected int count;

	// @todo Simple hacky optimization to create buckets
	private final static class Chunk
	{

		public int count;
		public float[] data;
	}

	private final float chunkSize = 16.0f;
	private final Map<Integer, Chunk> chunks = new HashMap<>();

	public StandardPCGSDF(int elementCount)
	{
		assert elementCount > 0 : "elementCount > 0";

		// x,y,z,r
		componentSize = 4;

		data = new float[elementCount * componentSize];

	}

	@Override
	public float getMin(Vector3f position)
	{
		assert position != null : "position != null";

		return getMin(position.x, position.y, position.z);
	}

	@Override
	public float getMin(float x, float y, float z)
	{
		// @todo Simple hacky optimization to create buckets
		int xC = (int) (x / chunkSize);
		int yC = (int) (y / chunkSize);
		int zC = (int) (z / chunkSize);

		int chunkId = (xC + 32) * 64 * 64 + (yC + 32) * 64 + (zC + 32);

		Chunk chunk = chunks.get(chunkId);

		if (chunk == null) {
			return chunkSize;
		}

		float sqDistance = Float.MAX_VALUE;
		for (int i = 0; i < chunk.count * componentSize; i += componentSize) {
			float dX = chunk.data[i] - x;
			float dY = chunk.data[i + 1] - y;
			float dZ = chunk.data[i + 2] - z;
			float sSQR = chunk.data[i + 3];

			float sqD = dX * dX + dY * dY + dZ * dZ;

			if (sqD < sSQR) {
				return 0.0f;
			}

			sqDistance = Math.min(sqDistance, sqD - sSQR);
		}

		return MQMath.sqrt(sqDistance);

		/*
		float sqDistance = Float.MAX_VALUE;
		for (int i = 0; i < count * componentSize; i += componentSize) {
			float dX = data[i] - x;
			float dY = data[i + 1] - y;
			float dZ = data[i + 2] - z;
			float sSQR = data[i + 3];

			float sqD = dX * dX + dY * dY + dZ * dZ;

			if (sqD < sSQR) {
				return 0.0f;
			}

			sqDistance = Math.min(sqDistance, sqD - sSQR);
		}

		return MQMath.sqrt(sqDistance);
		 */
	}

	@Override
	public void addSphere(float x, float y, float z, float radius)
	{
		assert count * componentSize <= data.length - componentSize : "count * componentSize <= data.length - componentSize";

		// @todo Simple hacky optimization to create buckets
		int minXC = (int) ((x - radius * radius) / chunkSize);
		int maxXC = (int) ((x + radius * radius) / chunkSize);
		int minYC = (int) ((y - radius * radius) / chunkSize);
		int maxYC = (int) ((y + radius * radius) / chunkSize);
		int minZC = (int) ((z - radius * radius) / chunkSize);
		int maxZC = (int) ((z + radius * radius) / chunkSize);

		for (int xC = minXC; xC < maxXC; xC++) {
			for (int yC = minYC; yC < maxYC; yC++) {
				for (int zC = minZC; zC < maxZC; zC++) {

					int chunkId = (xC + 32) * 64 * 64 + (yC + 32) * 64 + (zC + 32);

					Chunk chunk = chunks.get(chunkId);

					if (chunk == null) {
						chunk = new Chunk();
						chunk.data = new float[data.length];
						chunks.put(chunkId, chunk);
					}

					int chunkIndex = chunk.count * componentSize;
					chunk.data[chunkIndex] = x;
					chunk.data[chunkIndex + 1] = y;
					chunk.data[chunkIndex + 2] = z;
					chunk.data[chunkIndex + 3] = radius * radius;
					chunk.count++;
				}
			}
		}

		/*
		int i = count * componentSize;
		data[i] = x;
		data[i + 1] = y;
		data[i + 2] = z;
		data[i + 3] = radius * radius;
		count++;
		 */
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	// "Getters/Setters" </editor-fold>
}
