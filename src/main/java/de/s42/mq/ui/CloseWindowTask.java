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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.tasks.LongRunningTask;
import de.s42.mq.tasks.TaskManager;
import java.util.Objects;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import org.lwjgl.glfw.Callbacks;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;

/**
 *
 * @author Benjamin Schiller
 */
public class CloseWindowTask extends AbstractWindowTask implements LongRunningTask
{

	private final static Logger log = LogManager.getLogger(CloseWindowTask.class.getName());

	@AttributeDL(required = true)
	private TaskManager taskManager;

	@Override
	protected void runTask()
	{
		assert window != null;
		assert taskManager != null;

		log.info("Closing Window");

		glfwMakeContextCurrent(window.getGlfwWindowHandle());
		window.requestCloseWindow();
		GL.setCapabilities(null);
		Callbacks.glfwFreeCallbacks(window.getGlfwWindowHandle());
		glfwDestroyWindow(window.getGlfwWindowHandle());
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
		glfwMakeContextCurrent(0);
	}

	public TaskManager getTaskManager()
	{
		return taskManager;
	}

	public void setTaskManager(TaskManager taskManager)
	{
		this.taskManager = taskManager;
	}
}
