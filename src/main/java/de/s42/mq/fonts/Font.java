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

import de.s42.base.files.FilesHelper;
import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.files.IsDirectoryDLAnnotation.isDirectory;
import de.s42.dl.annotations.files.IsFileDLAnnotation.isFile;
import de.s42.dl.exceptions.DLException;
import de.s42.dl.types.DLContainer;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.assets.AbstractAsset;
import de.s42.mq.fonts.fnt.FntLoader;
import java.nio.file.Path;
import java.util.*;

/**
 *
 * @author Benjamin Schiller
 */
public class Font extends AbstractAsset implements DLContainer<GlyphPage>
{

	private final static Logger log = LogManager.getLogger(Font.class.getName());

	@AttributeDL(required = true)
	@isFile
	protected Path source;

	@AttributeDL(required = false)
	@isDirectory
	protected Path textureDirectory;

	@AttributeDL(required = false, defaultValue = "1.0")
	protected float smoothingFactor = 1.0f;

	protected String face;
	protected float size;
	protected float scaleW;
	protected float scaleH;
	protected boolean bold;
	protected boolean italic;
	protected boolean smooth;
	protected boolean antiAlias;
	protected float stretchH;
	protected float paddingTop;
	protected float paddingBottom;
	protected float paddingLeft;
	protected float paddingRight;
	protected float spacingX;
	protected float spacingY;
	protected float lineHeight;
	protected float base;

	protected final List<GlyphPage> pages = new ArrayList<>();
	protected final Map<Integer, Glyph> glyphs = new HashMap<>();

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		log.debug("Loading font from file " + FilesHelper.createMavenNetbeansFileConsoleLink(getSource()));

		FntLoader.loadFromFile(getAssetManager(), getSource(), this);

		for (GlyphPage page : getPages()) {
			page.getTexture().load();
		}

		log.debug("Loaded font successfully " + getFace());

		super.load();
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		for (GlyphPage page : getPages()) {
			page.getTexture().unload();
		}

		super.unload();
	}

	public Map<Integer, Glyph> getGlyphs()
	{
		return glyphs;
	}

	public void addGlyph(Glyph glyph)
	{
		assert glyph != null;

		glyphs.put(glyph.getId(), glyph);

		//log.debug("Added glyph " + glyph.getId());
	}

	public void removeGlyph(Glyph glyph)
	{
		assert glyph != null;

		removeGlyph(glyph.getId());
	}

	public void removeGlyph(int id)
	{
		Glyph glyph = glyphs.remove(id);
	}

	public boolean hasGlyph(int id)
	{
		return glyphs.containsKey(id);
	}

	public Glyph getGlyph(int id)
	{
		return glyphs.get(id);
	}

	public List<GlyphPage> getPages()
	{
		return pages;
	}

	public void addPage(GlyphPage page)
	{
		assert page != null;

		pages.add(page);

		for (Glyph glyph : page.getGlyphs().values()) {
			addGlyph(glyph);
		}
	}

	public void removePage(GlyphPage page)
	{
		assert page != null;

		pages.remove(page);

		for (Glyph glyph : page.getGlyphs().values()) {
			removeGlyph(glyph);
		}
	}

	@Override
	public void addChild(String name, GlyphPage child)
	{
		assert child != null;

		addPage(child);
	}

	@Override
	public List<GlyphPage> getChildren()
	{
		return (List<GlyphPage>) Collections.unmodifiableList(pages);
	}

	public String getFace()
	{
		return face;
	}

	public void setFace(String face)
	{
		this.face = face;
	}

	public float getSize()
	{
		return size;
	}

	public void setSize(float size)
	{
		this.size = size;
	}

	public boolean isBold()
	{
		return bold;
	}

	public void setBold(boolean bold)
	{
		this.bold = bold;
	}

	public boolean isItalic()
	{
		return italic;
	}

	public void setItalic(boolean italic)
	{
		this.italic = italic;
	}

	public float getStretchH()
	{
		return stretchH;
	}

	public void setStretchH(float stretchH)
	{
		this.stretchH = stretchH;
	}

	public boolean isSmooth()
	{
		return smooth;
	}

	public void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}

	public boolean isAntiAlias()
	{
		return antiAlias;
	}

	public void setAntiAlias(boolean antiAlias)
	{
		this.antiAlias = antiAlias;
	}

	public float getPaddingTop()
	{
		return paddingTop;
	}

	public void setPaddingTop(float paddingTop)
	{
		this.paddingTop = paddingTop;
	}

	public float getPaddingBottom()
	{
		return paddingBottom;
	}

	public void setPaddingBottom(float paddingBottom)
	{
		this.paddingBottom = paddingBottom;
	}

	public float getPaddingLeft()
	{
		return paddingLeft;
	}

	public void setPaddingLeft(float paddingLeft)
	{
		this.paddingLeft = paddingLeft;
	}

	public float getPaddingRight()
	{
		return paddingRight;
	}

	public void setPaddingRight(float paddingRight)
	{
		this.paddingRight = paddingRight;
	}

	public float getSpacingX()
	{
		return spacingX;
	}

	public void setSpacingX(float spacingX)
	{
		this.spacingX = spacingX;
	}

	public float getSpacingY()
	{
		return spacingY;
	}

	public void setSpacingY(float spacingY)
	{
		this.spacingY = spacingY;
	}

	public float getLineHeight()
	{
		return lineHeight;
	}

	public void setLineHeight(float lineHeight)
	{
		this.lineHeight = lineHeight;
	}

	public float getBase()
	{
		return base;
	}

	public void setBase(float base)
	{
		this.base = base;
	}

	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public Path getTextureDirectory()
	{
		return textureDirectory;
	}

	public void setTextureDirectory(Path textureDirectory)
	{
		this.textureDirectory = textureDirectory;
	}

	public float getScaleW()
	{
		return scaleW;
	}

	public void setScaleW(float scaleW)
	{
		this.scaleW = scaleW;
	}

	public float getScaleH()
	{
		return scaleH;
	}

	public void setScaleH(float scaleH)
	{
		this.scaleH = scaleH;
	}

	public float getSmoothingFactor()
	{
		return smoothingFactor;
	}

	public void setSmoothingFactor(float smoothingFactor)
	{
		this.smoothingFactor = smoothingFactor;
	}
}
