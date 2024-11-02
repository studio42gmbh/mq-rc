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

import de.s42.mq.assets.Asset;
import de.s42.mq.assets.Assets;
import de.s42.mq.core.Copyable;
import java.util.List;

/**
 *
 * @author Benjamin Schiller
 * @param <ChildType>
 */
public class Prefab<ChildType extends Object> extends Assets<ChildType>
{

	public Assets instantiate()
	{
		// Copy first asset and create
		Assets instance = new Assets();

		// Copy or link assets
		for (Asset asset : ((List<Asset>) getAssets())) {
			if (asset instanceof Copyable copyable) {
				instance.add(copyable.copy());
			} else {
				instance.add(asset);
			}
		}

		return instance;
	}
}
