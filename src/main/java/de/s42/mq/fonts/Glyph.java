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
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Benjamin Schiller
 */
public class Glyph
{

	protected final Map<Integer, Float> kernings = new HashMap<>();
	protected int id;
	protected float x;
	protected float y;
	protected float width;
	protected float height;
	protected float xOffset;
	protected float yOffset;
	protected float xAdvance;
	protected int channel;

	@AttributeDL(ignore = true)
	protected GlyphPage page;

	public Map<Integer, Float> getKernings()
	{
		return kernings;
	}

	public void addKerning(int otherChar, float amount)
	{
		kernings.put(otherChar, amount);
	}

	public void removeKerning(int otherChar)
	{
		kernings.remove(otherChar);
	}

	public float getKerningOffset(int otherChar)
	{
		Float kerning = kernings.get(otherChar);

		if (kerning != null) {
			return kerning;
		}

		return 0.0f;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	public float getWidth()
	{
		return width;
	}

	public void setWidth(float width)
	{
		this.width = width;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public float getxOffset()
	{
		return xOffset;
	}

	public void setxOffset(float xOffset)
	{
		this.xOffset = xOffset;
	}

	public float getyOffset()
	{
		return yOffset;
	}

	public void setyOffset(float yOffset)
	{
		this.yOffset = yOffset;
	}

	public GlyphPage getPage()
	{
		return page;
	}

	public void setPage(GlyphPage page)
	{
		this.page = page;
	}

	public int getChannel()
	{
		return channel;
	}

	public void setChannel(int channel)
	{
		this.channel = channel;
	}

	public float getxAdvance()
	{
		return xAdvance;
	}

	public void setxAdvance(float xAdvance)
	{
		this.xAdvance = xAdvance;
	}

}
