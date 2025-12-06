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
import de.s42.mq.pcg.points.StandardPCGPoints;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 *
 * @author Benjamin Schiller
 */
public class ImageColorPointProcessor implements PCGPointProcessor
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(ImageColorPointProcessor.class.getName());

	protected PCGImage image;
	protected Vertex32Transform transform;
	protected ImageColorPointProcessorFunction function;

	public ImageColorPointProcessor()
	{
	}

	/**
	 *
	 * @param image Sampler to read data from
	 * @param transform Converts the point space into image space
	 * @param function
	 */
	public ImageColorPointProcessor(PCGImage image, Vertex32Transform transform, ImageColorPointProcessorFunction function)
	{
		assert image != null : "image != null";
		assert transform != null : "transform != null";
		assert function != null : "function != null";

		this.image = image;
		this.transform = transform;
		this.function = function;
	}

	@Override
	public void process(PCGPoints points, float[] data, int startIndex, int endIndex)
	{
		assert data != null : "data != null";
		assert startIndex <= endIndex : "startIndex <= endIndex";
		assert startIndex >= 0 && startIndex <= data.length : "startIndex >= 0 && startIndex < data.length";
		assert endIndex >= 0 && endIndex <= data.length : "endIndex >= 0 && endIndex < data.length";

		int componentSize = points.getComponentSize();
		for (int i = startIndex; i < endIndex; i += componentSize) {

			float x = StandardPCGPoints.retrievePositionX(data, i);
			float y = StandardPCGPoints.retrievePositionY(data, i);
			float z = StandardPCGPoints.retrievePositionZ(data, i);

			Vector2f transformed = transform.transform(x, y, z, new Vector2f());

			Vector4f color = image.getRGBA(transformed.x, transformed.y, new Vector4f());

			function.process(data, i, color.x, color.y, color.z, color.w);
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

	public ImageColorPointProcessorFunction getFunction()
	{
		return function;
	}

	public void setFunction(ImageColorPointProcessorFunction function)
	{
		this.function = function;
	}
}
