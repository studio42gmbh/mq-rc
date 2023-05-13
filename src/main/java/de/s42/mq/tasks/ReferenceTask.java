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
import de.s42.mq.core.AbstractEntity;

/**
 *
 * @author Benjamin Schiller
 */
public class ReferenceTask extends AbstractEntity implements Task
{
	@AttributeDL(required = true)
	protected Task reference;
	
	protected Worker worker;

	@Override
	public void run()
	{
		assert reference != null;

		reference.setWorker(worker);
		reference.run();
	}

	public Task getReference()
	{
		return reference;
	}

	public void setReference(Task reference)
	{
		this.reference = reference;
	}

	@Override
	public boolean isRunInMainThread()
	{
		return reference.isRunInMainThread();
	}

	@Override
	public void waitForFinished() throws InterruptedException
	{
		reference.waitForFinished();
	}

	@Override
	public void waitForFinished(long timeoutInMillis) throws InterruptedException
	{
		reference.waitForFinished(timeoutInMillis);
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
