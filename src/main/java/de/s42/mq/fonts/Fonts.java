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
package de.s42.mq.fonts;

import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DLContainer;
import de.s42.mq.assets.AbstractAsset;
import java.util.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Fonts extends AbstractAsset implements DLContainer<Font>
{

	protected final Map<String, Font> fonts = new HashMap<>();

	public Map<String, Font> getFonts()
	{
		return fonts;
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		super.load();

		// make sure later loaded face names are also mapped
		for (Font font : new ArrayList<>(fonts.values())) {
			font.load();
			addFont(font);
		}
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		// make sure later loaded face names are also mapped
		for (Font font : fonts.values()) {
			font.unload();
		}

		super.unload();
	}

	public void addFont(Font font)
	{
		assert font != null;

		fonts.put(font.getName(), font);

		if (font.getFace() != null) {
			fonts.put(font.getFace(), font);
		}
	}

	public void removeFont(Font font)
	{
		assert font != null;

		removeFont(font.getName());

		if (font.getFace() != null) {
			removeFont(font.getFace());
		}

	}

	public void removeFont(String fontFace)
	{
		assert fontFace != null;

		fonts.remove(fontFace);
	}

	@Override
	public void addChild(String name, Font child)
	{
		assert child != null;

		addFont(child);
	}

	@Override
	public List<Font> getChildren()
	{
		return (List<Font>) Collections.unmodifiableList(new ArrayList<>(fonts.values()));
	}
}
