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
import de.s42.dl.annotations.attributes.RequiredDLAnnotation.required;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation.dontPersist;
import de.s42.dl.exceptions.DLException;
import de.s42.mq.MQColor;
import de.s42.mq.buffers.FrameBuffer;
import de.s42.mq.core.Copyable;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.FloatData;
import de.s42.mq.data.StringData;
import de.s42.mq.materials.Material;
import de.s42.mq.meshes.Mesh;
import de.s42.mq.rendering.RenderContext;
import de.s42.mq.ui.UIComponent;
import de.s42.mq.ui.UIManager;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 *
 * @author Benjamin Schiller
 */
public class Text extends Mesh implements UIComponent, Copyable
{

	//private final static Logger log = LogManager.getLogger(Text.class.getName());
	public final static float DPI_CORRECT = 72.0f / 96.0f;// ((float) Toolkit.getDefaultToolkit().getScreenResolution()) / 72.0f;

	public static enum VerticalAlignment
	{
		TOP, CENTER, BOTTOM
	}

	public static enum HorizontalAlignment
	{
		LEFT, CENTER, RIGHT
	}

	// Position X, Y, Z  UVs U, Y
	protected final static float quadVertices[] = {
		0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
		0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
		1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 0.0f, 1.0f, 0.0f
	};

	@required
	protected Font font;

	@required
	protected FrameBuffer buffer;

	protected ColorData color = new ColorData();
	protected FloatData edge = new FloatData(0.5f);
	protected ColorData color2 = new ColorData(new MQColor(1.0f));
	protected FloatData edge2 = new FloatData(1.0f);
	protected FloatData fontSize = new FloatData(1.0f);
	protected FloatData letterSpacing = new FloatData(0.0f);

	@AttributeDL(defaultValue = "false")
	protected boolean scaleWithBufferSize = false;

	@AttributeDL(defaultValue = "TOP")
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

	@AttributeDL(defaultValue = "LEFT")
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

	@AttributeDL(defaultValue = "100")
	protected int maxCharCount = 100;

	/**
	 * if no layout is given position and scale shall remain untouched
	 */
	protected Layout layout;
	protected LayoutOptions layoutOptions;

	protected StringData text = new StringData("");
	protected UIManager uiManager;

	@AttributeDL(defaultValue = "false")
	protected boolean focusable = false;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean visible = true;

	@dontPersist
	protected int vao = -1;

	@dontPersist
	protected int vbo = -1;

	@dontPersist
	protected int charDataBuffer = -1;

	@dontPersist
	protected float charData[];

	@dontPersist
	protected boolean textDirty = true;

	public void setOptions(TextOptions options)
	{
		assert options != null : "options != null";

		setColor(options.getColor());
		setEdge(options.getEdge());
		setColor2(options.getColor2());
		setEdge2(options.getEdge2());
		setFontSize(options.getFontSize());
		setFont(options.getFont());
		setLetterSpacing(options.getLetterSpacing());
		setScaleWithBufferSize(options.isScaleWithBufferSize());
		setVerticalAlignment(options.getVerticalAlignment());
		setHorizontalAlignment(options.getHorizontalAlignment());
		setMaxCharCount(options.getMaxCharCount());
		setMaterial(options.getMaterial());
		setBuffer(options.getBuffer());
	}

	@Override
	public Text copy()
	{
		Text copy = (Text) super.copy();

		// @todo Finalize proper copying
		copy.uiManager = uiManager;
		copy.maxCharCount = maxCharCount;
		copy.vao = vao;
		copy.vbo = vbo;
		copy.charDataBuffer = charDataBuffer;
		copy.charData = charData;
		copy.textDirty = textDirty;

		copy.font = font;
		copy.layout = layout;
		copy.layoutOptions = (layoutOptions != null) ? (LayoutOptions) layoutOptions.copy() : null;
		copy.buffer = buffer;
		copy.text.setValue(text.getValue());
		copy.color.setValue(color.getValue());
		copy.edge.setValue(edge.getValue());
		copy.color2.setValue(color2.getValue());
		copy.edge2.setValue(edge2.getValue());
		copy.fontSize.setValue(fontSize.getValue());
		copy.letterSpacing.setValue(letterSpacing.getValue());
		copy.scaleWithBufferSize = scaleWithBufferSize;
		copy.verticalAlignment = verticalAlignment;
		copy.horizontalAlignment = horizontalAlignment;

		return copy;
	}

	@Override
	public void load() throws DLException
	{
		assert maxCharCount > 0 : "maxCharCount > 0";
		assert buffer != null : "buffer != null " + getName();

		if (isLoaded()) {
			return;
		}

		super.load();

		log.trace("Generating font cache", maxCharCount);
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		// Generate chardata top, left, bottom, right, uvtop, uvleft, uvbottom, uvright
		charData = new float[maxCharCount * 8];

		for (int i = 0; i < maxCharCount; ++i) {
			int cI = i * 8;
			charData[cI] = 0.0f;
			charData[cI + 1] = 0.0f;
			charData[cI + 2] = 0.1f;
			charData[cI + 3] = 0.1f;
			charData[cI + 4] = 0.0f;
			charData[cI + 5] = 0.0f;
			charData[cI + 6] = 0.5f;
			charData[cI + 7] = 0.5f;
		}

		charDataBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, charDataBuffer);
		glBufferData(GL_ARRAY_BUFFER, charData, GL_DYNAMIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// Generate vertex buffer
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glBindVertexArray(0);
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		glDeleteBuffers(vbo);
		vbo = -1;
		glDeleteVertexArrays(vao);
		vao = -1;

		super.unload();
	}

	public Vector2f computeDimension(String text)
	{
		assert isLoaded() : "isLoaded()";
		assert buffer != null : "buffer != null";
		assert text != null : "text != null";

		Vector2f dimension = new Vector2f();

		float fontStretch = font.getStretchH() / 100.0f;

		float fontSizeX = fontSize.getFloatValue() * fontStretch / font.getSize() / DPI_CORRECT;
		float fontSizeY = fontSize.getFloatValue() / font.getSize() / DPI_CORRECT;

		float fontLineHeight = font.getLineHeight();
		float letterSpace = letterSpacing.getFloatValue() * font.getSize();

		int cCount = Math.min(Character.codePointCount(text, 0, text.length()), maxCharCount);
		float x = 0.0f;
		float y = (fontLineHeight - font.getPaddingTop() - font.getPaddingBottom()) * fontSizeY;
		Glyph lastGlyph = null;

		for (int c = 0; c < cCount; ++c) {

			int charId = Character.codePointAt(text, c);

			// New line
			if (charId == '\n') {
				dimension.x = Math.max(x, dimension.x);
				x = 0;
				y += (fontLineHeight - font.getPaddingTop() - font.getPaddingBottom()) * fontSizeY;
			}

			Glyph glyph = font.getGlyph(charId);

			// @todo Provide better replacement mechanics for missing glyphs?
			if (glyph == null) {
				glyph = font.getGlyph('_');
			}

			float kerningOffset = 0.0f;

			if (lastGlyph != null) {
				kerningOffset = lastGlyph.getKerningOffset(glyph.getId());
			}

			lastGlyph = glyph;

			x += (glyph.getxAdvance() + kerningOffset - font.getPaddingLeft() - font.getPaddingRight() + letterSpace) * fontSizeX;
		}

		dimension.x = Math.max(x, dimension.x);
		dimension.y = Math.max(y, dimension.y);

		return dimension;
	}

	public Vector2f computeDimensionScreenspace(String text)
	{
		assert isLoaded() : "isLoaded()";
		assert buffer != null : "buffer != null";
		assert text != null : "text != null";

		Vector2f dimension = computeDimension(text);

		float screenWidth = buffer.getWidth();
		float screenHeight = buffer.getHeight();

		// @todo Find solution for making text scale properly with parent scalings
		Vector3f parentScale = getParentMatrix().getScale(new Vector3f());
		screenWidth *= parentScale.x;
		screenHeight *= parentScale.y;

		if (!isScaleWithBufferSize()) {
			float normScale = 1024.0f / screenHeight;
			screenWidth *= normScale;
			screenHeight *= normScale;
		}

		dimension.x *= 1.0 / screenWidth * 2.0f;
		dimension.y *= 1.0 / screenHeight * 2.0f;

		return dimension;
	}

	// @todo Allow to render multipage texts
	protected void updateCharData()
	{
		assert isLoaded() : "isLoaded()";
		assert buffer != null : "buffer != null";

		String t = getText().getStringValue();

		if (t == null) {
			return;
		}

		String[] lines = t.split("\n");
		Font f = getFont();
		Vector2f dimensions = computeDimensionScreenspace(t);
		GlyphPage p = f.getPages().get(0);

		float alignmentOffsetY = 0.0f;

		if (verticalAlignment == VerticalAlignment.CENTER) {
			alignmentOffsetY = dimensions.y * 0.5f;
		} else if (verticalAlignment == VerticalAlignment.BOTTOM) {
			alignmentOffsetY = dimensions.y;
		}

		float screenWidth = buffer.getWidth();
		float screenHeight = buffer.getHeight();

		Vector3f parentScale = getParentMatrix().getScale(new Vector3f());
		screenWidth *= parentScale.x;
		screenHeight *= parentScale.y;

		if (!isScaleWithBufferSize()) {
			float normScale = 1024.0f / screenHeight;
			screenWidth *= normScale;
			screenHeight *= normScale;
		}

		float fontStretch = f.getStretchH() / 100.0f;

		float fontSizeX = fontSize.getFloatValue() * fontStretch / screenWidth * 2.0f / f.getSize() / DPI_CORRECT;
		float fontSizeY = fontSize.getFloatValue() / screenHeight * 2.0f / f.getSize() / DPI_CORRECT;

		float fontLineHeight = f.getLineHeight();
		float letterSpace = letterSpacing.getFloatValue() * f.getSize();

		float texSizeX = 1.0f / p.getTexture().getWidth();
		float texSizeY = 1.0f / p.getTexture().getHeight();

		int cCount = Math.min(Character.codePointCount(t, 0, t.length()), maxCharCount);
		float x = 0.0f;
		float y = 0.0f;
		int line = 0;
		Glyph lastGlyph = null;

		Vector2f lineDimensions = computeDimensionScreenspace(lines[0]);
		float lineAlignmentOffsetX = 0.0f;

		if (null != horizontalAlignment) {
			switch (horizontalAlignment) {
				case LEFT ->
					lineAlignmentOffsetX = 0.0f;
				case CENTER ->
					lineAlignmentOffsetX = -lineDimensions.x * 0.5f;
				case RIGHT ->
					lineAlignmentOffsetX = -lineDimensions.x;
				default -> {
				}
			}
		}

		for (int c = 0; c < cCount; ++c) {

			int charId = Character.codePointAt(t, c);

			// New line
			if (charId == '\n') {
				x = 0;
				y -= (fontLineHeight - f.getPaddingTop() - f.getPaddingBottom()) * fontSizeY;
				line++;

				// @todo solve issue with newline right at the end of a text
				lineDimensions = computeDimensionScreenspace(lines[line]);
				if (null != horizontalAlignment) {
					switch (horizontalAlignment) {
						case LEFT ->
							lineAlignmentOffsetX = 0;
						case CENTER ->
							lineAlignmentOffsetX = -lineDimensions.x * 0.5f;
						case RIGHT ->
							lineAlignmentOffsetX = -lineDimensions.x;
						default -> {
						}
					}
				}
			}

			Glyph glyph = f.getGlyph(charId);

			// @todo Provide better replacement mechanics for missing glyphs?
			if (glyph == null) {
				glyph = f.getGlyph('_');
			}

			int cI = c * 8;

			float kerningOffset = 0.0f;

			if (lastGlyph != null) {
				kerningOffset = lastGlyph.getKerningOffset(glyph.getId());
			}

			lastGlyph = glyph;

			// Pos
			charData[cI + 0] = x + (glyph.getxOffset() + kerningOffset) * fontSizeX + lineAlignmentOffsetX;
			charData[cI + 1] = y - (glyph.getyOffset()) * fontSizeY + alignmentOffsetY;
			charData[cI + 2] = (glyph.getWidth()) * fontSizeX;
			charData[cI + 3] = (glyph.getHeight()) * fontSizeY;

			// UVs
			charData[cI + 4] = (glyph.getX()) * texSizeX;
			charData[cI + 5] = (glyph.getY()) * texSizeY;
			charData[cI + 6] = (glyph.getWidth()) * texSizeX;
			charData[cI + 7] = (glyph.getHeight()) * texSizeY;

			x += (glyph.getxAdvance() + kerningOffset - f.getPaddingLeft() - f.getPaddingRight() + letterSpace) * fontSizeX;
		}

		glBindBuffer(GL_ARRAY_BUFFER, charDataBuffer);
		glBufferData(GL_ARRAY_BUFFER, charData, GL_DYNAMIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	protected float lastFontSize;
	protected String lastText;
	protected int lastWidth;
	protected int lastHeight;

	protected float getFontScreenSize()
	{
		Vector3f parentScale = getParentMatrix().getScale(new Vector3f());
		return getFontSize().getFloatValue() * parentScale.y;
	}

	@Override
	public void render(RenderContext context)
	{
		assert context != null : "context != null";
		assert isLoaded();
		assert getMaterial() != null;
		assert getMaterial().isLoaded();
		assert getMaterial().getShader() instanceof FontShader;
		assert getMaterial().getShader().isLoaded();
		assert getFont() != null;
		assert getFont().isLoaded();
		assert getLayout() != null;
		assert getLayoutOptions() != null : "getLayoutOptions() != null for " + getName();

		if (!isVisible()) {
			return;
		}

		if (getText().getValue() == null) {
			return;
		}

		// log.debug("Render " + font.getFace() + " " + getText());
		// Use override material if given
		Material mat = (context.getOverrideMaterial() != null) ? context.getOverrideMaterial() : material;
		FontShader shader = (FontShader) mat.getShader();

		// Apply layout if given
		Layout lay = getLayout();
		if (lay != null) {
			lay.layout(this, getLayoutOptions());
		}
		// @todo Reset the scale - is this really wanted that way here? -> will prevent Text from being scalable with scale
		getScale().set(1.0f);

		float fontScreenSize = getFontScreenSize();

		if (isTextDirty()
			|| (lastFontSize != fontScreenSize)
			|| (!lastText.equals(getText().getStringValue()))
			|| (lastWidth != getBuffer().getWidth())
			|| (lastHeight != getBuffer().getHeight())) {
			updateCharData();
			setTextDirty(false);
			lastFontSize = fontScreenSize;
			lastText = getText().getStringValue();
			lastWidth = getBuffer().getWidth();
			lastHeight = getBuffer().getHeight();
		}

		updateModelMatrix();

		// @todo Change formula to better manage scaling
		float edgeSize = Math.max(0.001f, font.getSmoothingFactor() / Math.max(1.0f, fontScreenSize));

		mat.beforeRendering(context);
		shader.setMesh(this);
		shader.setBaseTexture(getFont().getPages().get(0).getTexture());

		shader.setEdge(getEdge());
		shader.setTint(getColor());
		shader.getEdgeSize().setFloatValue(edgeSize);

		shader.setEdge2(getEdge2());
		shader.setTint2(getColor2());
		shader.getEdge2Size().setFloatValue(edgeSize);

		shader.beforeRendering(context);

		glBindVertexArray(vao);

		// Positions
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glEnableVertexAttribArray(shader.getInputPosition());
		glVertexAttribPointer(shader.getInputPosition(), 3, GL_FLOAT, false, 5 * 4, 0L);

		// UVs
		glEnableVertexAttribArray(shader.getInputTextureCoords());
		glVertexAttribPointer(shader.getInputTextureCoords(), 2, GL_FLOAT, false, 5 * 4, 3L * 4L);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// CharData
		glBindBuffer(GL_ARRAY_BUFFER, charDataBuffer);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 0L);
		glVertexAttribDivisor(2, 1);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3, 2, GL_FLOAT, false, 8 * 4, 2L * 4L);
		glVertexAttribDivisor(3, 1);
		glEnableVertexAttribArray(4);
		glVertexAttribPointer(4, 2, GL_FLOAT, false, 8 * 4, 4L * 4L);
		glVertexAttribDivisor(4, 1);
		glEnableVertexAttribArray(5);
		glVertexAttribPointer(5, 2, GL_FLOAT, false, 8 * 4, 6L * 4L);
		glVertexAttribDivisor(5, 1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		glDrawArraysInstanced(GL_TRIANGLES, 0, 6, Math.min(getText().getStringValue().length(), maxCharCount));

		glBindVertexArray(0);

		shader.afterRendering(context);
		mat.afterRendering(context);
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		// do nothing
	}

	@Override
	public void handleChar(String chars)
	{
		// do nothing
	}

	@Override
	public void handleClick(int x, int y) throws DLException
	{
		// do nothing
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		this.font = font;

		setTextDirty(true);
	}

	public ColorData getColor()
	{
		return color;
	}

	public void setColor(ColorData color)
	{
		this.color = color;
	}

	public StringData getText()
	{
		return text;
	}

	public void setText(StringData text)
	{
		this.text = text;

		setTextDirty(true);
	}

	public boolean isTextDirty()
	{
		return textDirty;
	}

	public void setTextDirty(boolean textDirty)
	{
		this.textDirty = textDirty;
	}

	public FloatData getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(FloatData fontSize)
	{
		this.fontSize = fontSize;
	}

	public FrameBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer)
	{
		this.buffer = buffer;
	}

	public boolean isScaleWithBufferSize()
	{
		return scaleWithBufferSize;
	}

	public void setScaleWithBufferSize(boolean scaleWithBufferSize)
	{
		this.scaleWithBufferSize = scaleWithBufferSize;
	}

	public int getMaxCharCount()
	{
		return maxCharCount;
	}

	public void setMaxCharCount(int maxCharCount)
	{
		this.maxCharCount = maxCharCount;
	}

	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}

	public HorizontalAlignment getHorizontalAlignment()
	{
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	public FloatData getEdge()
	{
		return edge;
	}

	public void setEdge(FloatData edge)
	{
		this.edge = edge;
	}

	public ColorData getColor2()
	{
		return color2;
	}

	public void setColor2(ColorData color2)
	{
		this.color2 = color2;
	}

	public FloatData getEdge2()
	{
		return edge2;
	}

	public void setEdge2(FloatData edge2)
	{
		this.edge2 = edge2;
	}

	public FloatData getLetterSpacing()
	{
		return letterSpacing;
	}

	public void setLetterSpacing(FloatData letterSpacing)
	{
		this.letterSpacing = letterSpacing;
	}

	@Override
	public Layout getLayout()
	{
		return layout;
	}

	@Override
	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	@Override
	public LayoutOptions getLayoutOptions()
	{
		return layoutOptions;
	}

	@Override
	public void setLayoutOptions(LayoutOptions layoutOptions)
	{
		this.layoutOptions = layoutOptions;
	}

	@Override
	public UIManager getUiManager()
	{
		return uiManager;
	}

	@Override
	public void setUiManager(UIManager uiManager)
	{
		this.uiManager = uiManager;
	}

	@Override
	public boolean isFocusable()
	{
		return focusable;
	}

	@Override
	public void setFocusable(boolean focusable)
	{
		this.focusable = focusable;
	}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	// "Getters/Setters" </editor-fold>
}
