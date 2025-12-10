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
package de.s42.mq.pcg;

import de.s42.mq.pcg.images.PCGImage;
import de.s42.mq.pcg.images.StandardPCGImage;
import de.s42.mq.pcg.points.PCGPoints;
import de.s42.mq.pcg.points.StandardPCGPoints;
import de.s42.mq.pcg.sdf.PCGSDF;
import de.s42.mq.pcg.sdf.StandardPCGSDF;
import de.s42.mq.pcg.voxels.PCGVoxels;
import de.s42.mq.pcg.voxels.StandardPCGVoxels;
import de.s42.mq.util.AABB;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 *
 * @author Benjamin Schiller
 */
public class StandardPCGContext implements PCGContext
{

	protected AABB bounds;

	public StandardPCGContext()
	{
	}

	@Override
	public PCGImage loadImage(String id)
	{
		assert id != null : "id != null";

		// @todo
		StandardPCGImage image = new StandardPCGImage(id);
		image.load();

		return image;
	}

	@Override
	public PCGPoints createPoints(int count)
	{
		assert count >= 0 : "count >= 0";

		return new StandardPCGPoints(count);
	}

	@Override
	public PCGPoints createPoints(int count, int extendedComponentSize)
	{
		assert count >= 0 : "count >= 0";
		assert extendedComponentSize >= 0 : "extendedComponentSize >= 0";

		return new StandardPCGPoints(count, extendedComponentSize);
	}

	@Override
	public PCGVoxels createVoxels(Vector3i dimension, Vector3f origin)
	{
		return createVoxels(dimension.x, dimension.y, dimension.z, origin);
	}

	@Override
	public PCGVoxels createVoxels(int width, int height, int depth, Vector3f origin)
	{
		return new StandardPCGVoxels(width, height, depth, origin);
	}

	@Override
	public PCGSDF createSDF(int count)
	{
		return new StandardPCGSDF(count);
	}

	@Override
	public int getBoundsId()
	{
		return bounds.hashCode();
	}

	@Override
	public AABB getBounds()
	{
		return bounds;
	}

	public void setBounds(AABB bounds)
	{
		this.bounds = bounds;
	}
}
