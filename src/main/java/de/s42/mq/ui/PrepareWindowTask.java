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
package de.s42.mq.ui;

import de.s42.dl.exceptions.DLException;
import java.io.IOException;
import java.nio.IntBuffer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

/**
 *
 * @author Benjamin Schiller
 */
public class PrepareWindowTask extends AbstractWindowTask
{

	private final static Logger log = LogManager.getLogger(PrepareWindowTask.class.getName());

	protected GLFWErrorCallback errorCallback;
	protected Callback debugProc;

	@Override
	protected void runTask()
	{
		assert window != null;

		log.info("Window", window.getName(), window.getWidth(), "x", window.getHeight());

		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_FALSE);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_DEPTH_BITS, 0);
		glfwWindowHint(GLFW_STENCIL_BITS, 0);
		//glfwWindowHint(GLFW_SAMPLES, 4);

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		assert vidmode != null : "Could not retrieve video mode";

		// fullscreen
		if (window.isFullScreen()) {

			//long monitorHandle = glfwGetWindowMonitor(window.getGlfwWindowHandle());
			glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
			glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

			// if width or height = -1 -> use natural screen resolution
			if (window.getWidth() == -1 || window.getHeight() == -1) {
				window.setWidth(vidmode.width());
				window.setHeight(vidmode.height());
			}

			log.info("Starting fullscreen", window.getWidth(), window.getHeight(), vidmode.refreshRate());

			window.setGlfwWindowHandle(
				glfwCreateWindow(window.getWidth(), window.getHeight(), window.getTitle(), glfwGetPrimaryMonitor(), NULL)
			);

			// window full screen
			/*glfwSetWindowMonitor(window.getGlfwWindowHandle(), monitorHandle, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
			window.setWidth(vidmode.width());
			window.setHeight(vidmode.height());
			window.setResized(true);
			 */
		} // windowed mode
		else {

			log.info("Starting windowed", window.getWidth(), window.getHeight(), vidmode.refreshRate());

			glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
			glfwWindowHint(GLFW_DECORATED, window.isDecorated() ? GLFW_TRUE : GLFW_FALSE);

			window.setGlfwWindowHandle(
				glfwCreateWindow(window.getWidth(), window.getHeight(), window.getTitle(), NULL, NULL)
			);

			//glfwSetWindowMonitor(window.getGlfwWindowHandle(), 0, 0, 0, window.getWidth(), window.getHeight(), 60);
			glfwSetWindowPos(window.getGlfwWindowHandle(), (vidmode.width() - window.getWidth()) / 2, (vidmode.height() - window.getHeight()) / 2);
			try ( MemoryStack frame = MemoryStack.stackPush()) {
				IntBuffer framebufferSize = frame.mallocInt(2);
				nglfwGetFramebufferSize(window.getGlfwWindowHandle(), memAddress(framebufferSize), memAddress(framebufferSize) + 4);
				window.setSize(framebufferSize.get(0), framebufferSize.get(1));
			}
		}

		window.setResized(true);

		if (window.getGlfwWindowHandle() == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		try {
			//load icon
			glfwSetWindowIcon(window.getGlfwWindowHandle(), window.getImageBufferFromIcon());
		} catch (IOException ex) {
			throw new RuntimeException("Error loading icon - " + ex.getMessage(), ex);
		}

		glfwMakeContextCurrent(window.getGlfwWindowHandle());

		if (window.isvSync()) {
			glfwSwapInterval(1);
		} else {
			glfwSwapInterval(0);
		}

		try {
			window.load();
		} catch (DLException ex) {
			throw new RuntimeException(ex);
		}

		if (window.getDefaultCursor() != null) {
			window.setCursor(window.getDefaultCursor());
		}

		glfwShowWindow(window.getGlfwWindowHandle());

		glfwMakeContextCurrent(0);

		// @todo this causes the app NOT to exit - find out why some day
		/*
		debugProc = GLUtil.setupDebugMessageCallback();		
		if (debugProc != null) {
			glDebugMessageCallback(null, 0L);
			//debugProc.free();
			debugProc = null;
		}*/
	}
}
