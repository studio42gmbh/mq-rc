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
package de.s42.mq.scenes;

import de.s42.dl.exceptions.InvalidModule;
import de.s42.dl.exceptions.InvalidInstance;
import de.s42.dl.*;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.data.PathData;
import de.s42.mq.tasks.Task;
import de.s42.mq.tasks.Worker;
import java.util.Optional;

/**
 *
 * @author Benjamin Schiller
 */
public class Scene extends AbstractAsset implements Task
{

	public final static String SCENE_RUN_TASK_PATH = "$scene.run";

	@AttributeDL(required = true)
	//@AnnotationDL(value = IsFileDLAnnotation.DEFAULT_SYMBOL)
	protected PathData source;

	/*
	 * is required if you want to call load and run directly
	 */
	@AttributeDL(required = false)
	protected DLCore core;

	protected Task runTask;
	protected Worker worker;

	@Override
	@SuppressWarnings("UseSpecificCatch")
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		assert core != null;
		assert source != null;

		super.load();

		try {
			DLModule scene = core.parse(getSource().getValue().toString());

			Optional runTaskInstance = core.getReferenceResolver().resolve(scene, SCENE_RUN_TASK_PATH);

			if (runTaskInstance.isEmpty()) {
				throw new InvalidModule("Scene module has to contain path " + SCENE_RUN_TASK_PATH);
			}

			Object taskObject = ((DLInstance) runTaskInstance.orElseThrow()).toJavaObject();

			if (!(taskObject instanceof Task)) {
				throw new InvalidModule("Scene module has to contain a Task in path " + SCENE_RUN_TASK_PATH + " but is " + taskObject);
			}

			runTask = (Task) taskObject;
		} catch (Throwable ex) {
			throw new InvalidInstance("Could not load scene " + getName() + " - " + ex.getMessage(), ex);
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		super.unload();

		runTask = null;
	}

	public PathData getSource()
	{
		return source;
	}

	public void setSource(PathData source)
	{
		this.source = source;
	}

	public DLCore getCore()
	{
		return core;
	}

	public void setCore(DLCore core)
	{
		this.core = core;
	}

	@Override
	public boolean isRunInMainThread()
	{
		assert isLoaded();

		return runTask.isRunInMainThread();
	}

	@Override
	public void waitForFinished() throws InterruptedException
	{
		assert isLoaded();

		runTask.waitForFinished();
	}

	@Override
	public void waitForFinished(long timeoutInMillis) throws InterruptedException
	{
		assert isLoaded();

		runTask.waitForFinished(timeoutInMillis);
	}

	@Override
	public void run()
	{
		assert isLoaded();

		runTask.setWorker(worker);
		runTask.run();
	}

	public Task getRunTask()
	{
		return runTask;
	}

	public Worker getWorker()
	{
		return worker;
	}

	@Override
	public void setWorker(Worker worker)
	{
		this.worker = worker;
	}
}
