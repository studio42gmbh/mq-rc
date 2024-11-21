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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Benjamin Schiller
 */
public class DefaultTaskManagerWorker implements Runnable, Worker
{

	private final static Logger log = LogManager.getLogger(DefaultTaskManagerWorker.class.getName());

	private final CountDownLatch startSignal;
	private final CountDownLatch endSignal;
	private final BlockingQueue<Task> tasks;
	private final AtomicInteger tasksToDo;

	private boolean terminated;

	public DefaultTaskManagerWorker(CountDownLatch startSignal, CountDownLatch endSignal, BlockingQueue<Task> tasks, AtomicInteger tasksToDo)
	{
		this.startSignal = startSignal;
		this.endSignal = endSignal;
		this.tasks = tasks;
		this.tasksToDo = tasksToDo;
	}

	@Override
	@SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
	public void run()
	{
		log.info("Started");

		startSignal.countDown();

		while (!isTerminated() && !Thread.currentThread().isInterrupted()) {

			try {
				Task task = tasks.poll(100, TimeUnit.MILLISECONDS);

				if (task != null) {

					task.setWorker(this);
					task.run();

					int c = tasksToDo.decrementAndGet();
					if (c == 0) {
						synchronized (tasksToDo) {
							tasksToDo.notifyAll();
						}
					}
				}
			} catch (Exception ex) {
				if (!(ex instanceof InterruptedException)) {
					log.error(ex);
					System.exit(-1);
				}
				setTerminated(true);
			}
		}

		log.info("Exiting");

		endSignal.countDown();
	}

	@Override
	public boolean isTerminated()
	{
		return terminated;
	}

	public void setTerminated(boolean terminated)
	{
		this.terminated = terminated;
	}
}
