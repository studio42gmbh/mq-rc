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

import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.core.AbstractEntity;
import de.s42.mq.data.BooleanData;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractTask extends AbstractEntity implements Task
{

	private final static Logger log = LogManager.getLogger(AbstractTask.class.getName());

	protected Task waitForTask;
	protected BooleanData active;
	protected boolean runInMainThread;
	protected boolean firstTime = true;
	protected Worker worker;

	protected final CountDownLatch finishedLatch = new CountDownLatch(1);

	protected void runTaskFirstTime() throws Exception
	{
		// init task
	}

	abstract protected void runTask() throws Exception;

	@Override
	public void run()
	{
		//log.trace("Start {}", getName());

		// wait for task to finish
		if (waitForTask != null) {
			try {
				log.debug("Waiting for", waitForTask.getName());

				waitForTask.waitForFinished();
				waitForTask = null;

				log.debug("Continuing", getName());
			} catch (InterruptedException ex) {

				log.debug("Exit Interrupted", getName());
				finishedLatch.countDown();
				return;
			}
		}

		if (active == null || active.getBooleanValue()) {
			try {

				if (firstTime) {
					runTaskFirstTime();
					firstTime = false;
				}

				runTask();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		//log.trace("Exit {}", getName());
		finishedLatch.countDown();
	}

	@Override
	public void waitForFinished() throws InterruptedException
	{
		finishedLatch.await();
	}

	@Override
	public void waitForFinished(long timeoutInMillis) throws InterruptedException
	{
		finishedLatch.await(timeoutInMillis, TimeUnit.MILLISECONDS);
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Task getWaitForTask()
	{
		return waitForTask;
	}

	public void setWaitForTask(Task waitForTask)
	{
		this.waitForTask = waitForTask;
	}

	@Override
	public boolean isRunInMainThread()
	{
		return runInMainThread;
	}

	public void setRunInMainThread(boolean runInMainThread)
	{
		this.runInMainThread = runInMainThread;
	}

	public BooleanData getActive()
	{
		return active;
	}

	public void setActive(BooleanData active)
	{
		this.active = active;
	}

	public boolean isFirstTime()
	{
		return firstTime;
	}

	public void setFirstTime(boolean firstTime)
	{
		this.firstTime = firstTime;
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
	// "Getters/Setters" </editor-fold>
}
