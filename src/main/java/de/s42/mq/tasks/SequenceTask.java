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

import de.s42.dl.types.DLContainer;
import de.s42.mq.data.BooleanData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 */
public class SequenceTask extends AbstractTask implements DLContainer<Task>
{

	protected final List<Task> tasks = new ArrayList<>();

	protected BooleanData repeat;

	@Override
	protected void runTask()
	{
		// allow configurable repeat
		if (repeat != null) {
			while (true) {
				for (Task task : tasks) {
					task.run();
				}
				if (!repeat.getBooleanValue()) {
					break;
				}
			}
		} // no repeat given - run once
		else {
			for (Task task : tasks) {
				task.run();
			}
		}
	}

	@Override
	public void addChild(String name, Task child)
	{
		//remove intermediate reference for performance reasons
		if (child instanceof ReferenceTask referenceTask) {
			tasks.add(referenceTask.getReference());
		} else {
			tasks.add(child);
		}
	}

	@Override
	public List<Task> getChildren()
	{
		return (List<Task>) Collections.unmodifiableList(tasks);
	}

	public BooleanData getRepeat()
	{
		return repeat;
	}

	public void setRepeat(BooleanData repeat)
	{
		this.repeat = repeat;
	}
}
