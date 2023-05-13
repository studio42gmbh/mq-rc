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
import de.s42.dl.exceptions.DLException;
import de.s42.mq.tasks.AbstractTask;
import de.s42.mq.tasks.LongRunningTask;

/**
 *
 * @author Benjamin Schiller
 */
public class UnloadAssetTask extends AbstractTask implements LongRunningTask
{

	@AttributeDL(required = true)
	protected Asset asset;

	@Override
	protected void runTask() throws DLException
	{
		assert asset != null;

		asset.unload();
	}

	public Asset getAsset()
	{
		return asset;
	}

	public void setAsset(Asset asset)
	{
		assert asset != null;

		this.asset = asset;
	}
}
