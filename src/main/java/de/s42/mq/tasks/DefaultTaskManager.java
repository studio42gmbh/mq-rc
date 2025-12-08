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
package de.s42.mq.tasks;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.AbstractAsset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultTaskManager extends AbstractAsset implements TaskManager
{

	private final static Logger log = LogManager.getLogger(DefaultTaskManager.class.getName());

	@AttributeDL(required = true, defaultValue = "1")
	protected int workerCount = 1;

	@AttributeDL(required = true, defaultValue = "100")
	protected int maxTasksInQueue = 100;

	private boolean workerDaemons;
	private CountDownLatch startSignal;
	private CountDownLatch endSignal;
	private final List<Thread> threads = Collections.synchronizedList(new ArrayList());
	private final List<DefaultTaskManagerWorker> workers = Collections.synchronizedList(new ArrayList());
	private BlockingQueue<Task> tasks;
	private final AtomicInteger tasksToDo = new AtomicInteger(0);
	private BlockingQueue<Task> mainThreadTasks;

	public DefaultTaskManager()
	{

	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		assert workerCount > 0;
		assert maxTasksInQueue > 0;

		super.load();

		log.info("Load", name);

		startSignal = new CountDownLatch(workerCount);
		endSignal = new CountDownLatch(workerCount);
		tasks = new ArrayBlockingQueue(maxTasksInQueue);
		mainThreadTasks = new ArrayBlockingQueue(maxTasksInQueue);

		for (int i = 0; i < workerCount; ++i) {
			DefaultTaskManagerWorker worker = new DefaultTaskManagerWorker(startSignal, endSignal, tasks, tasksToDo);
			Thread thread = new Thread(worker, "DefaultTaskManagerWorker" + i);
			thread.setDaemon(workerDaemons);
			log.info("Starting Worker", thread.getName());
			thread.start();
			threads.add(thread);
			workers.add(worker);
		}
	}

	@Override
	@SuppressWarnings("SynchronizeOnNonFinalField")
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		log.info("Unload", name);

		for (DefaultTaskManagerWorker worker : workers) {
			worker.setTerminated(true);
		}

		synchronized (tasks) {
			tasks.notifyAll();
		}

		waitForWorkers();

		startSignal = null;
		endSignal = null;
		tasks = null;
		mainThreadTasks = null;

		super.unload();
	}

	@Override
	public void schedule(Task task)
	{
		assert isLoaded();
		assert task != null;

		if (task.isRunInMainThread()) {
			mainThreadTasks.add(task);
		} else {
			tasksToDo.incrementAndGet();
			tasks.add(task);
		}
	}

	@Override
	public void schedule(Collection<Task> newTasks)
	{
		assert isLoaded();
		assert newTasks != null;

		for (Task task : newTasks) {
			schedule(task);
		}
	}

	@Override
	public void schedule(Task[] newTasks)
	{
		assert isLoaded();
		assert tasks != null;

		for (Task task : newTasks) {
			schedule(task);
		}
	}

	public void waitForWorkers()
	{
		assert isLoaded();

		try {
			startSignal.await();
			endSignal.await();
		} catch (InterruptedException ex) {
			//log.error("Interrupted", ex);
		}
	}

	@Override
	public void waitForTasksDone()
	{
		assert isLoaded();

		synchronized (tasksToDo) {
			while (tasksToDo.get() > 0) {
				try {
					tasksToDo.wait();
				} catch (InterruptedException ex) {
					//log.error("Error waiting for tasks", ex);
				}
			}
		}
	}

	public int getWorkerCount()
	{
		return workerCount;
	}

	public void setWorkerCount(int workerCount)
	{
		this.workerCount = workerCount;
	}

	public boolean isWorkerDaemons()
	{
		return workerDaemons;
	}

	public void setWorkerDaemons(boolean workerDaemons)
	{
		this.workerDaemons = workerDaemons;
	}

	public int getMaxTasksInQueue()
	{
		return maxTasksInQueue;
	}

	public void setMaxTasksInQueue(int maxTasksInQueue)
	{
		this.maxTasksInQueue = maxTasksInQueue;
	}

	@Override
	public void runMainThread()
	{
		assert isLoaded();

		log.info("Start runMainThread");

		while (!mainThreadTasks.isEmpty()) {

			try {
				Task task = mainThreadTasks.take();
				task.run();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}

		log.info("Exit runMainThread");
	}
}
