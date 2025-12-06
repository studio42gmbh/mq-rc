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

import static de.s42.mq.pcg.points.StandardPCGPoints.PCG_POINTS_STRUCT_COMPONENTS;
import static de.s42.mq.pcg.points.StandardPCGPoints.PCG_POINT_MASK_VISIBLE;
import de.s42.mq.util.AABB;
import de.s42.mq.util.MQRandom;

/**
 *
 * @author Benjamin Schiller
 */
public class RandomizePointPositions implements PCGPointProcessor
{

	protected final MQRandom random;
	protected final AABB randomBounds;

	public RandomizePointPositions(MQRandom random, AABB randomBounds)
	{
		assert random != null : "random != null";
		assert randomBounds != null : "randomBounds != null";

		this.random = random;
		this.randomBounds = randomBounds;
	}

	@Override
	public void process(PCGPoints points, float[] data, int startIndex, int endIndex)
	{
		assert data != null : "data != null";
		assert startIndex <= endIndex : "startIndex <= endIndex";
		assert startIndex >= 0 && startIndex <= data.length : "startIndex >= 0 && startIndex < data.length";
		assert endIndex >= 0 && endIndex <= data.length : "endIndex >= 0 && endIndex < data.length";

		float minX = randomBounds.min.x;
		float maxX = randomBounds.max.x;
		float minY = randomBounds.min.y;
		float maxY = randomBounds.max.y;
		float minZ = randomBounds.min.z;
		float maxZ = randomBounds.max.z;

		for (int i = startIndex; i < endIndex; i += PCG_POINTS_STRUCT_COMPONENTS) {
			data[i] = random.nextFloat(minX, maxX);
			data[i + 1] = random.nextFloat(minY, maxY);
			data[i + 2] = random.nextFloat(minZ, maxZ);
			data[i + 3] = Float.intBitsToFloat(PCG_POINT_MASK_VISIBLE);
		}
	}
}
