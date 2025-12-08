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

import de.s42.base.files.FilesHelper;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.joml.Vector4f;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;

/**
 * @todo this is just a test - implement properly
 * @author Benjamin Schiller
 */
public class StandardPCGImage implements PCGImage
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(StandardPCGImage.class.getName());

	protected String id;
	protected static int width;
	protected static int height;
	protected static float[] data;

	public StandardPCGImage()
	{
	}

	public StandardPCGImage(String id)
	{
		assert id != null : "id != null";

		this.id = id;
	}

	@Override
	public Vector4f getRGBA(float x, float y, Vector4f target)
	{
		assert target != null : "target != null";

		if (data == null) {
			return target;
		}

		return getRGBA((int) (x * (float) (width - 1)), (int) (y * (float) (height - 1)), target);

	}

	@Override
	public Vector4f getRGBA(int x, int y, Vector4f target)
	{
		assert target != null : "target != null";

		if (data == null) {
			return target;
		}

		int index = (x + y * width) << 2;

		target.x = data[index];
		target.y = data[index + 1];
		target.z = data[index + 2];
		target.w = data[index + 3];

		return target;
	}

	public synchronized void load()
	{
		// @todo add proper caching
		if (data != null) {
			return;
		}

		log.info("loading", id);

		// Load height map to memory
		try (MemoryStack frame = MemoryStack.stackPush()) {

			IntBuffer widthB = frame.mallocInt(1);
			IntBuffer heightB = frame.mallocInt(1);
			IntBuffer componentsB = frame.mallocInt(1);

			ByteBuffer sourceData = FilesHelper.getFileAsMappedByteBuffer(id);

			ByteBuffer buffer = stbi_load_from_memory(sourceData, widthB, heightB, componentsB, 4);

			assert buffer != null : "buffer != null";

			// @todo add proper loading
			width = widthB.get();
			height = heightB.get();
			//int components = componentsB.get();

			data = new float[width * height << 2];

			int i = 0;
			while (buffer.hasRemaining() && i < data.length) {
				data[i] = ((float) Byte.toUnsignedInt(buffer.get())) / 255.0f;
				data[i + 1] = ((float) Byte.toUnsignedInt(buffer.get())) / 255.0f;
				data[i + 2] = ((float) Byte.toUnsignedInt(buffer.get())) / 255.0f;
				data[i + 3] = ((float) Byte.toUnsignedInt(buffer.get())) / 255.0f;

				/*
				if (i % 10000 == 0) {
					log.debug(data[i], data[i + 1], data[i + 2], data[i + 3]);
				}
				 */
				i += 4;
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}
}
