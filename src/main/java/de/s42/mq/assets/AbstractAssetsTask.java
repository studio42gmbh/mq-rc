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
import de.s42.mq.tasks.AbstractTask;
import de.s42.mq.tasks.LongRunningTask;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractAssetsTask extends AbstractTask implements LongRunningTask
{
	@AttributeDL(required = true)
	protected Assets assets;

	public Assets getAssets()
	{
		return assets;
	}

	public void setAssets(Assets assets)
	{
		assert assets != null;

		this.assets = assets;
	}
}
