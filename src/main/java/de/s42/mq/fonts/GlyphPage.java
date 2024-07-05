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

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.types.DLContainer;
import de.s42.mq.materials.Texture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public class GlyphPage implements DLContainer<Glyph>
{

	@AttributeDL(required = true)
	protected Texture texture;

	protected final Map<Integer, Glyph> glyphs = new HashMap<>();

	public Map<Integer, Glyph> getGlyphs()
	{
		return glyphs;
	}

	public void addGlyph(Glyph glyph)
	{
		assert glyph != null;

		glyphs.put(glyph.getId(), glyph);
		glyph.setPage(this);
	}

	public void removeGlyph(Glyph glyph)
	{
		assert glyph != null;

		removeGlyph(glyph.getId());
	}

	public void removeGlyph(int id)
	{
		Glyph glyph = glyphs.remove(id);
		if (glyph != null) {
			glyph.setPage(null);
		}
	}

	@Override
	public void addChild(String name, Glyph child)
	{
		addGlyph(child);
	}

	@Override
	public List<Glyph> getChildren()
	{
		return (List<Glyph>) Collections.unmodifiableList(new ArrayList<>(glyphs.values()));
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}
}
