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
package de.s42.mq.assets;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.FloatData;
import de.s42.mq.tasks.AbstractTask;

/**
 *
 * @author Benjamin Schiller
 */
public class UpdateUpdateable extends AbstractTask
{

	@AttributeDL(required = true)
	protected Updateable updateable;

	@AttributeDL(required = true)
	protected FloatData deltaTime;

	@Override
	protected void runTask()
	{
		assert updateable != null;

		updateable.update(deltaTime.getFloatValue());
	}

	public Updateable getUpdateable()
	{
		return updateable;
	}

	public void setUpdateable(Updateable updateable)
	{
		assert updateable != null;

		this.updateable = updateable;
	}

	public FloatData getDeltaTime()
	{
		return deltaTime;
	}

	public void setDeltaTime(FloatData deltaTime)
	{
		this.deltaTime = deltaTime;
	}
}
