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
package de.s42.mq.pcg.images;

import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.pcg.Vertex32Transform;
import de.s42.mq.pcg.points.PCGPointProcessor;
import de.s42.mq.pcg.points.PCGPoints;
import static de.s42.mq.pcg.points.StandardPCGPoints.PCG_POINTS_STRUCT_COMPONENTS;
import static de.s42.mq.pcg.points.StandardPCGPoints.PCG_POINT_MASK_VISIBLE;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 *
 * @author Benjamin Schiller
 */
public class ImageColorPointFilter implements PCGPointProcessor
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(ImageColorPointFilter.class.getName());

	protected PCGImage image;
	protected Vertex32Transform transform;
	protected ImageColorPointFilterFunction filter;

	public ImageColorPointFilter()
	{
	}

	public ImageColorPointFilter(PCGImage image, Vertex32Transform transform, ImageColorPointFilterFunction filter)
	{
		assert image != null : "image != null";
		assert transform != null : "transform != null";
		assert filter != null : "filter != null";

		this.image = image;
		this.transform = transform;
		this.filter = filter;
	}

	@Override
	public void process(PCGPoints points, float[] data, int startIndex, int endIndex)
	{
		assert data != null : "data != null";
		assert startIndex <= endIndex : "startIndex <= endIndex";
		assert startIndex >= 0 && startIndex <= data.length : "startIndex >= 0 && startIndex < data.length";
		assert endIndex >= 0 && endIndex <= data.length : "endIndex >= 0 && endIndex < data.length";

		for (int i = startIndex; i < endIndex; i += PCG_POINTS_STRUCT_COMPONENTS) {

			float x = data[i];
			float y = data[i + 1];
			float z = data[i + 2];

			Vector2f transformed = transform.transform(x, y, z, new Vector2f());

			Vector4f color = image.getRGBA(transformed.x, transformed.y, new Vector4f());

			// Set Mask VISIBLE to 1 if accepted to 0 otherwise
			int mask = Float.floatToRawIntBits(data[i + 3]);
			mask = (filter.accept(color.x, color.y, color.z, color.w)) ? (mask & PCG_POINT_MASK_VISIBLE) : (mask & ~PCG_POINT_MASK_VISIBLE);
			data[i + 3] = Float.intBitsToFloat(mask);

			//log.debug("pos", x, y, z, "tr", transformed.x, transformed.y, "col", color.x, color.y, color.z, color.w, "mask", mask);
		}
	}

	public PCGImage getImage()
	{
		return image;
	}

	public void setImage(PCGImage image)
	{
		this.image = image;
	}

	public Vertex32Transform getTransform()
	{
		return transform;
	}

	public void setTransform(Vertex32Transform transform)
	{
		this.transform = transform;
	}

	public ImageColorPointFilterFunction getFilter()
	{
		return filter;
	}

	public void setFilter(ImageColorPointFilterFunction filter)
	{
		this.filter = filter;
	}
}
