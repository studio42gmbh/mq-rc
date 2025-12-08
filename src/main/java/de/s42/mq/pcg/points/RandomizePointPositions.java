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

import de.s42.mq.util.AABB;
import de.s42.mq.util.MQRandom;

/**
 *
 * @author Benjamin Schiller
 */
public class RandomizePointPositions implements PCGPointProcessor, PCGSubPointProcessor
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
	public void process(PCGPoints points, float[] data, int startIndex, int endIndex, int step)
	{
		assert data != null : "data != null";
		assert startIndex >= 0 && startIndex <= points.getCount() : "startIndex >= 0 && startIndex < points.getCount()";
		assert endIndex >= 0 && endIndex <= points.getCount() : "endIndex >= 0 && endIndex < points.getCount()";
		assert startIndex <= endIndex : "startIndex <= endIndex";

		int componentSize = points.getComponentSize();

		float minX = randomBounds.min.x;
		float maxX = randomBounds.max.x;
		float minY = randomBounds.min.y;
		float maxY = randomBounds.max.y;
		float minZ = randomBounds.min.z;
		float maxZ = randomBounds.max.z;

		int componentStepSize = componentSize * step;
		for (int i = startIndex * componentSize; i < endIndex * componentSize; i += componentStepSize) {
			StandardPCGPoints.applyPosition(data, i, random.nextFloat(minX, maxX), random.nextFloat(minY, maxY), random.nextFloat(minZ, maxZ));
		}
	}

	@Override
	public void process(PCGPoints points, float[] data, int parentIndex, int startIndex, int endIndex, int step)
	{
		assert data != null : "data != null";
		assert startIndex >= 0 && startIndex <= points.getCount() : "startIndex >= 0 && startIndex < points.getCount()";
		assert endIndex >= 0 && endIndex <= points.getCount() : "endIndex >= 0 && endIndex < points.getCount()";
		assert startIndex <= endIndex : "startIndex <= endIndex";

		int componentSize = points.getComponentSize();

		int parentComponentIndex = parentIndex * componentSize;
		float pX = StandardPCGPoints.retrievePositionX(data, parentComponentIndex);
		float pY = StandardPCGPoints.retrievePositionY(data, parentComponentIndex);
		float pZ = StandardPCGPoints.retrievePositionZ(data, parentComponentIndex);

		float minX = randomBounds.min.x + pX;
		float maxX = randomBounds.max.x + pX;
		float minY = randomBounds.min.y + pY;
		float maxY = randomBounds.max.y + pY;
		float minZ = randomBounds.min.z + pZ;
		float maxZ = randomBounds.max.z + pZ;

		int componentStepSize = componentSize * step;
		for (int i = startIndex * componentSize; i < endIndex * componentSize; i += componentStepSize) {
			StandardPCGPoints.applyPosition(data, i, random.nextFloat(minX, maxX), random.nextFloat(minY, maxY), random.nextFloat(minZ, maxZ));
		}
	}
}
