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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Benjamin Schiller
 */
public class TaskQueue extends AbstractTask
{
	@AttributeDL(ignore = true)
	protected final BlockingQueue<Task> tasks = new ArrayBlockingQueue(100);

	@Override
	protected void runTask()
	{
		while (!tasks.isEmpty()) {

			try {
				Task task = tasks.take();
				task.run();
			}
			catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public BlockingQueue<Task> getTasks()
	{
		return tasks;
	}

	public void schedule(Task task)
	{
		assert task != null;

		tasks.add(task);
	}
}
