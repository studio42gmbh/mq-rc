/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.mq.util;

import de.s42.base.files.FilesHelper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import org.lwjgl.glfw.GLFWImage;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Benjamin Schiller
 */
public final class GLFWHelper
{
	private GLFWHelper()
	{

	}

	public static GLFWImage.Buffer getImageBufferFromImagePath(Path filePath) throws IOException
	{
		// See as info https://discourse.glfw.org/t/set-window-icon-in-lwjgl-3-1/863/3
		GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
		GLFWImage image = GLFWImage.malloc();
		ByteBuffer icon;
		try (MemoryStack frame = MemoryStack.stackPush()) {
			IntBuffer widthB = frame.mallocInt(1);
			IntBuffer heightB = frame.mallocInt(1);
			IntBuffer components = frame.mallocInt(1);

			icon = stbi_load_from_memory(
				FilesHelper.getFileAsMappedByteBuffer(filePath),
				widthB, heightB, components, 4);

			if (icon == null) {
				throw new IllegalArgumentException("Could not load icon resources " + filePath);
			}

			image.set(widthB.get(), heightB.get(), icon);
			imagebf.put(0, image);
		}
		return imagebf;
	}

	public static GLFWImage getImageFromImagePath(Path filePath) throws IOException
	{
		// See as info https://discourse.glfw.org/t/set-window-icon-in-lwjgl-3-1/863/3
		GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
		GLFWImage image = GLFWImage.malloc();
		ByteBuffer buffer;
		try (MemoryStack frame = MemoryStack.stackPush()) {
			IntBuffer widthB = frame.mallocInt(1);
			IntBuffer heightB = frame.mallocInt(1);
			IntBuffer components = frame.mallocInt(1);

			buffer = stbi_load_from_memory(
				FilesHelper.getFileAsMappedByteBuffer(filePath),
				widthB, heightB, components, 4);

			if (buffer == null) {
				throw new IllegalArgumentException("Could not load icon resources " + filePath);
			}

			image.set(widthB.get(), heightB.get(), buffer);
			imagebf.put(0, image);
		}
		return image;
	}

}
