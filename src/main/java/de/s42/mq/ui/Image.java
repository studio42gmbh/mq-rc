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
package de.s42.mq.ui;

import de.s42.dl.DLAttribute.AttributeDL;
import de.s42.dl.annotations.persistence.DontPersistDLAnnotation;
import de.s42.dl.exceptions.DLException;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import de.s42.mq.MQColor;
import de.s42.mq.buffers.FXBuffer;
import de.s42.mq.data.ColorData;
import de.s42.mq.data.Vector2Data;
import de.s42.mq.data.Vector4Data;
import de.s42.mq.materials.Material;
import de.s42.mq.materials.Texture;
import de.s42.mq.materials.Texture.TextureFiltering;
import de.s42.mq.materials.Texture.TextureWrap;
import de.s42.mq.meshes.Quad;
import de.s42.mq.shaders.BasicShader;
import de.s42.mq.shaders.Shader;
import de.s42.mq.ui.actions.UIAction;
import de.s42.mq.ui.layout.Layout;
import de.s42.mq.ui.layout.LayoutOptions;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple image in the scene.
 *
 * @author Benjamin Schiller
 */
public class Image extends Quad implements UIComponent, UIAction
{

	private final static Logger log = LogManager.getLogger(Image.class.getName());

	@DontPersistDLAnnotation.dontPersist
	protected final List<UIAction> actions = new ArrayList<>();

	/**
	 * set at least the path or the texture or the material
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = { "texture", "buffer", "material" })
	protected Path source;

	/**
	 * set at least the path or the texture or the material
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = { "texture", "source", "material" })
	protected FXBuffer buffer;

	/**
	 * set at least the path or the texture or the material
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = RequiredOrDLAnnotation.DEFAULT_SYMBOL, parameters = { "source", "buffer", "material" })
	protected Texture texture;

	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	protected ColorData tint = new ColorData(new MQColor(1.0f));

	@AttributeDL(required = false)
	protected Layout layout;

	/**
	 * x = lefttop, y = righttop, z = rightbottom, w = leftbottom
	 */
	@AttributeDL(required = false)
	//@AnnotationDL(value = EditableDLAnnotation.DEFAULT_SYMBOL)
	//@AnnotationDL(value = MinDLAnnotation.DEFAULT_SYMBOL, parameters = "0.0")
	//@AnnotationDL(value = MaxDLAnnotation.DEFAULT_SYMBOL, parameters = "10000.0")
	//@AnnotationDL(value = StepDLAnnotation.DEFAULT_SYMBOL, parameters = "1.0")
	protected Vector4Data borderRadius = new Vector4Data();

	@AttributeDL(required = false)
	protected Vector2Data dimensionUI = new Vector2Data();

	@AttributeDL(required = false)
	protected LayoutOptions layoutOptions;

	/**
	 * if set to true its generating mipmaps and using LINEAR_MIP_LINEAR etc; smooth = false is good for precise pixel
	 * graphics
	 */
	@AttributeDL(required = false, defaultValue = "true")
	protected boolean smooth = true;

	/**
	 * if set to true layouters shall change the uvs to match the screencoordinates in uv space (left,top = 0,0,
	 * right,bottom = 1,1)
	 */
	@AttributeDL(required = false, defaultValue = "false")
	protected boolean screenUvs = false;

	@AttributeDL(required = false)
	protected UIManager uiManager;

	@AttributeDL(required = false, defaultValue = "true")
	protected boolean focusable = true;

	protected boolean textureLoaded;
	protected float originalWidth;

	@Override
	public Image copy()
	{
		Image copy = (Image) super.copy();

		// @todo finalize proper copying
		copy.uiManager = uiManager;
		copy.source = source;
		copy.buffer = buffer;
		copy.texture = texture;
		copy.layout = layout;
		copy.layoutOptions = (layoutOptions != null) ? layoutOptions.copy() : null;
		copy.smooth = smooth;
		copy.screenUvs = screenUvs;
		copy.borderRadius.setValue(borderRadius.getValue());
		copy.dimensionUI.setValue(dimensionUI.getValue());
		copy.tint.setValue(tint.getValue());
		copy.actions.addAll(actions);

		return copy;
	}

	public void setOptions(ImageOptions options, boolean useIdentifierMaterial)
	{
		assert options != null;

		setTint(options.getTint());
		setSmooth(options.isSmooth());
		setScreenUvs(options.isScreenUvs());
		if (!useIdentifierMaterial) {
			setMaterial(options.getMaterial());
		} else {
			setMaterial(options.getIdentifierMaterial());
		}
	}

	@Override
	public void load() throws DLException
	{
		if (isLoaded()) {
			return;
		}

		originalWidth = getScale().x;

		log.debug("Loading image", this);

		if (uiManager != null) {
			uiManager.register(this);
		}

		// load texture from buffer
		if (buffer != null) {
			texture = buffer.getTexture();
		}

		// load a texture from path
		if (source != null && texture == null) {

			texture = new Texture(source);

			texture.setWrapR(TextureWrap.CLAMP_TO_EDGE);
			texture.setWrapS(TextureWrap.CLAMP_TO_EDGE);
			texture.setWrapT(TextureWrap.CLAMP_TO_EDGE);

			if (isSmooth()) {
				texture.setGenerateMipMap(true);
				texture.setEnableAnisotropic(true);
				texture.setMaxMipLevel(3);
				texture.setMaxLod(3);
				texture.setMinFilter(TextureFiltering.LINEAR_MIPMAP_LINEAR);
				texture.setMagFilter(TextureFiltering.LINEAR);
			} else {
				texture.setGenerateMipMap(false);
				texture.setEnableAnisotropic(false);
				texture.setMaxMipLevel(0);
				texture.setMaxLod(0);
				texture.setMinFilter(TextureFiltering.NEAREST);
				texture.setMagFilter(TextureFiltering.NEAREST);
			}

			texture.load();
			textureLoaded = true;
		}

		//if (isScreenUvs()) {
		setDynamicVertices(true);
		//}

		super.load();
	}

	@Override
	public void unload() throws DLException
	{
		if (!isLoaded()) {
			return;
		}

		log.debug("Unloading image", this);

		if (textureLoaded) {
			texture.unload();
			texture = null;
			textureLoaded = false;
		}

		if (uiManager != null) {
			uiManager.unregister(this);
		}

		super.unload();
	}

	@Override
	public void render()
	{
		// make sure the texture gets set from the buffer
		if (buffer != null) {
			texture = buffer.getTexture();
		}

		//set texture into shader if given BasicShade
		if (texture != null) {

			Material mat = getMaterial();

			assert mat != null;

			Shader shader = mat.getShader();

			//@todo how to make sharing of a texture into shading/materials more generic?
			switch (shader) {
				case BasicShader basicShader -> {
					basicShader.setBaseTexture(texture);
					basicShader.setTint(getTint());
				}
				case ComponentBackgroundShader componentShader ->
					componentShader.setColor(getTint());
				default -> {
				}
			}
		}

		// apply layout if given
		Layout lay = getLayout();
		if (lay != null) {
			lay.layout(this, layoutOptions);
		}

		super.render();
	}

	@Override
	public void handleKey(int key, int scancode, int action, int mods)
	{
		// do nothing
	}

	@Override
	public void handleChar(String chars)
	{
	}

	@Override
	public void handleClick(int x, int y) throws DLException
	{
		doPerform();
	}

	public void addAction(UIAction action)
	{
		assert action != null;

		actions.add(action);
	}

	@Override
	public void addChild(String name, Object child)
	{
		if (child instanceof UIAction action) {
			addAction(action);
		}

		super.addChild(name, child);
	}

	@Override
	public void doPerform() throws DLException
	{
		for (UIAction action : actions) {
			action.doPerform();
		}
	}

	// <editor-fold desc="Getters/Setters" defaultstate="collapsed">
	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public Path getSource()
	{
		return source;
	}

	public void setSource(Path source)
	{
		this.source = source;
	}

	public boolean isTextureLoaded()
	{
		return textureLoaded;
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

	public boolean isSmooth()
	{
		return smooth;
	}

	public void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}

	public FXBuffer getBuffer()
	{
		return buffer;
	}

	public void setBuffer(FXBuffer buffer)
	{
		this.buffer = buffer;
	}

	public boolean isScreenUvs()
	{
		return screenUvs;
	}

	public void setScreenUvs(boolean screenUvs)
	{
		this.screenUvs = screenUvs;
	}

	public ColorData getTint()
	{
		return tint;
	}

	public void setTint(ColorData tint)
	{
		this.tint = tint;
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

	public Vector4Data getBorderRadius()
	{
		return borderRadius;
	}

	public void setBorderRadius(Vector4Data borderRadius)
	{
		this.borderRadius = borderRadius;
	}

	public Vector2Data getDimensionUI()
	{
		return dimensionUI;
	}

	public void setDimensionUI(Vector2Data dimensionUI)
	{
		this.dimensionUI = dimensionUI;
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
	// "Getters/Setters" </editor-fold>
}
