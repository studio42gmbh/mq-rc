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
package de.s42.mq.prefabs;

import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.tasks.AbstractTask;
import de.s42.mq.tasks.LongRunningTask;

/**
 *
 * @author Benjamin Schiller
 */
public class LoadPrefabTask extends AbstractTask implements LongRunningTask
{

	@required
	protected Prefab prefab;

	@required
	protected Object context;

	@Override
	protected void runTask() throws DLException
	{
		assert prefab != null : "prefab != null";

		prefab.instantiate(context);
	}

	public Object getContext()
	{
		return context;
	}

	public void setContext(Object context)
	{
		this.context = context;
	}

	public Prefab getPrefab()
	{
		return prefab;
	}

	public void setPrefab(Prefab prefab)
	{
		this.prefab = prefab;
	}
}
