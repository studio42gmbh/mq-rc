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
package de.s42.mq.dl;

import de.s42.dl.DLInstance;
import de.s42.dl.DLModule;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.scenes.Scenes;
import de.s42.mq.ui.Window;
import de.s42.mq.tasks.Task;
import de.s42.mq.tasks.TaskManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class MQDLConfig
{

	private final MQDLCore core;
	private final DLModule config;

	public final static String WINDOW_INSTANCE_IDENTIFIER = "window";
	public final static String TASKS_INSTANCE_IDENTIFIER = "tasks";
	public final static String TASK_MANAGER_INSTANCE_IDENTIFIER = "taskManager";
	public final static String SCENES_INSTANCE_IDENTIFIER = "scenes";

	public MQDLConfig(MQDLCore core, DLModule config)
	{
		assert core != null;
		assert config != null;

		this.core = core;
		this.config = config;
	}

	public List<Task> getTasks() throws DLException
	{
		DLInstance tasksContainer = core.getExported(TASKS_INSTANCE_IDENTIFIER).get();

		List<DLInstance> tasksInstances = tasksContainer.getChildren(getCore().getTaskType());

		List<Task> tasks = new ArrayList<>(tasksInstances.size());

		for (DLInstance taskInstance : tasksInstances) {
			tasks.add((Task)taskInstance.toJavaObject());
		}

		return Collections.unmodifiableList(tasks);
	}

	public TaskManager getTaskManager()
	{
		return (TaskManager)core.getExported(TASK_MANAGER_INSTANCE_IDENTIFIER).get().toJavaObject();
	}

	public Scenes getScenes()
	{
		return (Scenes)core.getExported(SCENES_INSTANCE_IDENTIFIER).get().toJavaObject();
	}

	public Window getWindow()
	{
		return (Window)core.getExported(WINDOW_INSTANCE_IDENTIFIER).get().toJavaObject();
	}

	public MQDLCore getCore()
	{
		return core;
	}

	public DLModule getConfig()
	{
		return config;
	}
}
