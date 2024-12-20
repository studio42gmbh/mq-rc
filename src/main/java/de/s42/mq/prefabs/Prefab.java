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

import de.s42.dl.exceptions.DLException;
import de.s42.mq.assets.Assets;

/**
 *
 * @author Benjamin Schiller
 */
public class Prefab extends Assets
{

	protected final static PrefabLoader DEFAULT_LOADER = new BasePrefabLoader();

	protected PrefabLoader loader = DEFAULT_LOADER;

	public Assets instantiate(Object... context) throws DLException
	{
		return loader.instantiate(this, context);
	}

	public PrefabLoader getLoader()
	{
		return loader;
	}

	public void setLoader(PrefabLoader loader)
	{
		this.loader = loader;
	}
}
