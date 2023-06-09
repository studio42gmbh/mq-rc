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
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.materials.Material;
import java.util.ArrayList;
import java.util.List;
import de.s42.dl.types.DLContainer;

/**
 *
 * @author Benjamin Schiller
 * @param <ChildType>
 */
public class Assets<ChildType extends Object> extends AbstractAsset implements DLContainer<ChildType>
{
	private final static Logger log = LogManager.getLogger(Assets.class.getName());

	protected final List<Asset> assets = new ArrayList();

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		for (Asset asset : assets) {
			asset.load();
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		for (Asset asset : assets) {
			asset.unload();
		}

		super.unload();
	}

	public void add(Asset asset)
	{
		assert asset != null;

		assets.add(asset);
	}

	@Override
	public void addChild(String name, ChildType child)
	{
		if (child instanceof Asset) {
			add((Asset) child);
		}
	}

	protected Material findMaterial(Assets container, String name)
	{
		assert name != null;
		assert container != null;

		// @todo optimize retrieval of material
		for (Asset asset : (List<Asset>) container.assets) {
			if ((asset instanceof Material) && asset.getName().equals(name)) {
				return (Material) asset;
			}

			if (asset instanceof Assets) {
				Material mat = findMaterial((Assets) asset, name);

				if (mat != null) {
					return mat;
				}
			}
		}

		return null;
	}

	public Material findMaterial(String name)
	{
		return findMaterial(this, name);
	}

	public <AssetType extends Asset> AssetType findAssetOfType(Class<AssetType> type)
	{
		assert type != null;

		for (Asset asset : assets) {
			if (type.isAssignableFrom(asset.getClass())) {
				return (AssetType) asset;
			}
		}

		return null;
	}

	public <AssetType extends Asset> List<AssetType> findAssetsOfType(Class<AssetType> type)
	{
		assert type != null;

		List<AssetType> result = new ArrayList<>(assets.size());

		for (Asset asset : assets) {
			if (type.isAssignableFrom(asset.getClass())) {
				result.add((AssetType) asset);
			}
		}

		return result;
	}

	public <AssetType extends Asset> AssetType findAssetOfName(String assetName)
	{
		assert assetName != null;

		for (Asset asset : assets) {
			if (assetName.equals(asset.getName())) {
				return (AssetType) asset;
			}
		}

		return null;
	}

	public int indexOfAsset(Asset asset)
	{
		assert asset != null;

		return assets.indexOf(asset);
	}

	public int getAssetCount()
	{
		return assets.size();
	}

	public <AssetType extends Asset> AssetType findAssetWithIndex(int index)
	{
		assert index > -1;
		assert index < assets.size();

		return (AssetType) assets.get(index);
	}
}
