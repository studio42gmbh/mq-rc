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

import de.s42.dl.DLAnnotation.AnnotationDL;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.FloatData;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.dl.annotations.InSecondsDLAnnotation;

/**
 *
 * @author Benjamin Schiller
 */
public class DebouncedSequenceTask extends SequenceTask
{
	private final static Logger log = LogManager.getLogger(DebouncedSequenceTask.class.getName());

	@AttributeDL(required = true)
	//@AnnotationDL(value = InSecondsDLAnnotation.DEFAULT_SYMBOL)
	protected FloatData delta = new FloatData();

	protected long lastCallTime;

	@Override
	protected void runTask()
	{
		long currentTime = System.nanoTime();

		float currentCallDelta = (currentTime - lastCallTime) / 1E9f;

		if (currentCallDelta < delta.getFloatValue()) {
			return;
		}

		//log.debug("Debounced " + getName() + " " + currentCallTime);
		lastCallTime = currentTime;

		super.runTask();
	}

	public FloatData getDelta()
	{
		return delta;
	}

	public void setDelta(FloatData delta)
	{
		this.delta = delta;
	}
}
