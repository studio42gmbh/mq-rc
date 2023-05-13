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

import de.s42.dl.exceptions.DLException;
import de.s42.mq.core.AbstractEntity;

/**
 *
 * @author Benjamin Schiller
 */
public abstract class AbstractAsset extends AbstractEntity implements Asset, Updateable
{
	public static final AssetManager DEFAULT_ASSET_MANAGER = new DefaultAssetManager();

	protected boolean loaded;

	@Override
	public void update(float elapsedTime)
	{
	}

	public void reload() throws DLException
	{
		unload();
		load();
	}

	@Override
	public void load() throws DLException
	{
		loaded = true;
	}

	@Override
	public void unload() throws DLException
	{
		loaded = false;
	}

	@Override
	public boolean isLoaded()
	{
		return loaded;
	}

	@Override
	public AssetManager getAssetManager()
	{
		return DEFAULT_ASSET_MANAGER;
	}
}
