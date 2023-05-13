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
package de.s42.mq.input;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.ui.AbstractWindowTask;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 *
 * @author Benjamin Schiller
 */
public class PrepareInputTask extends AbstractWindowTask implements InputTask
{

	private final static Logger log = LogManager.getLogger(PrepareInputTask.class.getName());

	@AttributeDL(required = true)
	protected Input input;

	protected GLFWKeyCallback keyCallback;
	protected GLFWCursorPosCallback cursorCallback;
	protected GLFWMouseButtonCallback mouseButtonCallback;
	protected GLFWScrollCallback scrollCallback;

	@Override
	protected void runTask()
	{
		assert window != null;

		glfwSetKeyCallback(window.getGlfwWindowHandle(), keyCallback = new GLFWKeyCallback()
		{
			@Override
			public void invoke(long windowHandle, int key, int scancode, int action, int mods)
			{
				// @todo later map to glfw independent keys?
				input.handleKey(key, scancode, action, mods);

				/*
				if (action == GLFW_PRESS) {
					log.debug("Press {} {}", key, scancode);
				}

				if (action == GLFW_RELEASE) {
					log.debug("Release {} {}", key, scancode);
				}
				 */
			}
		});

		glfwSetCursorPosCallback(window.getGlfwWindowHandle(), cursorCallback = new GLFWCursorPosCallback()
		{
			@Override
			public void invoke(long window, double xpos, double ypos)
			{
				input.handleMouseCursor(xpos, ypos);

				//log.debug("Mouse {} {}", xpos, ypos);
			}
		});

		glfwSetMouseButtonCallback(window.getGlfwWindowHandle(), mouseButtonCallback = new GLFWMouseButtonCallback()
		{
			@Override
			public void invoke(long window, int button, int action, int mods)
			{
				input.handleMouseButton(button, action, mods);

				//log.debug("glfwSetMouseButtonCallback {} {} {}", button, action, mods);
			}
		});

		glfwSetScrollCallback(window.getGlfwWindowHandle(), scrollCallback = new GLFWScrollCallback()
		{
			@Override
			public void invoke(long window, double xoffset, double yoffset)
			{
				input.handleScroll(xoffset, yoffset);

				//log.debug("glfwSetScrollCallback {} {}", xoffset, yoffset);
			}
		});
	}

	@Override
	public Input getInput()
	{
		return input;
	}

	@Override
	public void setInput(Input input)
	{
		this.input = input;
	}
}
