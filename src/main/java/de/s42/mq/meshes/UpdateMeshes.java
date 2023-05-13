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
package de.s42.mq.meshes;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.mq.data.FloatData;
import de.s42.mq.tasks.AbstractTask;

/**
 *
 * @author Benjamin Schiller
 */
public class UpdateMeshes extends AbstractTask
{

	@AttributeDL(required = true)
	protected MeshGroup meshes;

	@AttributeDL(required = true)
	protected FloatData deltaTime = new FloatData();

	@Override
	protected void runTask()
	{
		assert meshes != null;
		assert deltaTime != null;

		meshes.update(deltaTime.getFloatValue());
	}

	public MeshGroup getMeshes()
	{
		return meshes;
	}

	public void setMeshes(MeshGroup meshes)
	{
		this.meshes = meshes;
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
