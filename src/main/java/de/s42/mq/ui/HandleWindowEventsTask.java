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

import de.s42.log.LogManager;
import de.s42.log.Logger;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

/**
 *
 * @author Benjamin Schiller
 */
public class HandleWindowEventsTask extends AbstractWindowTask
{

	private final static Logger log = LogManager.getLogger(HandleWindowEventsTask.class.getName());

	protected GLFWFramebufferSizeCallback fsCallback;

	@Override
	@SuppressWarnings("SynchronizeOnNonFinalField")
	protected void runTask()
	{
		assert window != null;

		log.debug("Start handle window events");

		glfwSetFramebufferSizeCallback(window.getGlfwWindowHandle(), fsCallback = new GLFWFramebufferSizeCallback()
		{
			@Override
			public void invoke(long glfwWindowHandle, int w, int h)
			{
				if (w > 0 && h > 0) {
					window.setSize(w, h);
				}
			}
		});

		while (!window.shouldClose()) {

			glfwWaitEventsTimeout(0.1);
		}
		
		log.debug("Stopped handle window events");

		/*
		fsCallback.free();
		fsCallback = null;		
		 */
	}
}
